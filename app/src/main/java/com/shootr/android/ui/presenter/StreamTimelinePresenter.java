package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.DeleteLocalShotsByStream;
import com.shootr.android.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.android.domain.interactor.shot.ShareShotInteractor;
import com.shootr.android.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.android.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.android.domain.interactor.timeline.ReloadStreamTimelineInteractor;
import com.shootr.android.ui.Poller;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.presenter.interactorwrapper.StreamHoldingTimelineInteractorsWrapper;
import com.shootr.android.ui.presenter.interactorwrapper.StreamTimelineInteractorsWrapper;
import com.shootr.android.ui.views.StreamTimelineView;
import com.shootr.android.util.ErrorMessageFactory;
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
        streamTimelineView.showLoading();
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
            }
        });
    }

    public void onAllStreamShotsClick() {
        showingHolderShots(false);
        handleShotsChange();
    }

    private void handleShotsChange() {
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
                        // TODO WUUUT? showErrorLoadingNewShots();
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
