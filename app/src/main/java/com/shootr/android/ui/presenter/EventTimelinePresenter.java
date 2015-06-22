package com.shootr.android.ui.presenter;

import android.os.Handler;
import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.presenter.interactorwrapper.EventTimelineInteractorsWrapper;
import com.shootr.android.ui.views.EventTimelineView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class EventTimelinePresenter implements Presenter, ShotSent.Receiver {

    private static final long REFRESH_INTERVAL_MILLISECONDS = 10 * 1000;

    private final EventTimelineInteractorsWrapper timelineInteractorWrapper;
    private final SelectEventInteractor selectEventInteractor;
    private final ShotModelMapper shotModelMapper;
    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;

    private EventTimelineView eventTimelineView;
    private String eventId;
    private boolean isLoadingOlderShots;
    private boolean mightHaveMoreShots = true;

    private Runnable pollShotsRunnable;
    private boolean shouldPoll;
    private Handler pollShotsHanlder;

    @Inject public EventTimelinePresenter(EventTimelineInteractorsWrapper timelineInteractorWrapper,
      SelectEventInteractor selectEventInteractor,
      ShotModelMapper shotModelMapper,
      @Main Bus bus,
      ErrorMessageFactory errorMessageFactory) {
        this.timelineInteractorWrapper = timelineInteractorWrapper;
        this.selectEventInteractor = selectEventInteractor;
        this.shotModelMapper = shotModelMapper;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void setView(EventTimelineView eventTimelineView) {
        this.eventTimelineView = eventTimelineView;
    }

    public void initialize(EventTimelineView eventTimelineView, String eventId) {
        this.eventId = eventId;
        this.setView(eventTimelineView);
        this.selectEvent();
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

    protected void selectEvent() {
        selectEventInteractor.selectEvent(eventId, new Interactor.Callback<EventSearchResult>() {
            @Override
            public void onLoaded(EventSearchResult eventSearchResult) {
                loadTimeline();
            }
        });
    }

    protected void loadTimeline() {
        eventTimelineView.showLoading();
        timelineInteractorWrapper.loadTimeline(new Interactor.Callback<Timeline>() {
            @Override public void onLoaded(Timeline timeline) {
                List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
                eventTimelineView.hideLoading();
                eventTimelineView.setShots(shotModels);
                if (!shotModels.isEmpty()) {
                    eventTimelineView.hideEmpty();
                    eventTimelineView.showShots();
                } else {
                    eventTimelineView.showEmpty();
                    eventTimelineView.hideShots();
                }
                loadNewShots();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                eventTimelineView.hideLoading();
                eventTimelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
            }
        });
    }

    public void refresh() {
        eventTimelineView.showLoading();
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
                    eventTimelineView.addNewShots(shotModels);
                    eventTimelineView.hideEmpty();
                    eventTimelineView.showShots();
                }
                eventTimelineView.hideLoading();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                eventTimelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
                eventTimelineView.hideLoading();
            }
        });
    }

    private void loadOlderShots(long lastShotInScreenDate) {
        isLoadingOlderShots = true;
        eventTimelineView.showLoadingOldShots();
        timelineInteractorWrapper.obtainOlderTimeline(lastShotInScreenDate, new Interactor.Callback<Timeline>() {
              @Override public void onLoaded(Timeline timeline) {
                  isLoadingOlderShots = false;
                  eventTimelineView.hideLoadingOldShots();
                  List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
                  if (!shotModels.isEmpty()) {
                      eventTimelineView.addOldShots(shotModels);
                  } else {
                      mightHaveMoreShots = false;
                  }
              }
          }, new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  eventTimelineView.hideLoadingOldShots();
                  eventTimelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
              }
          });
    }

    @Override public void resume() {
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
