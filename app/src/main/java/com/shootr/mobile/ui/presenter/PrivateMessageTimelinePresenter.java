package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.ShotSent;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageTimeline;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.PrivateMessageChannelModel;
import com.shootr.mobile.ui.model.PrivateMessageModel;
import com.shootr.mobile.ui.model.mappers.PrivateMessageChannelModelMapper;
import com.shootr.mobile.ui.model.mappers.PrivateMessageModelMapper;
import com.shootr.mobile.ui.presenter.interactorwrapper.PrivateMessageChannelTimelineInteractorWrapper;
import com.shootr.mobile.ui.views.PrivateMessageChannelTimelineView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PrivateMessageTimelinePresenter implements Presenter, ShotSent.Receiver {

  private static final long REFRESH_INTERVAL_MILLISECONDS = 10 * 1000;

  private final PrivateMessageChannelTimelineInteractorWrapper timelineInteractorWrapper;
  private final Bus bus;
  private final ErrorMessageFactory errorMessageFactory;
  private final Poller poller;
  private final PrivateMessageModelMapper privateMessageModelMapper;
  private final PrivateMessageChannelModelMapper privateMessageChannelModelMapper;

  private PrivateMessageChannelTimelineView view;
  private String channelId;
  private String idTargetUser;
  private boolean isLoadingOlderShots;
  private boolean mightHaveMoreShots = true;
  private boolean isRefreshing = false;
  private boolean hasBeenPaused = false;
  private boolean isEmpty = true;
  private boolean isFirstShotPosition;
  private boolean isFirstLoad;
  private boolean isTimelineInitialized;
  private Integer newShotsNumber;
  private List<PrivateMessageModel> privateMessageModels;
  private PrivateMessageChannelModel privateMessageChannelModel;

  @Inject public PrivateMessageTimelinePresenter(
      PrivateMessageChannelTimelineInteractorWrapper timelineInteractorWrapper, @Main Bus bus,
      ErrorMessageFactory errorMessageFactory, Poller poller,
      PrivateMessageModelMapper privateMessageModelMapper,
      PrivateMessageChannelModelMapper privateMessageChannelModelMapper) {
    this.timelineInteractorWrapper = timelineInteractorWrapper;
    this.bus = bus;
    this.errorMessageFactory = errorMessageFactory;
    this.poller = poller;
    this.privateMessageModelMapper = privateMessageModelMapper;
    this.privateMessageChannelModelMapper = privateMessageChannelModelMapper;
  }

  public void setView(PrivateMessageChannelTimelineView view) {
    this.view = view;
  }

  public void initialize(PrivateMessageChannelTimelineView streamTimelineView, String idChannel,
      String idTargetUser) {
    this.channelId = idChannel;
    this.idTargetUser = idTargetUser;
    this.newShotsNumber = 0;
    this.privateMessageModels = new ArrayList<>();
    this.setView(streamTimelineView);
    this.loadTimeline(streamTimelineView, idChannel, idTargetUser);
    setupPoller();
  }

  private void setupPoller() {
    this.poller.init(REFRESH_INTERVAL_MILLISECONDS, new Runnable() {
      @Override public void run() {
        loadNewMessages();
      }
    });
  }

  public void loadTimeline(final PrivateMessageChannelTimelineView timelineView, String idChannel,
      String idTargetUser) {

    timelineInteractorWrapper.loadTimeline(idChannel, idTargetUser, hasBeenPaused,
        new Interactor.Callback<PrivateMessageTimeline>() {
          @Override public void onLoaded(PrivateMessageTimeline privateMessageTimeline) {
            if (privateMessageTimeline != null) {
              privateMessageChannelModel = privateMessageChannelModelMapper.transform(
                  privateMessageTimeline.getPrivateMessageChannel());
              privateMessageModels =
                  privateMessageModelMapper.transform(privateMessageTimeline.getPrivateMessages(),
                      privateMessageTimeline.getPrivateMessageChannel().getImage());
              timelineView.setTitle(privateMessageChannelModel.getTitle());
              timelineView.setImage(privateMessageChannelModel.getImageUrl());
              showMessagesInView(privateMessageTimeline);
            }
          }
        });
  }

  private void startPollingShots() {
    poller.startPolling();
  }

  private void stopPollingShots() {
    poller.stopPolling();
  }

  protected void loadTimeline() {
    timelineInteractorWrapper.loadTimeline(channelId, idTargetUser, hasBeenPaused,
        new Interactor.Callback<PrivateMessageTimeline>() {
          @Override public void onLoaded(PrivateMessageTimeline privateMessageTimeline) {
            if (privateMessageTimeline != null) {
              showMessagesInView(privateMessageTimeline);
            }
          }
        });
  }

  private void showMessagesInView(PrivateMessageTimeline timeline) {
    List<PrivateMessageModel> messageModels =
        privateMessageModelMapper.transform(timeline.getPrivateMessages(),
            privateMessageChannelModel.getImageUrl());
    if (isFirstLoad) {
      showFirstLoad(messageModels);
      setTimelineInitialized(messageModels);
      isFirstShotPosition = true;
    } else if (isTimelineInitialized) {
      showFirstLoad(messageModels);
      isTimelineInitialized = false;
      isFirstLoad = false;
    } else {
      handleNewMessages(timeline, messageModels, isFirstShotPosition);
    }
    if (isFirstLoad) {
      loadNewMessages();
    }
  }

  private void setTimelineInitialized(List<PrivateMessageModel> messageModels) {
    if (!messageModels.isEmpty()) {
      isTimelineInitialized = true;
    }
  }

  private void showFirstLoad(List<PrivateMessageModel> privateMessages) {
    privateMessageModels.addAll(privateMessages);
    setShotsWithoutReposition(privateMessages);
  }

  private void handleNewMessages(PrivateMessageTimeline timeline,
      List<PrivateMessageModel> messageModels, Boolean isFirstShotPosition) {
    List<PrivateMessageModel> newMessages = new ArrayList<>();
    checkForNewMessages(messageModels, newMessages);
    if (privateMessageModels.isEmpty()) {
      isFirstShotPosition = true;
    }
    privateMessageModels.addAll(newMessages);
    if (newMessages.isEmpty()) {
      updateMessagesInfo(timeline);
    } else {
      addNewMessages(isFirstShotPosition, newMessages);
    }
  }

  private void checkForNewMessages(List<PrivateMessageModel> messageModels,
      List<PrivateMessageModel> newMessagesModels) {
    for (PrivateMessageModel privateMessageModel : messageModels) {
      if (!privateMessageModels.contains(privateMessageModel)) {
        newMessagesModels.add(privateMessageModel);
      }
    }
  }

  private void addNewMessages(Boolean isFirstShotPosition, List<PrivateMessageModel> newMessages) {
    if (isFirstShotPosition) {
      view.addMessages(newMessages);
    } else {
      addMessagesAbove(newMessages);
      newShotsNumber += newMessages.size();
      showTimeLineIndicator();
    }
  }

  private void showTimeLineIndicator() {
    if (newShotsNumber != null && newShotsNumber > 0) {
      view.showNewShotsIndicator(newShotsNumber);
    } else {
      view.hideNewShotsIndicator();
    }
  }

  private void setShotsWithoutReposition(List<PrivateMessageModel> privateMessageModels) {
    view.setMessages(privateMessageModels);
    isEmpty = privateMessageModels.isEmpty();
    view.hideCheckingForShots();
    handleStreamTimeLineVisibility();
  }

  private void addMessagesAbove(List<PrivateMessageModel> messageModels) {
    view.addAbove(messageModels);
  }

  private void handleStreamTimeLineVisibility() {
    if (isEmpty) {
      view.showEmpty();
      view.hideShots();
    } else {
      view.hideEmpty();
      view.showShots();
      if (isTimelineInitialized) {
        isFirstLoad = false;
      }
    }
  }

  public void refresh() {
    this.loadNewMessages();
  }

  public void showingLastMessage(PrivateMessageModel lastMessage) {
    if (!isLoadingOlderShots && mightHaveMoreShots) {
      handleOlderShotsToLoad(lastMessage);
    }
  }

  private void handleOlderShotsToLoad(PrivateMessageModel privateMessageModel) {
    loadOlderMessages(privateMessageModel.getBirth().getTime());
  }

  private void loadNewMessages() {
    if (handleMessagesRefresh()) {
      return;
    }
    //TODO remove
    long refresTime = (!(privateMessageModels == null || privateMessageModels.isEmpty())
        ? privateMessageModels.get(0).getBirth().getTime() : 0L);

    timelineInteractorWrapper.refreshTimeline(channelId, idTargetUser, refresTime, hasBeenPaused,
        new Interactor.Callback<PrivateMessageTimeline>() {
          @Override public void onLoaded(PrivateMessageTimeline timeline) {
            updateTimelineLiveSettings();
            loadNewMessagesInView(timeline);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            showErrorLoadingNewMessages();
          }
        });
  }

  private void updateTimelineLiveSettings() {
    hasBeenPaused = false;
  }

  private void showErrorLoadingNewMessages() {
    view.showError(errorMessageFactory.getCommunicationErrorMessage());
    view.hideLoading();
    view.hideCheckingForShots();
    isRefreshing = false;
  }

  private boolean handleMessagesRefresh() {
    if (isRefreshing) {
      return true;
    }
    isRefreshing = true;
    if (isEmpty) {
      view.hideEmpty();
      view.showCheckingForShots();
    }
    return false;
  }

  private void loadNewMessagesInView(PrivateMessageTimeline timeline) {
    boolean hasNewMessages = !timeline.getPrivateMessages().isEmpty();
    if (hasNewMessages) {
      loadTimeline();
    } else if (isEmpty) {
      view.showEmpty();
    }
    view.hideLoading();
    view.hideCheckingForShots();
    isRefreshing = false;
  }

  private void loadOlderMessages(long lastShotInScreenDate) {
    loadingOlderShots();

    timelineInteractorWrapper.obtainOlderTimeline(idTargetUser, lastShotInScreenDate,
        new Interactor.Callback<PrivateMessageTimeline>() {
          @Override public void onLoaded(PrivateMessageTimeline timeline) {
            loadOlderMessagesInView(timeline);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            showErrorLoadingOlderMessages();
          }
        });
  }

  private void loadingOlderShots() {
    isLoadingOlderShots = true;
    view.showLoadingOldShots();
  }

  private void showErrorLoadingOlderMessages() {
    view.hideLoadingOldShots();
    view.showError(errorMessageFactory.getCommunicationErrorMessage());
  }

  private void loadOlderMessagesInView(PrivateMessageTimeline timeline) {
    isLoadingOlderShots = false;
    view.hideLoadingOldShots();
    List<PrivateMessageModel> messageModels =
        privateMessageModelMapper.transform(timeline.getPrivateMessages(),
            privateMessageChannelModel.getImageUrl());
    if (!messageModels.isEmpty()) {
      view.addOldMessages(messageModels);
    } else {
      mightHaveMoreShots = false;
    }
  }

  private void updateMessagesInfo(PrivateMessageTimeline timeline) {
    List<PrivateMessageModel> messageModels =
        privateMessageModelMapper.transform(timeline.getPrivateMessages(),
            timeline.getPrivateMessageChannel().getImage());
    view.updateMessagesInfo(messageModels);
  }

  @Subscribe @Override public void onShotSent(ShotSent.Event event) {
    refresh();
  }

  public void setIsFirstShotPosition(Boolean firstPositionVisible) {
    this.isFirstShotPosition = firstPositionVisible;
  }

  public void setNewShotsNumber(Integer newShotsNumber) {
    this.newShotsNumber = newShotsNumber;
  }

  public void setIsFirstLoad(boolean isFirstLoad) {
    this.isFirstLoad = isFirstLoad;
  }


  @Override public void resume() {
    bus.register(this);
    startPollingShots();
    if (hasBeenPaused) {
      isFirstLoad = false;
      isTimelineInitialized = false;
      isFirstShotPosition = false;
      loadNewMessages();
    }
  }

  @Override public void pause() {
    bus.unregister(this);
    stopPollingShots();
    hasBeenPaused = true;
  }
}
