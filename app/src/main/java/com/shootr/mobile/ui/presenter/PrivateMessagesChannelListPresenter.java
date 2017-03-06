package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.ChannelsBadgeChanged;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShootrValidationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.timeline.privateMessage.GetPrivateMessagesChannelsInteractor;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannel;
import com.shootr.mobile.ui.model.PrivateMessageChannelModel;
import com.shootr.mobile.ui.model.mappers.PrivateMessageChannelModelMapper;
import com.shootr.mobile.ui.views.PrivateMessageChannelListView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.util.List;
import javax.inject.Inject;

public class PrivateMessagesChannelListPresenter implements Presenter {

  private final GetPrivateMessagesChannelsInteractor getPrivateMessagesChannelsInteractor;
  private final PrivateMessageChannelModelMapper mapper;
  private final ErrorMessageFactory errorMessageFactory;
  private final BusPublisher busPublisher;
  private final Bus bus;

  private PrivateMessageChannelListView view;
  private boolean hasBeenPaused = false;
  private List<PrivateMessageChannelModel> privateMessageChannelModels;

  @Inject public PrivateMessagesChannelListPresenter(
      GetPrivateMessagesChannelsInteractor getPrivateMessagesChannelsInteractor,
      PrivateMessageChannelModelMapper mapper, ErrorMessageFactory errorMessageFactory,
      BusPublisher busPublisher, @Main Bus bus) {
    this.getPrivateMessagesChannelsInteractor = getPrivateMessagesChannelsInteractor;
    this.mapper = mapper;
    this.errorMessageFactory = errorMessageFactory;
    this.busPublisher = busPublisher;
    this.bus = bus;
  }

  public void setView(PrivateMessageChannelListView findStreamsView) {
    this.view = findStreamsView;
  }

  public void initialize(final PrivateMessageChannelListView view) {
    this.setView(view);
    loadChannels();
  }

  public void loadChannels() {
    view.showLoading();
    getPrivateMessagesChannelsInteractor.loadChannels(false,
        new Interactor.Callback<List<PrivateMessageChannel>>() {
          @Override public void onLoaded(List<PrivateMessageChannel> result) {
            onLoadResults(result);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            view.hideLoading();
            showViewError(error);
          }
        });
  }

  public void onLoadResults(List<PrivateMessageChannel> resultList) {
    if (!resultList.isEmpty()) {
      privateMessageChannelModels = mapper.transform(resultList);
      renderViewChannelList(privateMessageChannelModels);
      setupUnreads(resultList);
      publishChannelBadge();
    } else {
      this.showViewEmpty();
    }
    view.hideLoading();
  }

  private void setupUnreads(List<PrivateMessageChannel> resultList) {
    int unreads = 0;
    for (PrivateMessageChannel privateMessageChannel : resultList) {
      if (!privateMessageChannel.isRead()) {
        unreads++;
      }
    }
    view.updateTitle(unreads);
  }

  private void publishChannelBadge() {
    busPublisher.post(new ChannelsBadgeChanged.Event());
  }

  private void showViewEmpty() {
    view.showEmpty();
  }

  private void renderViewChannelList(List<PrivateMessageChannelModel> privateMessageChannelModels) {
    view.hideEmpty();
    view.renderChannels(privateMessageChannelModels);
  }

  public void showViewError(ShootrException error) {
    String errorMessage;
    if (error instanceof ShootrValidationException) {
      String errorCode = ((ShootrValidationException) error).getErrorCode();
      errorMessage = errorMessageFactory.getMessageForCode(errorCode);
    } else {
      errorMessage = errorMessageFactory.getMessageForError(error);
    }
    view.showError(errorMessage);
  }

  @Override public void resume() {
    bus.register(this);
    if (hasBeenPaused) {
      loadChannels();
    }
  }

  @Override public void pause() {
    bus.unregister(this);
    hasBeenPaused = true;
  }

  public PrivateMessageChannelListView getView() {
    return view;
  }
}
