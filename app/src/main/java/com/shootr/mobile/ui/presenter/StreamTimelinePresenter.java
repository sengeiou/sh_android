package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
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
import com.shootr.mobile.domain.interactor.stream.GetStreamInfoInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.timeline.ReloadStreamTimelineInteractor;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.presenter.interactorwrapper.StreamHoldingTimelineInteractorsWrapper;
import com.shootr.mobile.ui.presenter.interactorwrapper.StreamTimelineInteractorsWrapper;
import com.shootr.mobile.ui.views.StreamTimelineView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class StreamTimelinePresenter implements Presenter, ShotSent.Receiver {

    private static final long REFRESH_INTERVAL_MILLISECONDS = 10 * 1000;
    private static final int MAX_LENGTH = 140;

    private final StreamTimelineInteractorsWrapper timelineInteractorWrapper;
    private final StreamHoldingTimelineInteractorsWrapper streamHoldingTimelineInteractorsWrapper;
    private final SelectStreamInteractor selectStreamInteractor;
    private final MarkNiceShotInteractor markNiceShotInteractor;
    private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    private final ShareShotInteractor shareShotInteractor;
    private final GetStreamInteractor getStreamInteractor;
    private final ShotModelMapper shotModelMapper;
    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;
    private final Poller poller;
    private final DeleteLocalShotsByStream deleteLocalShotsByStream;
    private final ReloadStreamTimelineInteractor reloadStreamTimelineInteractor;
    private final CreateStreamInteractor createStreamInteractor;

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
    private Integer oldListSize;
    private Integer newShotsNumber;
    private String streamTitle;
    private String streamDescription;
    private String streamSubTitle;
    private String streamTopic;
    private boolean isInitialized = false;
    private String currentTextWritten = "";

    @Inject public StreamTimelinePresenter(StreamTimelineInteractorsWrapper timelineInteractorWrapper,
      StreamHoldingTimelineInteractorsWrapper streamHoldingTimelineInteractorsWrapper,
      SelectStreamInteractor selectStreamInteractor, MarkNiceShotInteractor markNiceShotInteractor,
      UnmarkNiceShotInteractor unmarkNiceShotInteractor, ShareShotInteractor shareShotInteractor,
      GetStreamInteractor getStreamInteractor, ShotModelMapper shotModelMapper, @Main Bus bus,
      ErrorMessageFactory errorMessageFactory, Poller poller, DeleteLocalShotsByStream deleteLocalShotsByStream,
      ReloadStreamTimelineInteractor reloadStreamTimelineInteractor, CreateStreamInteractor createStreamInteractor) {
        this.timelineInteractorWrapper = timelineInteractorWrapper;
        this.streamHoldingTimelineInteractorsWrapper = streamHoldingTimelineInteractorsWrapper;
        this.selectStreamInteractor = selectStreamInteractor;
        this.markNiceShotInteractor = markNiceShotInteractor;
        this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
        this.shareShotInteractor = shareShotInteractor;
        this.getStreamInteractor = getStreamInteractor;
        this.shotModelMapper = shotModelMapper;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
        this.poller = poller;
        this.deleteLocalShotsByStream = deleteLocalShotsByStream;
        this.reloadStreamTimelineInteractor = reloadStreamTimelineInteractor;
        this.createStreamInteractor = createStreamInteractor;
    }

    public void setView(StreamTimelineView streamTimelineView) {
        isInitialized = true;
        this.streamTimelineView = streamTimelineView;
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

    public void setStreamSubTitle(String streamSubTitle) {
        this.streamSubTitle = streamSubTitle;
    }

    protected void showingHolderShots(boolean showingHoldingShots) {
        this.showingHoldingShots = showingHoldingShots;
    }

    public void initialize(StreamTimelineView streamTimelineView, String idStream, String idAuthor) {
        this.streamId = idStream;
        this.oldListSize = 0;
        this.newShotsNumber = 0;
        setIdAuthor(idAuthor);
        this.setView(streamTimelineView);
        this.streamTimelineView.showHoldingShots();
        this.loadStream(streamTimelineView, idStream);
        this.selectStream();
        this.poller.init(REFRESH_INTERVAL_MILLISECONDS, new Runnable() {
            @Override public void run() {
                loadNewShots();
            }
        });
    }

    public void initialize(final StreamTimelineView streamTimelineView, String idStream) {
        this.streamId = idStream;
        this.oldListSize = 0;
        this.newShotsNumber = 0;
        this.setView(streamTimelineView);
        this.loadStream(streamTimelineView, idStream);
        this.streamTimelineView.showHoldingShots();
        this.selectStream();
        this.poller.init(REFRESH_INTERVAL_MILLISECONDS, new Runnable() {
            @Override public void run() {
                loadNewShots();
            }
        });
    }

    public void loadStream(final StreamTimelineView streamTimelineView, String idStream) {
        getStreamInteractor.loadStream(idStream, new GetStreamInteractor.Callback() {
            @Override public void onLoaded(Stream stream) {
                setIdAuthor(stream.getAuthorId());
                setStreamTitle(stream.getTitle());
                setStreamSubTitle(stream.getShortTitle());
                setStreamDescription(stream.getDescription());
                setStreamTopic(stream.getTopic());
                streamTimelineView.setTitle(stream.getShortTitle());
                if (streamTopic != null && !streamTopic.isEmpty()) {
                    streamTimelineView.showTopicSnackBar(streamTopic);
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
        loadTimeline();
        selectStreamInteractor.selectStream(streamId, new Interactor.Callback<StreamSearchResult>() {
            @Override public void onLoaded(StreamSearchResult streamSearchResult) {
                /* no-op */
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                /* no-op */
            }
        });
    }

    protected void loadTimeline() {
        if (!showingHoldingShots) {
            timelineInteractorWrapper.loadTimeline(streamId, new Interactor.Callback<Timeline>() {
                @Override public void onLoaded(Timeline timeline) {
                    showShotsInView(timeline);
                }
            });
        } else {
            streamHoldingTimelineInteractorsWrapper.loadTimeline(streamId,
              idAuthor,
              new Interactor.Callback<Timeline>() {
                  @Override public void onLoaded(Timeline timeline) {
                      showShotsInView(timeline);
                  }
              },
              new Interactor.ErrorCallback() {
                  @Override public void onError(ShootrException error) {
                      showErrorLoadingNewShots();
                  }
              });
        }
    }

    private void showShotsInView(Timeline timeline) {
        List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
        if (isFirstLoad || isFirstShotPosition) {
            setShotsWithoutReposition(shotModels);
        }

        if (!isFirstShotPosition && !isFirstLoad) {
            setShotsAndReposition(shotModels);
            calculeNewShotsNumberAndShowTimeLineIndicator(shotModels);
        }
        oldListSize = shotModels.size();
        loadNewShots();
    }

    private void calculeNewShotsNumberAndShowTimeLineIndicator(List<ShotModel> shotModels) {
        newShotsNumber += Math.abs(oldListSize - shotModels.size());
        if (newShotsNumber != null && newShotsNumber > 0) {
            streamTimelineView.showTimelineIndicator(newShotsNumber);
        } else {
            streamTimelineView.hideTimelineIndicator();
        }
    }

    private void setShotsWithoutReposition(List<ShotModel> shotModels) {
        streamTimelineView.setShots(shotModels);
        isEmpty = shotModels.isEmpty();
        streamTimelineView.hideCheckingForShots();
        handleStreamTimeLineVisibility();
    }

    private void setShotsAndReposition(List<ShotModel> shotModels) {
        streamTimelineView.setShots(shotModels);
        streamTimelineView.setPosition(oldListSize, shotModels.size());
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
        if (handleShotsRefresh()) return;
        streamHoldingTimelineInteractorsWrapper.refreshTimeline(streamId,
          idAuthor,
          new Interactor.Callback<Timeline>() {
              @Override public void onLoaded(Timeline timeline) {
                  loadNewShotsInView(timeline);
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  showErrorLoadingNewShots();
              }
          });
    }

    private void loadNewShots() {
        if (handleShotsRefresh()) return;
        if (!showingHoldingShots) {
            timelineInteractorWrapper.refreshTimeline(streamId, new Interactor.Callback<Timeline>() {
                @Override public void onLoaded(Timeline timeline) {
                    loadNewShotsInView(timeline);
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    showErrorLoadingNewShots();
                }
            });
        } else {
            streamHoldingTimelineInteractorsWrapper.refreshTimeline(streamId,
              idAuthor,
              new Interactor.Callback<Timeline>() {
                  @Override public void onLoaded(Timeline timeline) {
                      loadNewShotsInView(timeline);
                  }
              },
              new Interactor.ErrorCallback() {
                  @Override public void onError(ShootrException error) {
                      showErrorLoadingNewShots();
                  }
              });
        }
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
            loadTimeline();
        } else if (isEmpty) {
            streamTimelineView.showEmpty();
        }
        streamTimelineView.hideLoading();
        streamTimelineView.hideCheckingForShots();
        isRefreshing = false;
    }

    private void loadHolderOlderShots(long lastShotInScreenDate) {
        loadingOlderShots();
        streamHoldingTimelineInteractorsWrapper.obtainOlderTimeline(lastShotInScreenDate,
          idAuthor,
          new Interactor.Callback<Timeline>() {
              @Override public void onLoaded(Timeline timeline) {
                  loadOlderShotsInView(timeline);
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  showErrorLoadingOlderShots();
              }
          });
    }

    private void loadOlderShots(long lastShotInScreenDate) {
        loadingOlderShots();
        timelineInteractorWrapper.obtainOlderTimeline(lastShotInScreenDate, new Interactor.Callback<Timeline>() {
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
                loadTimeline();
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
                loadTimeline();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                /* no-op */
            }
        });
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
        streamHoldingTimelineInteractorsWrapper.loadTimeline(streamId, idAuthor, new Interactor.Callback<Timeline>() {
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
                reloadStreamTimelineInteractor.loadStreamTimeline(streamId, new Interactor.Callback<Timeline>() {
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
        this.oldListSize = oldListSize;
    }

    @Override public void resume() {
        bus.register(this);
        startPollingShots();
        if (hasBeenPaused) {
            loadTimeline();
            selectStream();
            handleVisibilityTimelineIndicatorInResume();
        }
    }

    private void handleVisibilityTimelineIndicatorInResume() {
        streamTimelineView.hideTimelineIndicator();
    }

    @Override public void pause() {
        bus.unregister(this);
        stopPollingShots();
        hasBeenPaused = true;
    }

    public void editStream(String topic) {
        sendStream(topic);
    }

    private void sendStream(String topic) {
        String title = filterTitle(streamTitle);
        String shortTitle = filterShortTitle(streamSubTitle);
        String description = filterDescription(streamDescription);

        createStreamInteractor.sendStreamWithTopic(streamId,
          title,
          shortTitle,
          description,
          topic,
          false,
          new CreateStreamInteractor.Callback() {
              @Override public void onLoaded(Stream stream) {
                  streamTopic = stream.getTopic();
                  if (streamTopic!= null && !streamTopic.isEmpty()) {
                      streamTimelineView.showTopicSnackBar(streamTopic);
                  } else {
                      streamTimelineView.hideTopicSnackBar();
                  }
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  streamTimelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
              }
          });
    }

    private String filterTitle(String title) {
        return title.trim();
    }

    private String filterShortTitle(String shortTitle) {
        if (shortTitle.length() <= 20) {
            return shortTitle.trim();
        } else {
            return shortTitle.substring(0, 20).trim();
        }
    }

    private String filterDescription(String streamDescription) {
        if (streamDescription != null) {
            return streamDescription.trim();
        }
        return " ";
    }

    public void textChanged(String currentText) {
        currentTextWritten = filterText(currentText);
        updateCharCounter(currentTextWritten);
    }

    private void updateCharCounter(String filteredText) {
        int remainingLength = MAX_LENGTH - filteredText.length();
        streamTimelineView.setRemainingCharactersCount(remainingLength);

        boolean isValidShot = remainingLength > 0;
        if (isValidShot) {
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
}
