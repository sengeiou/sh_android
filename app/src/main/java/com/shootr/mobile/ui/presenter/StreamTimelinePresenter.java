package com.shootr.mobile.ui.presenter;

import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAdsManager;
import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.ShotSent;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.CallCtaCheckInInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ReshootInteractor;
import com.shootr.mobile.domain.interactor.shot.UndoReshootInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ViewTimelineEventInteractor;
import com.shootr.mobile.domain.interactor.stream.GetConnectionTimesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetNewFilteredShotsInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UpdateStreamInteractor;
import com.shootr.mobile.domain.interactor.timeline.UpdateWatchNumberInteractor;
import com.shootr.mobile.domain.model.shot.Shot;
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
  private static final long CONNECTION_TIMES = 3;

  private final StreamTimelineInteractorsWrapper timelineInteractorWrapper;
  private final SelectStreamInteractor selectStreamInteractor;
  private final MarkNiceShotInteractor markNiceShotInteractor;
  private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
  private final CallCtaCheckInInteractor callCtaCheckInInteractor;
  private final ViewTimelineEventInteractor viewTimelineEventInteractor;
  private final ReshootInteractor reshootInteractor;
  private final UndoReshootInteractor undoReshootInteractor;
  private final ShotModelMapper shotModelMapper;
  private final StreamModelMapper streamModelMapper;
  private final Bus bus;
  private final ErrorMessageFactory errorMessageFactory;
  private final Poller poller;
  private final UpdateWatchNumberInteractor updateWatchNumberInteractor;
  private final UpdateStreamInteractor updateStreamInteractor;
  private final GetNewFilteredShotsInteractor getNewFilteredShotsInteractor;
  private final GetConnectionTimesInteractor getConnectionTimesInteractor;
  private final SessionRepository sessionRepository;
  private final NativeAdsManager adsManager;

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
  private String streamTopic;
  private boolean isInitialized = false;
  private Long lastRefreshDate;
  private SortedSet<ShotModel> shotModels;
  private StreamModel streamModel;
  private Integer streamMode;
  private boolean isReadOnly;
  private boolean isCurrentUserContributor;
  private boolean shouldShowFollowDialog;

  @Inject public StreamTimelinePresenter(StreamTimelineInteractorsWrapper timelineInteractorWrapper,
      SelectStreamInteractor selectStreamInteractor, MarkNiceShotInteractor markNiceShotInteractor,
      UnmarkNiceShotInteractor unmarkNiceShotInteractor,
      CallCtaCheckInInteractor callCtaCheckInInteractor,
      ViewTimelineEventInteractor viewTimelineEventInteractor, ReshootInteractor reshootInteractor,
      UndoReshootInteractor undoReshootInteractor, ShotModelMapper shotModelMapper,
      StreamModelMapper streamModelMapper, @Main Bus bus, ErrorMessageFactory errorMessageFactory,
      Poller poller, UpdateWatchNumberInteractor updateWatchNumberInteractor,
      UpdateStreamInteractor updateStreamInteractor,
      GetNewFilteredShotsInteractor getNewFilteredShotsInteractor,
      GetConnectionTimesInteractor getConnectionTimesInteractor,
      SessionRepository sessionRepository, NativeAdsManager adsManager) {
    this.timelineInteractorWrapper = timelineInteractorWrapper;
    this.selectStreamInteractor = selectStreamInteractor;
    this.markNiceShotInteractor = markNiceShotInteractor;
    this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
    this.callCtaCheckInInteractor = callCtaCheckInInteractor;
    this.viewTimelineEventInteractor = viewTimelineEventInteractor;
    this.reshootInteractor = reshootInteractor;
    this.undoReshootInteractor = undoReshootInteractor;
    this.shotModelMapper = shotModelMapper;
    this.streamModelMapper = streamModelMapper;
    this.bus = bus;
    this.errorMessageFactory = errorMessageFactory;
    this.poller = poller;
    this.updateWatchNumberInteractor = updateWatchNumberInteractor;
    this.updateStreamInteractor = updateStreamInteractor;
    this.getNewFilteredShotsInteractor = getNewFilteredShotsInteractor;
    this.getConnectionTimesInteractor = getConnectionTimesInteractor;
    this.sessionRepository = sessionRepository;
    this.adsManager = adsManager;
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
    if (idAuthor != null) {
      setIdAuthor(idAuthor);
    }
    this.setupConnectionTimes(idStream);
    this.setView(streamTimelineView);
    this.selectStream();
    setupPoller();
  }

  private void setupConnectionTimes(String idStream) {
    getConnectionTimesInteractor.getConnectionTimes(idStream, true,
        new Interactor.Callback<Long>() {
          @Override public void onLoaded(Long times) {
            shouldShowFollowDialog = times == CONNECTION_TIMES;
          }
        });
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

  public boolean isInitialized() {
    return isInitialized;
  }

  protected void setIdAuthor(String idAuthor) {
    this.idAuthor = idAuthor;
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
        postWatchNumberEvent(false);
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

  private void postWatchNumberEvent(boolean localOnly) {
    updateWatchNumberInteractor.updateWatchNumber(localOnly, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
                /* no-op */
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
    if (isFirstLoad) {
      streamTimelineView.showCheckingForShots();
    }
    sendViewTimelineEvent();
    loadTimeline(streamMode);
    selectStreamInteractor.selectStream(streamId, new Interactor.Callback<StreamSearchResult>() {
      @Override public void onLoaded(StreamSearchResult streamSearchResult) {
        StreamModel model = streamModelMapper.transform(streamSearchResult.getStream());
        streamTimelineView.setIsContributor(model.isCurrentUserContributor());
        streamModel = model;
        setupStreamInfo(model);
        postWatchNumberEvent(true);
        handleFilterVisibility(model.getReadWriteMode());
        setStreamMode(model.getReadWriteMode());
        if (shouldShowFollowDialog && !model.isFollowing()) {
          shouldShowFollowDialog = false;
          streamTimelineView.showFollowDialog();
        }
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
                /* no-op */
      }
    });
  }

  private void sendViewTimelineEvent() {
    viewTimelineEventInteractor.countViewEvent(streamId, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        /* no-op */
      }
    });
  }

  private void setupStreamInfo(StreamModel streamModel) {
    setIdAuthor(streamModel.getAuthorId());
    setStreamTopic(streamModel.getTopic());
    isCurrentUserContributor = streamModel.isCurrentUserContributor();
    streamMode = streamModel.getReadWriteMode();
    handleStreamViewOnlyVisibility();
    streamTimelineView.setTitle(streamModel.getTitle());
    streamTimelineView.setVerified(streamModel.isVerifiedUser());
    streamTimelineView.sendAnalythicsEnterTimeline();
    if (streamTopic != null && !streamTopic.isEmpty()) {
      streamTimelineView.showPinnedMessage(streamTopic);
    } else {
      streamTimelineView.hidePinnedMessage();
    }
    if (streamModel.getReadWriteMode() == 0) {
      streamTimelineView.setupFilterShowcase();
    }
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
            if (isFirstLoad && !filterActivated) {
              loadNewShots();
            }
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
    if (isCurrentUserContributor || isCurrentUserStreamAuthor(idUser)) {
      streamTimelineView.hideStreamViewOnlyIndicator();
    } else {
      streamTimelineView.showStreamViewOnlyIndicator();
    }
  }

  private boolean isCurrentUserStreamAuthor(String idUser) {
    return idAuthor != null && idAuthor.equals(idUser);
  }

  private void showShotsInView(Timeline timeline) {
    List<ShotModel> shots = shotModelMapper.transform(timeline.getShots());

    if (hasBeenPaused) {
      streamTimelineView.setShots(shots);
      hasBeenPaused = false;
      return;
    }
    if (isFirstLoad) {
      showFirstLoad(shots);
      setTimelineInitialized(shots);
      isFirstShotPosition = true;
    } else if (isTimelineInitialized) {
      showFirstLoad(shots);
      loadAds();
      isTimelineInitialized = false;
      isFirstLoad = false;
    } else {
      handleNewShots(timeline, shots, isFirstShotPosition);
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
      streamTimelineView.showEmpty(filterActivated);
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

  public void showingLastShot(Object lastShot) {
    if (lastShot instanceof ShotModel) {
      if (!isLoadingOlderShots && mightHaveMoreShots) {
        handleOlderShotsToLoad((ShotModel) lastShot);
      }
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
    if (timeline.isFirstCall()) {
      loadTimeline(streamMode);
    }
    if (isFirstLoad) {
      isFirstLoad = false;
    } else if (hasNewShots) {
      List<ShotModel> newShots = new ArrayList<>(timeline.getShots().size());
      newShots.addAll(shotModelMapper.transform(timeline.getShots()));
      addNewShots(isFirstShotPosition, newShots);
    } else if (isEmpty) {
      streamTimelineView.showEmpty(filterActivated);
    }
    streamTimelineView.hideLoading();
    streamTimelineView.hideCheckingForShots();
    streamTimelineView.showEmpty(filterActivated);
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

  private void updateShotsInfo(Timeline timeline) {
    List<ShotModel> shots = shotModelMapper.transform(timeline.getShots());
    streamTimelineView.updateShotsInfo(shots);
  }

  @Subscribe @Override public void onShotSent(ShotSent.Event event) {
    List<ShotModel> shots = new ArrayList<>(1);
    shots.add(shotModelMapper.transform((Shot) event.getShot()));
    addNewShots(isFirstShotPosition, shots);
  }

  public void reshoot(final ShotModel shotModel) {
    reshootInteractor.reshoot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        streamTimelineView.setReshoot(shotModel.getIdShot(), true);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        streamTimelineView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public void onHoldingShotsClick() {
    showingHolderShots(true);
    streamTimelineView.showCheckingForShots();
    streamTimelineView.hideHoldingShots();
    streamTimelineView.showAllStreamShots();
    streamTimelineView.hideHighlightedShot();
    sessionRepository.setTimelineFilterActivated(true);
    isFirstLoad = true;
    loadTimeline(0);
  }

  public void onAllStreamShotsClick() {
    showingHolderShots(false);
    streamTimelineView.hideAllStreamShots();
    streamTimelineView.showHoldingShots();
    streamTimelineView.showHighlightedShot();
    sessionRepository.setTimelineFilterActivated(false);
    isFirstLoad = true;
    loadTimeline(0);
  }

  public void onShotDeleted(Integer count) {
    if (count <= 0) {
      streamTimelineView.showEmpty(filterActivated);
    } else {
      streamTimelineView.hideEmpty();
    }
  }

  public void setIsFirstShotPosition(Boolean firstPositionVisible) {
    if (firstPositionVisible) {
      newShotsNumber = 0;
    }
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
    topic = trimTopicAndNullWhenEmpty(topic);

    updateStreamInteractor.updateStreamMessage(streamId, topic, notifyMessage,
        new UpdateStreamInteractor.Callback() {
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
    } else {
      streamTimelineView.hidePinnedMessage();
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

  public void undoReshoot(final ShotModel shot) {
    undoReshootInteractor.undoReshoot(shot.getIdShot(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        streamTimelineView.setReshoot(shot.getIdShot(), false);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        streamTimelineView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public Boolean isStrategic() {
    if (streamModel == null) {
      return false;
    }
    return streamModel.isStrategic();
  }

  public void onShotInserted(int number) {
    newShotsNumber += number;
    showTimeLineIndicator();
  }

  private void loadAds() {
    if (streamId != null && streamId.equals("59cccdbec9e77c000c725a72")) {
      AdSettings.addTestDevice("2bb86c4af48c03280c3975fcb69148fb");
      adsManager.setListener(new NativeAdsManager.Listener() {
        @Override public void onAdsLoaded() {
          streamTimelineView.putAds(adsManager);
        }

        @Override public void onAdError(AdError adError) {
        /* no-op */
        }
      });
      adsManager.loadAds();
    }
  }

}
