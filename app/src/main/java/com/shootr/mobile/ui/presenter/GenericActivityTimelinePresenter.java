package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.data.prefs.ActivityBadgeCount;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.domain.ActivityTimeline;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.model.mappers.ActivityModelMapper;
import com.shootr.mobile.ui.presenter.interactorwrapper.ActivityTimelineInteractorsWrapper;
import com.shootr.mobile.ui.views.GenericActivityTimelineView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.util.List;
import javax.inject.Inject;

public class GenericActivityTimelinePresenter implements Presenter {

    private static final long REFRESH_INTERVAL_MILLISECONDS = 5 * 1000;

    private final ActivityTimelineInteractorsWrapper activityTimelineInteractorWrapper;
    private final ActivityModelMapper activityModelMapper;
    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;
    private final Poller poller;
    private final IntPreference badgeCount;
    private final SessionRepository sessionRepository;

    private GenericActivityTimelineView timelineView;
    private boolean isLoadingOlderActivities;
    private boolean mightHaveMoreActivities = true;
    private boolean isEmpty;
    private Boolean isUserActivityTimeline;

    @Inject
    public GenericActivityTimelinePresenter(ActivityTimelineInteractorsWrapper activityTimelineInteractorWrapper,
      ActivityModelMapper activityModelMapper, @Main Bus bus, ErrorMessageFactory errorMessageFactory, Poller poller,
      @ActivityBadgeCount IntPreference badgeCount, SessionRepository sessionRepository) {
        this.activityTimelineInteractorWrapper = activityTimelineInteractorWrapper;
        this.activityModelMapper = activityModelMapper;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
        this.poller = poller;
        this.badgeCount = badgeCount;
        this.sessionRepository = sessionRepository;
    }

    public void setView(GenericActivityTimelineView timelineView) {
        this.timelineView = timelineView;
    }

    public void initialize(GenericActivityTimelineView timelineView, Boolean isUserActivityTimeline) {
        this.setView(timelineView);
        this.isUserActivityTimeline = isUserActivityTimeline;
        this.loadTimeline();
        poller.init(REFRESH_INTERVAL_MILLISECONDS, new Runnable() {
            @Override public void run() {
                loadNewActivities(badgeCount.get());
            }
        });
    }

    private void clearActivityBadge() {
        badgeCount.delete();
    }

    private void startPollingActivities() {
        poller.startPolling();
    }

    private void stopPollingActivities() {
        poller.stopPolling();
    }

    protected void loadTimeline() {
        activityTimelineInteractorWrapper.loadTimeline(isUserActivityTimeline,
          new Interactor.Callback<ActivityTimeline>() {
              @Override public void onLoaded(ActivityTimeline timeline) {
                  List<ActivityModel> activityModels = activityModelMapper.transform(timeline.getActivities());
                  timelineView.setActivities(activityModels, sessionRepository.getCurrentUserId());
                  isEmpty = activityModels.isEmpty();
                  if (isEmpty) {
                      timelineView.showEmpty();
                      timelineView.hideActivities();
                  } else {
                      timelineView.hideEmpty();
                      timelineView.showActivities();
                  }
                  loadNewActivities(badgeCount.get());
                  clearActivityBadge();
              }
          });
    }

    public void refresh() {
        timelineView.showLoading();
        this.loadNewActivities(badgeCount.get());
    }

    public void showingLastActivity(ActivityModel lastActivity) {
        if (!isLoadingOlderActivities && mightHaveMoreActivities) {
            this.loadOlderActivities(lastActivity.getPublishDate().getTime());
        }
    }

    private void loadNewActivities(int badgeCount) {
        if (badgeCount > 0) {
            timelineView.showLoading();
        }
        if (isEmpty) {
            timelineView.hideEmpty();
            timelineView.showLoadingActivity();
        }
        activityTimelineInteractorWrapper.refreshTimeline(isUserActivityTimeline,
          new Interactor.Callback<ActivityTimeline>() {
              @Override public void onLoaded(ActivityTimeline timeline) {
                  List<ActivityModel> newActivity = activityModelMapper.transform(timeline.getActivities());
                  boolean hasNewActivity = !newActivity.isEmpty();
                  if (isEmpty && hasNewActivity) {
                      isEmpty = false;
                  } else if (isEmpty && !hasNewActivity) {
                      timelineView.showEmpty();
                  }
                  if (hasNewActivity) {
                      timelineView.addNewActivities(newActivity);
                      timelineView.hideEmpty();
                      timelineView.showActivities();
                  }
                  timelineView.hideLoading();
                  timelineView.hideLoadingActivity();
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  timelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
                  timelineView.hideLoading();
                  timelineView.hideLoadingActivity();
              }
          });
    }

    private void loadOlderActivities(long lastActivityInScreenDate) {
        isLoadingOlderActivities = true;
        timelineView.showLoadingOldActivities();
        activityTimelineInteractorWrapper.obtainOlderTimeline(isUserActivityTimeline,
          lastActivityInScreenDate,
          new Interactor.Callback<ActivityTimeline>() {
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
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  timelineView.hideLoadingOldActivities();
                  timelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
              }
          });
    }

    @Override public void resume() {
        loadTimeline();
        bus.register(this);
        startPollingActivities();
    }

    @Override public void pause() {
        bus.unregister(this);
        stopPollingActivities();
    }
}
