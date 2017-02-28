package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.data.prefs.ActivityBadgeCount;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetFavoritesIdsInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.GetFollowingIdsInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.model.activity.ActivityTimeline;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.model.mappers.ActivityModelMapper;
import com.shootr.mobile.ui.presenter.interactorwrapper.ActivityTimelineInteractorsWrapper;
import com.shootr.mobile.ui.views.GenericActivityTimelineView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GenericActivityTimelinePresenter implements Presenter {

    private static final long REFRESH_INTERVAL_MILLISECONDS = 5 * 1000;

    private final ActivityTimelineInteractorsWrapper activityTimelineInteractorWrapper;
    private final ActivityModelMapper activityModelMapper;
    private final AddToFavoritesInteractor addToFavoritesInteractor;
    private final RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
    private final GetFollowingIdsInteractor getFollowingIdsInteractor;
    private final GetFavoritesIdsInteractor getFavoritesIdsInteractor;
    private final FollowInteractor followInteractor;
    private final UnfollowInteractor unfollowInteractor;
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
    private ArrayList<String> followingIds = new ArrayList<>();
    private ArrayList<String> favortiesIds = new ArrayList<>();

    @Inject
    public GenericActivityTimelinePresenter(ActivityTimelineInteractorsWrapper activityTimelineInteractorWrapper,
        ActivityModelMapper activityModelMapper, AddToFavoritesInteractor addToFavoritesInteractor,
        RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
        GetFollowingIdsInteractor getFollowingIdsInteractor,
        GetFavoritesIdsInteractor getFavoritesIdsInteractor, FollowInteractor followInteractor,
        UnfollowInteractor unfollowInteractor, @Main Bus bus,
        ErrorMessageFactory errorMessageFactory, Poller poller, @ActivityBadgeCount IntPreference badgeCount,
        SessionRepository sessionRepository) {
        this.activityTimelineInteractorWrapper = activityTimelineInteractorWrapper;
        this.activityModelMapper = activityModelMapper;
        this.addToFavoritesInteractor = addToFavoritesInteractor;
        this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
        this.getFollowingIdsInteractor = getFollowingIdsInteractor;
        this.getFavoritesIdsInteractor = getFavoritesIdsInteractor;
        this.followInteractor = followInteractor;
        this.unfollowInteractor = unfollowInteractor;
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
        getFollowingIds();
        poller.init(REFRESH_INTERVAL_MILLISECONDS, new Runnable() {
            @Override public void run() {
                loadNewActivities(badgeCount.get());
            }
        });
    }

    private void getFollowingIds() {
        getFollowingIdsInteractor.loadFollowingsIds(sessionRepository.getCurrentUserId(),
            new Interactor.Callback<List<String>>() {
                @Override public void onLoaded(List<String> strings) {
                    followingIds.clear();
                    followingIds.addAll(strings);
                    loadFavoritesIds();
                }
            });
    }

    private void loadFavoritesIds() {
        getFavoritesIdsInteractor.loadFavoriteStreams(new Interactor.Callback<List<String>>() {
            @Override public void onLoaded(List<String> strings) {
                favortiesIds.clear();
                favortiesIds.addAll(strings);
                loadTimeline();
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
                  List<ActivityModel> activityModels =
                      activityModelMapper.mapWithFollowingsAndFavorites(timeline.getActivities(), followingIds, favortiesIds);
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
                  List<ActivityModel> newActivity =
                      activityModelMapper.mapWithFollowingsAndFavorites(timeline.getActivities(), followingIds, favortiesIds);
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
                  List<ActivityModel> activityModels =
                      activityModelMapper.mapWithFollowingsAndFavorites(timeline.getActivities(), followingIds, favortiesIds);
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
        getFollowingIds();
        bus.register(this);
        startPollingActivities();
    }

    @Override public void pause() {
        bus.unregister(this);
        stopPollingActivities();
    }

    public void followUser(final String idUser) {
        followInteractor.follow(idUser, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                followingIds.add(idUser);
                loadTimeline();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                timelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
            }
        });
    }

    public void unFollowUser(final String idUser) {
        unfollowInteractor.unfollow(idUser, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                followingIds.remove(idUser);
                loadTimeline();
            }
        });
    }

    public void addFavorite(final String idStream) {
        addToFavoritesInteractor.addToFavorites(idStream, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                favortiesIds.add(idStream);
                loadTimeline();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                timelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
            }
        });
    }

    public void removeFavorite(final String idStream) {
        removeFromFavoritesInteractor.removeFromFavorites(idStream, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                favortiesIds.remove(idStream);
                loadTimeline();
            }
        });
    }
}
