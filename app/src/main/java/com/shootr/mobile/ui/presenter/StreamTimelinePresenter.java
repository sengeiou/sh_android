package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.Contributor;
import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.bus.ShotSent;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.DeleteLocalShotsByStream;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ShareShotInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.stream.CreateStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.timeline.ReloadStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.UpdateWatchNumberInteractor;
import com.shootr.mobile.domain.interactor.user.GetContributorsInteractor;
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
  private static final int MAX_LENGTH = 40;

  private final StreamTimelineInteractorsWrapper timelineInteractorWrapper;
  private final StreamHoldingTimelineInteractorsWrapper streamHoldingTimelineInteractorsWrapper;
  private final SelectStreamInteractor selectStreamInteractor;
  private final MarkNiceShotInteractor markNiceShotInteractor;
  private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
  private final ShareShotInteractor shareShotInteractor;
  private final GetStreamInteractor getStreamInteractor;
  private final ShotModelMapper shotModelMapper;
  private final StreamModelMapper streamModelMapper;
  private final Bus bus;
  private final ErrorMessageFactory errorMessageFactory;
  private final Poller poller;
  private final DeleteLocalShotsByStream deleteLocalShotsByStream;
  private final ReloadStreamTimelineInteractor reloadStreamTimelineInteractor;
  private final UpdateWatchNumberInteractor updateWatchNumberInteractor;
  private final CreateStreamInteractor createStreamInteractor;
  private final GetContributorsInteractor getContributorsInteractor;
  private final SessionRepository sessionRepository;

  private StreamTimelineView streamTimelineView;
  private String streamId;
  private boolean isLoadingOlderShots;
  private boolean mightHaveMoreShots = true;
  private boolean isRefreshing = false;
  private boolean hasBeenPaused = false;
  private boolean isEmpty = true;
  private String idAuthor;
  private boolean showingHoldingShots;
  private boolean isFirstShotPosition;
  private boolean isFirstLoad;
  private Integer newShotsNumber;
  private String streamTitle;
  private String streamDescription;
  private String streamTopic;
  private boolean isInitialized = false;
  private Long lastRefreshDate;
  private SortedSet<ShotModel> shotModels;
  private StreamModel streamModel;
  private Integer streamMode;

  @Inject public StreamTimelinePresenter(StreamTimelineInteractorsWrapper timelineInteractorWrapper,
      StreamHoldingTimelineInteractorsWrapper streamHoldingTimelineInteractorsWrapper,
      SelectStreamInteractor selectStreamInteractor, MarkNiceShotInteractor markNiceShotInteractor,
      UnmarkNiceShotInteractor unmarkNiceShotInteractor, ShareShotInteractor shareShotInteractor,
      GetStreamInteractor getStreamInteractor, ShotModelMapper shotModelMapper,
      StreamModelMapper streamModelMapper, @Main Bus bus, ErrorMessageFactory errorMessageFactory,
      Poller poller, DeleteLocalShotsByStream deleteLocalShotsByStream,
      UpdateWatchNumberInteractor updateWatchNumberInteractor,
      ReloadStreamTimelineInteractor reloadStreamTimelineInteractor,
      CreateStreamInteractor createStreamInteractor,
      GetContributorsInteractor getContributorsInteractor, SessionRepository sessionRepository) {
    this.timelineInteractorWrapper = timelineInteractorWrapper;
    this.streamHoldingTimelineInteractorsWrapper = streamHoldingTimelineInteractorsWrapper;
    this.selectStreamInteractor = selectStreamInteractor;
    this.markNiceShotInteractor = markNiceShotInteractor;
    this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
    this.shareShotInteractor = shareShotInteractor;
    this.getStreamInteractor = getStreamInteractor;
    this.shotModelMapper = shotModelMapper;
    this.streamModelMapper = streamModelMapper;
    this.bus = bus;
    this.errorMessageFactory = errorMessageFactory;
    this.poller = poller;
    this.deleteLocalShotsByStream = deleteLocalShotsByStream;
    this.reloadStreamTimelineInteractor = reloadStreamTimelineInteractor;
    this.updateWatchNumberInteractor = updateWatchNumberInteractor;
    this.createStreamInteractor = createStreamInteractor;
    this.getContributorsInteractor = getContributorsInteractor;
    this.sessionRepository = sessionRepository;
  }

  public void setView(StreamTimelineView streamTimelineView) {
    isInitialized = true;
    this.streamTimelineView = streamTimelineView;
  }

  public void initialize(StreamTimelineView streamTimelineView, String idStream, String idAuthor, Integer streamMode) {
    this.streamId = idStream;
    this.setStreamMode(streamMode);
    this.newShotsNumber = 0;
    this.lastRefreshDate = 0L;
    this.shotModels = new TreeSet<>();
    setIdAuthor(idAuthor);
    this.setView(streamTimelineView);
    handleStreamViewOnlyVisibility();
    this.streamTimelineView.showHoldingShots();
    this.loadStream(streamTimelineView, idStream);
    this.selectStream();
    setupPoller();
  }

  public void initialize(final StreamTimelineView streamTimelineView, String idStream, Integer streamMode) {
    this.streamId = idStream;
    this.setStreamMode(streamMode);
    this.newShotsNumber = 0;
    this.lastRefreshDate = 0L;
    this.shotModels = new TreeSet<>();
    this.setView(streamTimelineView);
    this.loadStream(streamTimelineView, idStream);
    this.streamTimelineView.showHoldingShots();
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
    this.showingHoldingShots = showingHoldingShots;
  }

  private void setupPoller() {
    this.poller.init(REFRESH_INTERVAL_MILLISECONDS, new Runnable() {
      @Override public void run() {
        loadNewShots();
        postWatchNumberEvent();
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
        streamTimelineView.setTitle(stream.getTitle());
        if (streamTopic != null && !streamTopic.isEmpty()) {
          streamTimelineView.showPinnedMessage(streamTopic);
        } else {
          streamTimelineView.hidePinnedMessage();
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
    if (!showingHoldingShots) {
      timelineInteractorWrapper.loadTimeline(streamId, hasBeenPaused, readWriteMode,
          new Interactor.Callback<Timeline>() {
            @Override public void onLoaded(Timeline timeline) {
              showShotsInView(timeline);
              handleStreamViewOnlyVisibility();
            }
          });
    } else {
      streamHoldingTimelineInteractorsWrapper.loadTimeline(streamId, idAuthor, hasBeenPaused,
          new Interactor.Callback<Timeline>() {
            @Override public void onLoaded(Timeline timeline) {
              showShotsInView(timeline);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              showErrorLoadingNewShots();
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
    getContributorsInteractor.obtainContributors(streamId, false,
        new Interactor.Callback<List<Contributor>>() {
          @Override public void onLoaded(List<Contributor> contributors) {
            String idUser = sessionRepository.getCurrentUserId();
            if (isCurrentUserContributor(contributors, idUser) || isCurrentUserStreamAuthor(
                idUser)) {
              streamTimelineView.hideStreamViewOnlyIndicator();
            } else {
              streamTimelineView.showStreamViewOnlyIndicator();
            }
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {

          }
        });
  }

  private boolean isCurrentUserStreamAuthor(String idUser) {
    return idAuthor != null && idAuthor.equals(idUser);
  }

  private boolean isCurrentUserContributor(List<Contributor> contributors, String idUser) {
    for (Contributor contributor : contributors) {
      if (contributor.getIdUser().equals(idUser)) {
        return true;
      }
    }
    return false;
  }

  private void showShotsInView(Timeline timeline) {
    List<ShotModel> shots = shotModelMapper.transform(timeline.getShots());
    if (isFirstLoad || isFirstShotPosition) {
      shotModels.addAll(shots);
      setShotsWithoutReposition(shots);
    }

    if (!isFirstShotPosition && !isFirstLoad) {
      List<ShotModel> newShots = new ArrayList<>();
      for (ShotModel shot : shots) {
        if (!shotModels.contains(shot)) {
          newShots.add(shot);
        }
      }
      shotModels.addAll(newShots);
      if (newShots.isEmpty()) {
        updateShotsInfo(timeline);
      } else {
        addShotsAbove(newShots);
      }
      showTimeLineIndicator();
    }
    loadNewShots();
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
      isFirstLoad = false;
    }
  }

  public void refresh() {
    if (showingHoldingShots) {
      this.loadHolderNewShots();
    } else {
      this.loadNewShots();
    }
  }

  public void showingLastShot(ShotModel lastShot) {
    if (!isLoadingOlderShots && mightHaveMoreShots) {
      handleOlderShotsToLoad(lastShot);
    }
  }

  private void handleOlderShotsToLoad(ShotModel lastShot) {
    if (showingHoldingShots) {
      this.loadHolderOlderShots(lastShot.getBirth().getTime());
    } else {
      this.loadOlderShots(lastShot.getBirth().getTime());
    }
  }

  private void loadHolderNewShots() {
    if (handleShotsRefresh()) {
      return;
    }
    streamHoldingTimelineInteractorsWrapper.refreshTimeline(streamId, idAuthor, lastRefreshDate,
        hasBeenPaused, new Interactor.Callback<Timeline>() {
          @Override public void onLoaded(Timeline timeline) {
            loadNewShotsInView(timeline);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            showErrorLoadingNewShots();
          }
        });
  }

  private void loadNewShots() {
    if (handleShotsRefresh()) {
      return;
    }
    if (!showingHoldingShots) {
      timelineInteractorWrapper.refreshTimeline(streamId, lastRefreshDate, hasBeenPaused,
          streamMode, new Interactor.Callback<Timeline>() {
            @Override public void onLoaded(Timeline timeline) {
              updateTimelineLiveSettings();
              loadNewShotsInView(timeline);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              hasBeenPaused = false;
              showErrorLoadingNewShots();
            }
          });
    } else {
      streamHoldingTimelineInteractorsWrapper.refreshTimeline(streamId, idAuthor, lastRefreshDate,
          hasBeenPaused, new Interactor.Callback<Timeline>() {
            @Override public void onLoaded(Timeline timeline) {
              updateTimelineLiveSettings();
              loadNewShotsInView(timeline);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              hasBeenPaused = false;
              showErrorLoadingNewShots();
            }
          });
    }
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
      newShotsNumber += timeline.getShots().size();
      loadTimeline(streamMode);
    } else if (isEmpty) {
      streamTimelineView.showEmpty();
    }
    streamTimelineView.hideLoading();
    streamTimelineView.hideCheckingForShots();
    isRefreshing = false;
  }

  private void loadHolderOlderShots(long lastShotInScreenDate) {
    loadingOlderShots();
    streamHoldingTimelineInteractorsWrapper.obtainOlderTimeline(lastShotInScreenDate, idAuthor,
        new Interactor.Callback<Timeline>() {
          @Override public void onLoaded(Timeline timeline) {
            loadOlderShotsInView(timeline);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            showErrorLoadingOlderShots();
          }
        });
  }

  private void loadOlderShots(long lastShotInScreenDate) {
    loadingOlderShots();
    timelineInteractorWrapper.obtainOlderTimeline(lastShotInScreenDate, streamMode,
        new Interactor.Callback<Timeline>() {
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

  public void markNiceShot(String idShot) {
    markNiceShotInteractor.markNiceShot(idShot, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        refreshForUpdatingShotsInfo();
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
                /* no-op */
      }
    });
  }

  public void unmarkNiceShot(String idShot) {
    unmarkNiceShotInteractor.unmarkNiceShot(idShot, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        refreshForUpdatingShotsInfo();
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
                /* no-op */
      }
    });
  }

  private void refreshForUpdatingShotsInfo() {
    if (!showingHoldingShots) {
      timelineInteractorWrapper.loadTimeline(streamId, hasBeenPaused, streamMode,
          new Interactor.Callback<Timeline>() {
            @Override public void onLoaded(Timeline timeline) {
              updateShotsInfo(timeline);
            }
          });
    } else {
      streamHoldingTimelineInteractorsWrapper.loadTimeline(streamId, idAuthor, hasBeenPaused,
          new Interactor.Callback<Timeline>() {
            @Override public void onLoaded(Timeline timeline) {
              updateShotsInfo(timeline);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              showErrorLoadingNewShots();
            }
          });
    }
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
    streamTimelineView.showLoading();
    streamHoldingTimelineInteractorsWrapper.loadTimeline(streamId, idAuthor, hasBeenPaused,
        new Interactor.Callback<Timeline>() {
          @Override public void onLoaded(Timeline timeline) {
            List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
            streamTimelineView.setShots(shotModels);
            isEmpty = shotModels.isEmpty();
            streamTimelineView.hideCheckingForShots();
            if (isEmpty) {
              streamTimelineView.showEmpty();
              streamTimelineView.hideShots();
            } else {
              streamTimelineView.hideEmpty();
              streamTimelineView.showShots();
            }
            streamTimelineView.hideHoldingShots();
            streamTimelineView.showAllStreamShots();
            streamTimelineView.hideLoading();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            showErrorLoadingNewShots();
          }
        });
  }

  public void onAllStreamShotsClick() {
    showingHolderShots(false);
    handleShotsChange();
  }

  private void handleShotsChange() {
    streamTimelineView.showLoading();
    deleteLocalShotsByStream.deleteShot(streamId, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        reloadStreamTimelineInteractor.loadStreamTimeline(streamId,
            new Interactor.Callback<Timeline>() {
              @Override public void onLoaded(Timeline timeline) {
                loadNewShotsInView(timeline);
                streamTimelineView.showHoldingShots();
                streamTimelineView.hideAllStreamShots();
              }
            }, new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                showErrorLoadingNewShots();
              }
            });
      }
    });
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

  private void handleVisibilityTimelineIndicatorInResume() {
    streamTimelineView.hideNewShotsIndicator();
  }

  public void editStream(String topic) {
    if (topic.isEmpty()) {
      sendStream(topic, false);
    } else {
      streamTimelineView.showPinMessageNotification(streamModel);
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
      selectStream();
      handleVisibilityTimelineIndicatorInResume();
    }
  }

  @Override public void pause() {
    bus.unregister(this);
    stopPollingShots();
    hasBeenPaused = true;
  }
}
