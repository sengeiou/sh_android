package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.ShotSent;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.CallCtaCheckInInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ShareShotInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.stream.CreateStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.GetNewFilteredShotsInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.timeline.UpdateWatchNumberInteractor;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.presenter.interactorwrapper.StreamHoldingTimelineInteractorsWrapper;
import com.shootr.mobile.ui.presenter.interactorwrapper.StreamTimelineInteractorsWrapper;
import com.shootr.mobile.ui.views.StreamTimelineView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.inject.Inject;

public class StreamTimelinePresenter implements Presenter, ShotSent.Receiver {

  private static final long REFRESH_INTERVAL_MILLISECONDS = 10 * 1000;
  private static final long MAX_REFRESH_INTERVAL_MILLISECONDS = 60 * 1000;
  private static final int MAX_LENGTH = 40;
  private static final String SCHEMA = "shootr://";

  private final StreamTimelineInteractorsWrapper timelineInteractorWrapper;
  private final StreamHoldingTimelineInteractorsWrapper streamHoldingTimelineInteractorsWrapper;
  private final SelectStreamInteractor selectStreamInteractor;
  private final MarkNiceShotInteractor markNiceShotInteractor;
  private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
  private final CallCtaCheckInInteractor callCtaCheckInInteractor;
  private final ShareShotInteractor shareShotInteractor;
  private final GetStreamInteractor getStreamInteractor;
  private final ShotModelMapper shotModelMapper;
  private final StreamModelMapper streamModelMapper;
  private final Bus bus;
  private final ErrorMessageFactory errorMessageFactory;
  private final Poller poller;
  private final UpdateWatchNumberInteractor updateWatchNumberInteractor;
  private final CreateStreamInteractor createStreamInteractor;
  private final GetNewFilteredShotsInteractor getNewFilteredShotsInteractor;
  private final SessionRepository sessionRepository;

  private StreamTimelineView streamTimelineView;
  private String streamId;
  private boolean isLoadingOlderShots;
  private boolean mightHaveMoreShots = true;
  private boolean isRefreshing = false;
  private boolean hasBeenPaused = false;
  private boolean isEmpty = true;
  private String idAuthor;
  private boolean filterActivated = false;
  private boolean calledForImportant = false;
  private boolean isFirstShotPosition;
  private boolean isFirstLoad;
  private boolean isTimelineInitialized;
  private Integer newShotsNumber;
  private String streamTitle;
  private String streamDescription;
  private String streamTopic;
  private boolean isInitialized = false;
  private Long lastRefreshDate;
  private SortedSet<ShotModel> shotModels;
  private StreamModel streamModel;
  private Integer streamMode;
  private boolean isReadOnly;
  private boolean isCurrentUserContirbutor;
  private boolean isViewOnlyStream;

  @Inject public StreamTimelinePresenter(StreamTimelineInteractorsWrapper timelineInteractorWrapper,
      StreamHoldingTimelineInteractorsWrapper streamHoldingTimelineInteractorsWrapper,
      SelectStreamInteractor selectStreamInteractor, MarkNiceShotInteractor markNiceShotInteractor,
      UnmarkNiceShotInteractor unmarkNiceShotInteractor,
      CallCtaCheckInInteractor callCtaCheckInInteractor, ShareShotInteractor shareShotInteractor,
      GetStreamInteractor getStreamInteractor, ShotModelMapper shotModelMapper,
      StreamModelMapper streamModelMapper, @Main Bus bus, ErrorMessageFactory errorMessageFactory,
      Poller poller, UpdateWatchNumberInteractor updateWatchNumberInteractor,
      CreateStreamInteractor createStreamInteractor,
      GetNewFilteredShotsInteractor getNewFilteredShotsInteractor,
      SessionRepository sessionRepository) {
    this.timelineInteractorWrapper = timelineInteractorWrapper;
    this.streamHoldingTimelineInteractorsWrapper = streamHoldingTimelineInteractorsWrapper;
    this.selectStreamInteractor = selectStreamInteractor;
    this.markNiceShotInteractor = markNiceShotInteractor;
    this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
    this.callCtaCheckInInteractor = callCtaCheckInInteractor;
    this.shareShotInteractor = shareShotInteractor;
    this.getStreamInteractor = getStreamInteractor;
    this.shotModelMapper = shotModelMapper;
    this.streamModelMapper = streamModelMapper;
    this.bus = bus;
    this.errorMessageFactory = errorMessageFactory;
    this.poller = poller;
    this.updateWatchNumberInteractor = updateWatchNumberInteractor;
    this.createStreamInteractor = createStreamInteractor;
    this.getNewFilteredShotsInteractor = getNewFilteredShotsInteractor;
    this.sessionRepository = sessionRepository;
  }

  public void setView(StreamTimelineView streamTimelineView) {
    isInitialized = true;
    this.streamTimelineView = streamTimelineView;
  }

  public void initialize(StreamTimelineView streamTimelineView, String idStream, String idAuthor,
      Integer streamMode) {
    this.streamId = idStream;
    this.setStreamMode(streamMode);
    this.newShotsNumber = 0;
    this.lastRefreshDate = 0L;
    this.shotModels = new TreeSet<>();
    setIdAuthor(idAuthor);
    this.setView(streamTimelineView);
    this.loadStream(streamTimelineView, idStream);
    this.selectStream();
    setupPoller();
  }

  private void handleFilterVisibility(Integer streamMode) {
    isReadOnly = streamMode != 0;
    if (streamMode == 0) {
      if (sessionRepository.isTimelineFilterActivated()) {
        filterActivated = true;
        this.streamTimelineView.showAllStreamShots();
      } else {
        filterActivated = false;
        this.streamTimelineView.showHoldingShots();
      }
    }
  }

  public void initialize(final StreamTimelineView streamTimelineView, String idStream,
      Integer streamMode) {
    this.streamId = idStream;
    this.setStreamMode(streamMode);
    this.newShotsNumber = 0;
    this.lastRefreshDate = 0L;
    this.shotModels = new TreeSet<>();
    this.setView(streamTimelineView);
    this.loadStream(streamTimelineView, idStream);
    this.selectStream();
    setupPoller();
  }

  public boolean isInitialized() {
    return isInitialized;
  }

  protected void setIdAuthor(String idAuthor) {
    this.idAuthor = idAuthor;
  }

  public void setStreamTitle(String streamTitle) {
    this.streamTitle = streamTitle;
  }

  public void setStreamDescription(String streamDescription) {
    this.streamDescription = streamDescription;
  }

  public void setStreamTopic(String streamTopic) {
    this.streamTopic = streamTopic;
  }

  public String getStreamTopic() {
    return streamTopic;
  }

  protected void showingHolderShots(boolean showingHoldingShots) {
    this.filterActivated = showingHoldingShots;
  }

  private void setupPoller() {
    long intervalSynchroServerResponse = handleIntervalSynchro();
    this.poller.init(intervalSynchroServerResponse, new Runnable() {
      @Override public void run() {
        loadNewShots();
        postWatchNumberEvent();
        changeSynchroTimePoller();
      }
    });
  }

  private long handleIntervalSynchro() {
    int actualSynchroInterval = sessionRepository.getSynchroTime();
    long intervalSynchroServerResponse = actualSynchroInterval * 1000;
    if (intervalSynchroServerResponse < REFRESH_INTERVAL_MILLISECONDS) {
      intervalSynchroServerResponse = REFRESH_INTERVAL_MILLISECONDS;
    } else if (intervalSynchroServerResponse > MAX_REFRESH_INTERVAL_MILLISECONDS) {
      intervalSynchroServerResponse = REFRESH_INTERVAL_MILLISECONDS;
    }
    return intervalSynchroServerResponse;
  }

  private void changeSynchroTimePoller() {
    if (poller.isPolling()) {
      long intervalSynchroServerResponse = handleIntervalSynchro();
      if (intervalSynchroServerResponse != poller.getIntervalMilliseconds()) {
        poller.stopPolling();
        poller.setIntervalMilliseconds(intervalSynchroServerResponse);
        poller.startPolling();
      }
    }
  }

  private void hasNewFilteredShots() {
    getNewFilteredShotsInteractor.hasNewFilteredShots(streamId, new Interactor.Callback<Boolean>() {
      @Override public void onLoaded(Boolean hasNewFilteredShots) {
        if (hasNewFilteredShots) {
          if (!filterActivated) {
            streamTimelineView.showFilterAlert();
          }
        }
      }
    });
  }

  private void postWatchNumberEvent() {
    updateWatchNumberInteractor.updateWatchNumber(new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
                /* no-op */
      }
    });
  }

  public void loadStream(final StreamTimelineView streamTimelineView, String idStream) {
    getStreamInteractor.loadStream(idStream, new GetStreamInteractor.Callback() {
      @Override public void onLoaded(Stream stream) {
        streamModel = streamModelMapper.transform(stream);
        setIdAuthor(stream.getAuthorId());
        setStreamTitle(stream.getTitle());
        setStreamDescription(stream.getDescription());
        setStreamTopic(stream.getTopic());
        isCurrentUserContirbutor = stream.isCurrentUserContributor();
        streamMode = streamModel.getReadWriteMode();
        handleStreamViewOnlyVisibility();
        streamTimelineView.setTitle(stream.getTitle());
        streamTimelineView.sendAnalythicsEnterTimeline();
        if (streamTopic != null && !streamTopic.isEmpty()) {
          streamTimelineView.showPinnedMessage(streamTopic);
        } else {
          streamTimelineView.hidePinnedMessage();
        }
        if (streamModel.getReadWriteMode() == 0) {
          streamTimelineView.setupCheckInShowcase();
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

  protected void selectStream() {
    selectStreamInteractor.selectStream(streamId, new Interactor.Callback<StreamSearchResult>() {
      @Override public void onLoaded(StreamSearchResult streamSearchResult) {
        StreamModel streamModel = streamModelMapper.transform(streamSearchResult.getStream());
        handleFilterVisibility(streamModel.getReadWriteMode());
        setStreamMode(streamModel.getReadWriteMode());
        loadTimeline(streamModel.getReadWriteMode());
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
                /* no-op */
      }
    });
  }

  protected void setStreamMode(Integer mode) {
    streamMode = mode;
  }

  protected void setShotModels(SortedSet<ShotModel> shotModels) {
    this.shotModels = shotModels;
  }

  protected void loadTimeline(Integer readWriteMode) {
    timelineInteractorWrapper.loadTimeline(streamId, filterActivated, hasBeenPaused, readWriteMode,
        new Interactor.Callback<Timeline>() {
          @Override public void onLoaded(Timeline timeline) {
            manageCallImportantShots();
            showShotsInView(timeline);
            handleStreamViewOnlyVisibility();
          }
        });
  }

  private void manageCallImportantShots() {
    if (filterActivated && !calledForImportant) {
      calledForImportant = true;
      timelineInteractorWrapper.obtainImportantShotsTimeline(streamId,
          new Interactor.Callback<Timeline>() {
            @Override public void onLoaded(Timeline timelineFiltered) {
              loadTimeline(0);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {

            }
          });
    }
  }

  private void handleStreamViewOnlyVisibility() {
    if (streamMode != null && streamMode == 0) {
      streamTimelineView.hideStreamViewOnlyIndicator();
    } else {
      loadStreamMode();
    }
  }

  private void loadStreamMode() {
    String idUser = sessionRepository.getCurrentUserId();
    if (isCurrentUserContirbutor || isCurrentUserStreamAuthor(idUser)) {
      streamTimelineView.hideStreamViewOnlyIndicator();
      isViewOnlyStream = false;
    } else {
      streamTimelineView.showStreamViewOnlyIndicator();
      isViewOnlyStream = true;
    }
  }

  private boolean isCurrentUserStreamAuthor(String idUser) {
    return idAuthor != null && idAuthor.equals(idUser);
  }

  private void showShotsInView(Timeline timeline) {
    List<ShotModel> shots = shotModelMapper.transform(timeline.getShots());
    if (isFirstLoad) {
      showFirstLoad(shots);
      setTimelineInitialized(shots);
      isFirstShotPosition = true;
    } else if (isTimelineInitialized) {
      showFirstLoad(shots);
      isTimelineInitialized = false;
      isFirstLoad = false;
    } else {
      handleNewShots(timeline, shots, isFirstShotPosition);
    }
    if (isFirstLoad) {
      loadNewShots();
    }
  }

  private void setTimelineInitialized(List<ShotModel> shots) {
    if (!shots.isEmpty()) {
      isTimelineInitialized = true;
    }
  }

  private void showFirstLoad(List<ShotModel> shots) {
    shotModels.addAll(shots);
    setShotsWithoutReposition(shots);
  }

  private void handleNewShots(Timeline timeline, List<ShotModel> shots,
      Boolean isFirstShotPosition) {
    List<ShotModel> newShots = new ArrayList<>();
    checkForNewShots(shots, newShots);
    shotModels.addAll(newShots);
    if (newShots.isEmpty()) {
      updateShotsInfo(timeline);
    } else {
      addNewShots(isFirstShotPosition, newShots);
    }
  }

  private void checkForNewShots(List<ShotModel> shots, List<ShotModel> newShots) {
    for (ShotModel shot : shots) {
      if (!shotModels.contains(shot)) {
        newShots.add(shot);
      }
    }
  }

  private void addNewShots(Boolean isFirstShotPosition, List<ShotModel> newShots) {
    if (isFirstShotPosition) {
      streamTimelineView.addShots(newShots);
    } else {
      addShotsAbove(newShots);
      newShotsNumber += newShots.size();
      showTimeLineIndicator();
    }
    streamTimelineView.showShots();
  }

  private void showTimeLineIndicator() {
    if (newShotsNumber != null && newShotsNumber > 0) {
      streamTimelineView.showNewShotsIndicator(newShotsNumber);
    } else {
      streamTimelineView.hideNewShotsIndicator();
    }
  }

  private void setShotsWithoutReposition(List<ShotModel> shotModels) {
    streamTimelineView.setShots(shotModels);
    isEmpty = shotModels.isEmpty();
    streamTimelineView.hideCheckingForShots();
    handleStreamTimeLineVisibility();
  }

  private void addShotsAbove(List<ShotModel> shotModels) {
    streamTimelineView.addAbove(shotModels);
  }

  private void handleStreamTimeLineVisibility() {
    if (isEmpty) {
      streamTimelineView.showEmpty();
      streamTimelineView.hideShots();
    } else {
      streamTimelineView.hideEmpty();
      streamTimelineView.showShots();
      if (isTimelineInitialized) {
        isFirstLoad = false;
      }
    }
  }

  public void refresh() {
    this.loadNewShots();
  }

  public void showingLastShot(ShotModel lastShot) {
    if (!isLoadingOlderShots && mightHaveMoreShots) {
      handleOlderShotsToLoad(lastShot);
    }
  }

  private void handleOlderShotsToLoad(ShotModel lastShot) {
    this.loadOlderShots(lastShot.getBirth().getTime());
  }

  private void loadNewShots() {
    if (handleShotsRefresh()) {
      return;
    }
    timelineInteractorWrapper.refreshTimeline(streamId, filterActivated, lastRefreshDate,
        hasBeenPaused, streamMode, new Interactor.Callback<Timeline>() {
          @Override public void onLoaded(Timeline timeline) {
            updateTimelineLiveSettings();
            loadNewShotsInView(timeline);
            if (!isReadOnly && !filterActivated) {
              hasNewFilteredShots();
            }
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            hasBeenPaused = false;
            showErrorLoadingNewShots();
          }
        });
  }

  private void updateTimelineLiveSettings() {
    hasBeenPaused = false;
    lastRefreshDate = new Date().getTime();
  }

  private void showErrorLoadingNewShots() {
    streamTimelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
    streamTimelineView.hideLoading();
    streamTimelineView.hideCheckingForShots();
    isRefreshing = false;
  }

  private boolean handleShotsRefresh() {
    if (isRefreshing) {
      return true;
    }
    isRefreshing = true;
    if (isEmpty) {
      streamTimelineView.hideEmpty();
      streamTimelineView.showCheckingForShots();
    }
    return false;
  }

  private void loadNewShotsInView(Timeline timeline) {
    boolean hasNewShots = !timeline.getShots().isEmpty();
    if (hasNewShots) {
      loadTimeline(streamMode);
    } else if (isEmpty) {
      streamTimelineView.showEmpty();
    }
    streamTimelineView.hideLoading();
    streamTimelineView.hideCheckingForShots();
    isRefreshing = false;
  }

  private void loadOlderShots(long lastShotInScreenDate) {
    loadingOlderShots();
    timelineInteractorWrapper.obtainOlderTimeline(streamId, filterActivated, lastShotInScreenDate,
        streamMode, new Interactor.Callback<Timeline>() {
          @Override public void onLoaded(Timeline timeline) {
            loadOlderShotsInView(timeline);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            showErrorLoadingOlderShots();
          }
        });
  }

  private void loadingOlderShots() {
    isLoadingOlderShots = true;
    streamTimelineView.showLoadingOldShots();
  }

  private void showErrorLoadingOlderShots() {
    streamTimelineView.hideLoadingOldShots();
    streamTimelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
  }

  private void loadOlderShotsInView(Timeline timeline) {
    isLoadingOlderShots = false;
    streamTimelineView.hideLoadingOldShots();
    List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
    if (!shotModels.isEmpty()) {
      streamTimelineView.addOldShots(shotModels);
    } else {
      mightHaveMoreShots = false;
    }
  }

  public void markNiceShot(final ShotModel shotModel) {
    markNiceShotInteractor.markNiceShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        streamTimelineView.renderNice(shotModel);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
                /* no-op */
      }
    });
  }

  public void unmarkNiceShot(final String idShot) {
    unmarkNiceShotInteractor.unmarkNiceShot(idShot, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        streamTimelineView.renderUnnice(idShot);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
                /* no-op */
      }
    });
  }

  private void refreshForUpdatingShotsInfo() {
    timelineInteractorWrapper.loadTimeline(streamId, filterActivated, hasBeenPaused, streamMode,
        new Interactor.Callback<Timeline>() {
          @Override public void onLoaded(Timeline timeline) {
            updateShotsInfo(timeline);
          }
        });
  }

  private void updateShotsInfo(Timeline timeline) {
    List<ShotModel> shots = shotModelMapper.transform(timeline.getShots());
    streamTimelineView.updateShotsInfo(shots);
  }

  @Subscribe @Override public void onShotSent(ShotSent.Event event) {
    refresh();
  }

  public void shareShot(ShotModel shotModel) {
    shareShotInteractor.shareShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        streamTimelineView.showShotShared();
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        streamTimelineView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public void onHoldingShotsClick() {
    showingHolderShots(true);
    streamTimelineView.hideHoldingShots();
    streamTimelineView.showAllStreamShots();
    sessionRepository.setTimelineFilterActivated(true);
    isFirstLoad = true;
    loadTimeline(0);
  }

  public void onAllStreamShotsClick() {
    showingHolderShots(false);
    streamTimelineView.hideAllStreamShots();
    streamTimelineView.showHoldingShots();
    sessionRepository.setTimelineFilterActivated(false);
    isFirstLoad = true;
    loadTimeline(0);
  }

  public void onShotDeleted(Integer count) {
    if (count <= 0) {
      streamTimelineView.showEmpty();
    } else {
      streamTimelineView.hideEmpty();
    }
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

  protected void setOldListSize(Integer oldListSize) {
  }

  public void editStream(String topic) {
    if (topic.isEmpty()) {
      sendStream(topic, false);
    } else {
      streamTimelineView.showPinMessageNotification(topic);
    }
  }

  public void notifyMessage(String topic, Boolean notify) {
    sendStream(topic, notify);
  }

  private void sendStream(String topic, Boolean notifyMessage) {
    String title = filterTitle(streamTitle);
    String description = filterDescription(streamDescription);
    topic = trimTopicAndNullWhenEmpty(topic);

    createStreamInteractor.sendStream(streamId, title, description, streamModel.getReadWriteMode(),
        topic, false, notifyMessage, new CreateStreamInteractor.Callback() {
          @Override public void onLoaded(Stream stream) {
            streamTopic = stream.getTopic();
            if (streamTopic != null && !streamTopic.isEmpty()) {
              streamTimelineView.showPinnedMessage(streamTopic);
            } else {
              streamTimelineView.hidePinnedMessage();
            }
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            streamTimelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
          }
        });
  }

  private String trimTopicAndNullWhenEmpty(String streamTopic) {
    String trimmedText = streamTopic;
    if (trimmedText != null) {
      trimmedText = trimmedText.trim();
      if (trimmedText.isEmpty()) {
        trimmedText = null;
      }
    }
    return trimmedText;
  }

  private String filterTitle(String title) {
    return title.trim();
  }

  private String filterDescription(String streamDescription) {
    if (streamDescription != null) {
      return streamDescription.trim();
    }
    return null;
  }

  public void textChanged(String currentText) {
    updateCharCounter(filterText(currentText));
  }

  private void updateCharCounter(String filteredText) {
    int remainingLength = MAX_LENGTH - filteredText.length();
    streamTimelineView.setRemainingCharactersCount(remainingLength);

    boolean isValidTopic = remainingLength > 0;
    if (isValidTopic) {
      streamTimelineView.setRemainingCharactersColorValid();
    } else {
      streamTimelineView.setRemainingCharactersColorInvalid();
    }
  }

  private String filterText(String originalText) {
    String trimmed = originalText.trim();
    while (trimmed.contains("\n\n\n")) {
      trimmed = trimmed.replace("\n\n\n", "\n\n");
    }
    return trimmed;
  }

  @Override public void resume() {
    bus.register(this);
    startPollingShots();
    if (hasBeenPaused) {
      isFirstLoad = false;
      isTimelineInitialized = false;
      selectStream();
      loadNewShots();
    }
  }

  @Override public void pause() {
    bus.unregister(this);
    stopPollingShots();
    hasBeenPaused = true;
  }

  public void onHidePoll() {
    if (streamModel != null && streamModel.getTopic() != null) {
      streamTimelineView.showPinnedMessage(streamModel.getTopic());
    }
  }

  public void onCtaPressed(ShotModel shotModel) {
    if (shotModel.getCtaButtonLink().startsWith(SCHEMA) && shotModel.getType()
        .equals(ShotType.CTACHECKIN)) {
      callCheckIn(shotModel.getStreamId());
    } else {
      streamTimelineView.storeCtaClickLink(shotModel);
      streamTimelineView.openCtaAction(shotModel.getCtaButtonLink());
    }
  }

  public void onMenuCheckInClick() {
    if (streamModel != null) {
      callCheckIn(streamModel.getIdStream());
    }
  }

  private void callCheckIn(String streamId) {
    callCtaCheckInInteractor.checkIn(streamId, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        streamTimelineView.showChecked();
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        streamTimelineView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public boolean isViewOnlyStream() {
    return isViewOnlyStream;
  }

  public void setViewOnlyStream(boolean viewOnlyStream) {
    isViewOnlyStream = viewOnlyStream;
  }
}
