package com.shootr.android.ui.presenter;

import android.os.Handler;
import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.presenter.interactorwrapper.EventTimelineInteractorsWrapper;
import com.shootr.android.ui.views.TimelineView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class TimelinePresenter implements Presenter, ShotSent.Receiver {

    private static final long REFRESH_INTERVAL_MILLISECONDS = 10 * 1000;

    private final EventTimelineInteractorsWrapper timelineInteractorWrapper;
    private final ShotModelMapper shotModelMapper;
    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;

    private TimelineView timelineView;
    private boolean isLoadingOlderShots;
    private boolean mightHaveMoreShots = true;

    private Runnable pollShotsRunnable;
    private boolean shouldPoll;
    private Handler pollShotsHanlder;

    @Inject public TimelinePresenter(EventTimelineInteractorsWrapper timelineInteractorWrapper, ShotModelMapper shotModelMapper,
      @Main Bus bus, ErrorMessageFactory errorMessageFactory) {
        this.timelineInteractorWrapper = timelineInteractorWrapper;
        this.shotModelMapper = shotModelMapper;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void setView(TimelineView timelineView) {
        this.timelineView = timelineView;
    }

    public void initialize(TimelineView timelineView) {
        this.setView(timelineView);
        this.loadTimeline();
        this.pollShotsHanlder = new Handler();
        this.pollShotsRunnable = new Runnable() {
            @Override
            public void run() {
                if (!shouldPoll) {
                    return;
                }
                loadNewShots();
                scheduleNextPolling();
            }
        };
    }

    private void startPollingShots() {
        shouldPoll = true;
        scheduleNextPolling();
    }

    private void stopPollingShots() {
        shouldPoll = false;
        pollShotsHanlder.removeCallbacks(pollShotsRunnable);
    }

    private void scheduleNextPolling() {
        if (shouldPoll) {
            pollShotsHanlder.postDelayed(pollShotsRunnable, REFRESH_INTERVAL_MILLISECONDS);
        }
    }

    protected void loadTimeline() {
        timelineView.showLoading();
        timelineInteractorWrapper.loadTimeline(new Interactor.Callback<Timeline>() {
            @Override public void onLoaded(Timeline timeline) {
                List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
                timelineView.hideLoading();
                timelineView.setShots(shotModels);
                if (!shotModels.isEmpty()) {
                    timelineView.hideEmpty();
                    timelineView.showShots();
                } else {
                    timelineView.showEmpty();
                    timelineView.hideShots();
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                timelineView.hideLoading();
                timelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
            }
        });
    }

    public void refresh() {
        timelineView.showLoading();
        this.loadNewShots();
    }

    public void showingLastShot(ShotModel lastShot) {
        if (!isLoadingOlderShots && mightHaveMoreShots) {
            this.loadOlderShots(lastShot.getBirth().getTime());
        }
    }

    private void loadNewShots() {
        timelineInteractorWrapper.refreshTimeline(new Interactor.Callback<Timeline>() {
            @Override public void onLoaded(Timeline timeline) {
                List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
                if (!shotModels.isEmpty()) {
                    timelineView.addNewShots(shotModels);
                    timelineView.hideEmpty();
                    timelineView.showShots();
                }
                timelineView.hideLoading();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                timelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
                timelineView.hideLoading();
            }
        });
    }

    private void loadOlderShots(long lastShotInScreenDate) {
        isLoadingOlderShots = true;
        timelineView.showLoadingOldShots();
        timelineInteractorWrapper.obtainOlderTimeline(lastShotInScreenDate, new Interactor.Callback<Timeline>() {
              @Override public void onLoaded(Timeline timeline) {
                  isLoadingOlderShots = false;
                  timelineView.hideLoadingOldShots();
                  List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
                  if (!shotModels.isEmpty()) {
                      timelineView.addOldShots(shotModels);
                  } else {
                      mightHaveMoreShots = false;
                  }
              }
          }, new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  timelineView.hideLoadingOldShots();
                  timelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
              }
          });
    }

    @Override public void resume() {
        loadNewShots();
        bus.register(this);
        startPollingShots();
    }

    @Override public void pause() {
        bus.unregister(this);
        stopPollingShots();
    }

    @Subscribe
    @Override public void onShotSent(ShotSent.Event event) {
        refresh();
    }
}
