package com.shootr.android.ui.presenter;

import android.os.Handler;
import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.ActivityTimeline;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.model.mappers.ActivityModelMapper;
import com.shootr.android.ui.presenter.interactorwrapper.ActivityTimelineInteractorsWrapper;
import com.shootr.android.ui.views.ActivityTimelineView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.util.List;
import javax.inject.Inject;

public class ActivityTimelinePresenter implements Presenter {

    private static final long REFRESH_INTERVAL_MILLISECONDS = 10 * 1000;

    private final ActivityTimelineInteractorsWrapper activityTimelineInteractorWrapper;
    private final ActivityModelMapper activityModelMapper;
    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;

    private ActivityTimelineView timelineView;
    private boolean isLoadingOlderActivities;
    private boolean mightHaveMoreActivities = true;

    private Runnable pollActivitiesRunnable;
    private boolean shouldPoll;

    private Handler pollActivitiesHanlder;

    @Inject public ActivityTimelinePresenter(ActivityTimelineInteractorsWrapper activityTimelineInteractorWrapper,
      ActivityModelMapper activityModelMapper, @Main Bus bus, ErrorMessageFactory errorMessageFactory) {
        this.activityTimelineInteractorWrapper = activityTimelineInteractorWrapper;
        this.activityModelMapper = activityModelMapper;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void setView(ActivityTimelineView timelineView) {
        this.timelineView = timelineView;
    }

    public void initialize(ActivityTimelineView timelineView) {
        this.setView(timelineView);
        this.loadTimeline();
        this.pollActivitiesHanlder = new Handler();
        this.pollActivitiesRunnable = new Runnable() {
            @Override
            public void run() {
                if (!shouldPoll) {
                    return;
                }
                loadNewActivities();
                scheduleNextPolling();
            }
        };
    }

    private void startPollingActivities() {
        shouldPoll = true;
        scheduleNextPolling();
    }

    private void stopPollingActivities() {
        shouldPoll = false;
        pollActivitiesHanlder.removeCallbacks(pollActivitiesRunnable);
    }

    private void scheduleNextPolling() {
        if (shouldPoll) {
            pollActivitiesHanlder.postDelayed(pollActivitiesRunnable, REFRESH_INTERVAL_MILLISECONDS);
        }
    }

    protected void loadTimeline() {
        timelineView.showLoading();
        activityTimelineInteractorWrapper.loadTimeline(new Interactor.Callback<ActivityTimeline>() {
            @Override public void onLoaded(ActivityTimeline timeline) {
                List<ActivityModel> activityModels = activityModelMapper.transform(timeline.getActivities());
                timelineView.hideLoading();
                timelineView.setActivities(activityModels);
                if (!activityModels.isEmpty()) {
                    timelineView.hideEmpty();
                    timelineView.showActivities();
                } else {
                    timelineView.showEmpty();
                    timelineView.hideActivities();
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
        this.loadNewActivities();
    }

    public void showingLastActivity(ActivityModel lastActivity) {
        if (!isLoadingOlderActivities && mightHaveMoreActivities) {
            this.loadOlderActivities(lastActivity.getPublishDate().getTime());
        }
    }

    private void loadNewActivities() {
        activityTimelineInteractorWrapper.refreshTimeline(new Interactor.Callback<ActivityTimeline>() {
            @Override public void onLoaded(ActivityTimeline timeline) {
                List<ActivityModel> activityModels = activityModelMapper.transform(timeline.getActivities());
                if (!activityModels.isEmpty()) {
                    timelineView.addNewActivities(activityModels);
                    timelineView.hideEmpty();
                    timelineView.showActivities();
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

    private void loadOlderActivities(long lastActivityInScreenDate) {
        isLoadingOlderActivities = true;
        timelineView.showLoadingOldActivities();
        activityTimelineInteractorWrapper.obtainOlderTimeline(lastActivityInScreenDate, new Interactor.Callback<ActivityTimeline>() {
            @Override public void onLoaded(ActivityTimeline timeline) {
                isLoadingOlderActivities = false;
                timelineView.hideLoadingOldActivities();
                List<ActivityModel> activityModels = activityModelMapper.transform(timeline.getActivities());
                if (!activityModels.isEmpty()) {
                    timelineView.addOldActivities(activityModels);
                } else {
                    mightHaveMoreActivities = false;
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                timelineView.hideLoadingOldActivities();
                timelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
            }
        });
    }

    @Override public void resume() {
        loadNewActivities();
        bus.register(this);
        startPollingActivities();
    }

    @Override public void pause() {
        bus.unregister(this);
        stopPollingActivities();
    }
}