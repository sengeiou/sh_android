package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.bus.ShotSent;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.DeleteLocalShotsByStream;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ShareShotInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
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

    private final StreamTimelineInteractorsWrapper timelineInteractorWrapper;
    private final StreamHoldingTimelineInteractorsWrapper streamHoldingTimelineInteractorsWrapper;
    private final SelectStreamInteractor selectStreamInteractor;
    private final MarkNiceShotInteractor markNiceShotInteractor;
    private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    private final ShareShotInteractor shareShotInteractor;
    private final ShotModelMapper shotModelMapper;
    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;
    private final Poller poller;
    private final DeleteLocalShotsByStream deleteLocalShotsByStream;
    private final ReloadStreamTimelineInteractor reloadStreamTimelineInteractor;

    private StreamTimelineView streamTimelineView;
    private String streamId;
    private boolean isLoadingOlderShots;
    private boolean mightHaveMoreShots = true;
    private boolean isRefreshing = false;
    private boolean hasBeenPaused = false;
    private boolean isEmpty = true;
    private String idAuthor;
    private boolean showingHoldingShots;

    @Inject public StreamTimelinePresenter(StreamTimelineInteractorsWrapper timelineInteractorWrapper,
      StreamHoldingTimelineInteractorsWrapper streamHoldingTimelineInteractorsWrapper, SelectStreamInteractor selectStreamInteractor, MarkNiceShotInteractor markNiceShotInteractor,
      UnmarkNiceShotInteractor unmarkNiceShotInteractor, ShareShotInteractor shareShotInteractor, ShotModelMapper shotModelMapper,
      @Main Bus bus, ErrorMessageFactory errorMessageFactory, Poller poller,
      DeleteLocalShotsByStream deleteLocalShotsByStream,
      ReloadStreamTimelineInteractor reloadStreamTimelineInteractor) {
        this.timelineInteractorWrapper = timelineInteractorWrapper;
        this.streamHoldingTimelineInteractorsWrapper = streamHoldingTimelineInteractorsWrapper;
        this.selectStreamInteractor = selectStreamInteractor;
        this.markNiceShotInteractor = markNiceShotInteractor;
        this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
        this.shareShotInteractor = shareShotInteractor;
        this.shotModelMapper = shotModelMapper;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
        this.poller = poller;
        this.deleteLocalShotsByStream = deleteLocalShotsByStream;
        this.reloadStreamTimelineInteractor = reloadStreamTimelineInteractor;
    }

    public void setView(StreamTimelineView streamTimelineView) {
        this.streamTimelineView = streamTimelineView;
    }

    protected void setIdAuthor(String idAuthor) {
        this.idAuthor = idAuthor;
    }

    protected void showingHolderShots(boolean showingHoldingShots) {
        this.showingHoldingShots = showingHoldingShots;
    }

    public void initialize(StreamTimelineView streamTimelineView, String idStream, String idAuthor) {
        this.streamId = idStream;
        setIdAuthor(idAuthor);
        this.setView(streamTimelineView);
        this.streamTimelineView.showHoldingShots();
        this.selectStream();
        this.poller.init(REFRESH_INTERVAL_MILLISECONDS, new Runnable() {
            @Override public void run() {
                loadNewShots();
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
              }, new Interactor.ErrorCallback() {
                  @Override public void onError(ShootrException error) {
                      showErrorLoadingNewShots();
                  }
              });
        }
    }

    private void showShotsInView(Timeline timeline) {
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
        loadNewShots();
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
        }else if (isEmpty) {
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
        });
    }

    public void unmarkNiceShot(String idShot) {
        unmarkNiceShotInteractor.unmarkNiceShot(idShot, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadTimeline();
            }
        });

    }

    @Subscribe
    @Override public void onShotSent(ShotSent.Event event) {
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

    @Override public void resume() {
        bus.register(this);
        startPollingShots();
        if (hasBeenPaused) {
            loadTimeline();
            selectStream();
        }
    }

    @Override public void pause() {
        bus.unregister(this);
        stopPollingShots();
        hasBeenPaused = true;
    }
}
