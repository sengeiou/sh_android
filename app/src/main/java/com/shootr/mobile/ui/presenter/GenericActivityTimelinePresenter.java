package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.data.prefs.ActivityBadgeCount;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.FollowUnfollow;
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
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GenericActivityTimelinePresenter implements Presenter, FollowUnfollow.Receiver {

  private static final long REFRESH_INTERVAL_MILLISECONDS = 10 * 1000;
  private static final long MAX_REFRESH_INTERVAL_MILLISECONDS = 60 * 1000;

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
  private final BusPublisher busPublisher;

  private GenericActivityTimelineView timelineView;
  private boolean isLoadingOlderActivities;
  private boolean mightHaveMoreActivities = true;
  private boolean isEmpty;
  private Boolean isHistoryActivity;
  private ArrayList<String> followingIds = new ArrayList<>();
  private ArrayList<String> favoritesIds = new ArrayList<>();

  @Inject public GenericActivityTimelinePresenter(
      ActivityTimelineInteractorsWrapper activityTimelineInteractorWrapper,
      ActivityModelMapper activityModelMapper, AddToFavoritesInteractor addToFavoritesInteractor,
      RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      GetFollowingIdsInteractor getFollowingIdsInteractor,
      GetFavoritesIdsInteractor getFavoritesIdsInteractor, FollowInteractor followInteractor,
      UnfollowInteractor unfollowInteractor, @Main Bus bus, ErrorMessageFactory errorMessageFactory,
      Poller poller, @ActivityBadgeCount IntPreference badgeCount,
      SessionRepository sessionRepository, BusPublisher busPublisher) {
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
    this.busPublisher = busPublisher;
  }

  public void setView(GenericActivityTimelineView timelineView) {
    this.timelineView = timelineView;
  }

  public void initialize(GenericActivityTimelineView timelineView, Boolean isUserActivityTimeline,
      boolean usePoller) {
    this.setView(timelineView);
    this.isHistoryActivity = isUserActivityTimeline;
    getFollowingIds();
    if (usePoller) {
      setupPoller();
    }
    loadNewActivities(badgeCount.get());
  }

  private void setupPoller() {
    long intervalSynchroServerResponse = handleIntervalSynchro();
    poller.init(intervalSynchroServerResponse, new Runnable() {
      @Override public void run() {
        loadNewActivities(badgeCount.get());
        changeSynchroTimePoller();
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
        favoritesIds.clear();
        favoritesIds.addAll(strings);
        loadTimeline();
      }
    });
  }

  private void changeSynchroTimePoller() {
    if (poller.isPolling()) {
      long intervalSynchroServerResponse = handleIntervalSynchro();
      if (intervalSynchroServerResponse != poller.getIntervalMilliseconds()) {
        poller.stopPolling();
        poller.setIntervalMilliseconds(intervalSynchroServerResponse);
        poller.startPolling();
      }
    }
  }

  private long handleIntervalSynchro() {
    int actualSynchroInterval = sessionRepository.getSynchroTime();
    long intervalSynchroServerResponse = actualSynchroInterval * 1000;
    if (intervalSynchroServerResponse < REFRESH_INTERVAL_MILLISECONDS) {
      intervalSynchroServerResponse = REFRESH_INTERVAL_MILLISECONDS;
    } else if (intervalSynchroServerResponse > MAX_REFRESH_INTERVAL_MILLISECONDS) {
      intervalSynchroServerResponse = REFRESH_INTERVAL_MILLISECONDS;
    }
    return intervalSynchroServerResponse;
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
    activityTimelineInteractorWrapper.loadTimeline(isHistoryActivity,
        new Interactor.Callback<ActivityTimeline>() {
          @Override public void onLoaded(ActivityTimeline timeline) {
            List<ActivityModel> activityModels =
                activityModelMapper.mapWithFollowingsAndFavorites(timeline.getActivities(),
                    followingIds, favoritesIds);
            timelineView.setActivities(activityModels, sessionRepository.getCurrentUserId());
            isEmpty = activityModels.isEmpty();
            if (isEmpty) {
              timelineView.showEmpty();
              timelineView.hideActivities();
            } else {
              timelineView.hideEmpty();
              timelineView.showActivities();
            }
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
    activityTimelineInteractorWrapper.refreshTimeline(isHistoryActivity,
        new Interactor.Callback<ActivityTimeline>() {
          @Override public void onLoaded(ActivityTimeline timeline) {
            List<ActivityModel> newActivity =
                activityModelMapper.mapWithFollowingsAndFavorites(timeline.getActivities(),
                    followingIds, favoritesIds);
            boolean hasNewActivity = !newActivity.isEmpty();
            if (hasNewActivity) {
              timelineView.setNewContentArrived();
            }
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
        }, new Interactor.ErrorCallback() {
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
    activityTimelineInteractorWrapper.obtainOlderTimeline(isHistoryActivity,
        lastActivityInScreenDate, new Interactor.Callback<ActivityTimeline>() {
          @Override public void onLoaded(ActivityTimeline timeline) {
            isLoadingOlderActivities = false;
            timelineView.hideLoadingOldActivities();
            List<ActivityModel> activityModels =
                activityModelMapper.mapWithFollowingsAndFavorites(timeline.getActivities(),
                    followingIds, favoritesIds);
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
        busPublisher.post(new FollowUnfollow.Event(idUser, true));
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
        busPublisher.post(new FollowUnfollow.Event(idUser, false));
      }
    });
  }

  public void addFavorite(final String idStream) {
    addToFavoritesInteractor.addToFavorites(idStream, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        favoritesIds.add(idStream);
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
        favoritesIds.remove(idStream);
        loadTimeline();
      }
    });
  }

  @Subscribe @Override public void onFollowUnfollow(FollowUnfollow.Event event) {
    if (event.isFollow()) {
      followingIds.add(event.getIdUser());
    } else {
      followingIds.remove(event.getIdUser());
    }
    loadTimeline();
  }
}
