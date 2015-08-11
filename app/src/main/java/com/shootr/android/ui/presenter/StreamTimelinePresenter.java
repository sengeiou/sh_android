package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.android.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.android.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.android.ui.Poller;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
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
    private final SelectStreamInteractor selectStreamInteractor;
    private final MarkNiceShotInteractor markNiceShotInteractor;
    private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    private final ShotModelMapper shotModelMapper;
    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;
    private final Poller poller;

    private StreamTimelineView streamTimelineView;
    private String streamId;
    private boolean isLoadingOlderShots;
    private boolean mightHaveMoreShots = true;

    @Inject public StreamTimelinePresenter(StreamTimelineInteractorsWrapper timelineInteractorWrapper,
      SelectStreamInteractor selectStreamInteractor,
      MarkNiceShotInteractor markNiceShotInteractor,
      UnmarkNiceShotInteractor unmarkNiceShotInteractor,
      ShotModelMapper shotModelMapper,
      @Main Bus bus,
      ErrorMessageFactory errorMessageFactory,
      Poller poller) {
        this.timelineInteractorWrapper = timelineInteractorWrapper;
        this.selectStreamInteractor = selectStreamInteractor;
        this.markNiceShotInteractor = markNiceShotInteractor;
        this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
        this.shotModelMapper = shotModelMapper;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
        this.poller = poller;
    }

    public void setView(StreamTimelineView streamTimelineView) {
        this.streamTimelineView = streamTimelineView;
    }

    public void initialize(StreamTimelineView streamTimelineView, String streamId) {
        this.streamId = streamId;
        this.setView(streamTimelineView);
        this.selectStream();
        this.poller.init(REFRESH_INTERVAL_MILLISECONDS, new Runnable() {
            @Override
            public void run() {
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
        selectStreamInteractor.selectStream(streamId, new Interactor.Callback<StreamSearchResult>() {
            @Override
            public void onLoaded(StreamSearchResult streamSearchResult) {
                loadTimeline();
            }
        });
    }

    protected void loadTimeline() {
        streamTimelineView.showLoading();
        timelineInteractorWrapper.loadTimeline(new Interactor.Callback<Timeline>() {
            @Override
            public void onLoaded(Timeline timeline) {
                List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
                streamTimelineView.hideLoading();
                streamTimelineView.setShots(shotModels);
                if (!shotModels.isEmpty()) {
                    streamTimelineView.hideEmpty();
                    streamTimelineView.showShots();
                } else {
                    streamTimelineView.showEmpty();
                    streamTimelineView.hideShots();
                }
                loadNewShots();
            }
        }, new Interactor.ErrorCallback() {
            @Override
            public void onError(ShootrException error) {
                streamTimelineView.hideLoading();
                streamTimelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
            }
        });
    }

    public void refresh() {
        streamTimelineView.showLoading();
        this.loadNewShots();
    }

    public void showingLastShot(ShotModel lastShot) {
        if (!isLoadingOlderShots && mightHaveMoreShots) {
            this.loadOlderShots(lastShot.getBirth().getTime());
        }
    }

    private void loadNewShots() {
        timelineInteractorWrapper.refreshTimeline(new Interactor.Callback<Timeline>() {
            @Override
            public void onLoaded(Timeline timeline) {
                List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
                if (!shotModels.isEmpty()) {
                    streamTimelineView.addNewShots(shotModels);
                    streamTimelineView.hideEmpty();
                    streamTimelineView.showShots();
                }
                streamTimelineView.hideLoading();
            }
        }, new Interactor.ErrorCallback() {
            @Override
            public void onError(ShootrException error) {
                streamTimelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
                streamTimelineView.hideLoading();
            }
        });
    }

    private void loadOlderShots(long lastShotInScreenDate) {
        isLoadingOlderShots = true;
        streamTimelineView.showLoadingOldShots();
        timelineInteractorWrapper.obtainOlderTimeline(lastShotInScreenDate, new Interactor.Callback<Timeline>() {
            @Override
            public void onLoaded(Timeline timeline) {
                isLoadingOlderShots = false;
                streamTimelineView.hideLoadingOldShots();
                List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
                if (!shotModels.isEmpty()) {
                    streamTimelineView.addOldShots(shotModels);
                } else {
                    mightHaveMoreShots = false;
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override
            public void onError(ShootrException error) {
                streamTimelineView.hideLoadingOldShots();
                streamTimelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
            }
        });
    }

    public void markNiceShot(String idShot) {
        markNiceShotInteractor.markNiceShot(idShot, new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                loadTimeline();
            }
        });
    }

    public void unmarkNiceShot(String idShot) {
        unmarkNiceShotInteractor.unmarkNiceShot(idShot, new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                loadTimeline();
            }
        });

    }

    @Override public void resume() {
        bus.register(this);
        loadTimeline();
        startPollingShots();
    }

    @Override public void pause() {
        bus.unregister(this);
        stopPollingShots();
    }

    @Subscribe
    @Override public void onShotSent(ShotSent.Stream stream) {
        refresh();
    }
}
