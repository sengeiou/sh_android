package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.data.prefs.ActivityBadgeCount;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.domain.bus.BadgeChanged;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.discover.SendDeviceInfoInteractor;
import com.shootr.mobile.domain.interactor.shot.SendShotEventStatsIneteractor;
import com.shootr.mobile.domain.interactor.user.GetCurrentUserInteractor;
import com.shootr.mobile.domain.interactor.user.GetFollowingInteractor;
import com.shootr.mobile.domain.interactor.user.GetUserForAnalythicsByIdInteractor;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.MainScreenView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import javax.inject.Inject;

public class MainScreenPresenter implements Presenter, BadgeChanged.Receiver {

  private final GetCurrentUserInteractor getCurrentUserInteractor;
  private final SendDeviceInfoInteractor sendDeviceInfoInteractor;
  private final SendShotEventStatsIneteractor sendShotEventStatsIneteractor;
  private final GetUserForAnalythicsByIdInteractor getUserForAnalythicsByIdInteractor;
  private final SessionRepository sessionRepository;
  private final UserModelMapper userModelMapper;
  private final IntPreference badgeCount;
  private final GetFollowingInteractor followingInteractor;
  private final Bus bus;

  private MainScreenView mainScreenView;
  private boolean hasBeenPaused = false;
  private UserModel userModel;

  @Inject public MainScreenPresenter(GetCurrentUserInteractor getCurrentUserInteractor,
      SendDeviceInfoInteractor sendDeviceInfoInteractor,
      SendShotEventStatsIneteractor sendShotEventStatsIneteractor,
      GetUserForAnalythicsByIdInteractor getUserForAnalythicsByIdInteractor,
      SessionRepository sessionRepository, UserModelMapper userModelMapper,
      @ActivityBadgeCount IntPreference badgeCount, GetFollowingInteractor followingInteractor,
      @Main Bus bus) {
    this.getCurrentUserInteractor = getCurrentUserInteractor;
    this.sendDeviceInfoInteractor = sendDeviceInfoInteractor;
    this.sendShotEventStatsIneteractor = sendShotEventStatsIneteractor;
    this.getUserForAnalythicsByIdInteractor = getUserForAnalythicsByIdInteractor;
    this.sessionRepository = sessionRepository;
    this.userModelMapper = userModelMapper;
    this.badgeCount = badgeCount;
    this.followingInteractor = followingInteractor;
    this.bus = bus;
  }

  protected void setView(MainScreenView mainScreenView) {
    this.mainScreenView = mainScreenView;
  }

  public void initialize(MainScreenView mainScreenView) {
    setView(mainScreenView);
    this.loadCurrentUser();
    this.getFollows();
    this.sendDeviceInfo();
    this.sendShotEventStats();
    this.updateActivityBadge();
  }

  private void sendDeviceInfo() {
    sendDeviceInfoInteractor.sendDeviceInfo();
  }

  private void getFollows() {
    followingInteractor.synchronizeFollow();
  }

  private void sendShotEventStats() {
    sendShotEventStatsIneteractor.sendShotsStats();
  }

  private void loadCurrentUser() {
    getCurrentUserInteractor.getCurrentUser(new Interactor.Callback<User>() {
      @Override public void onLoaded(User user) {
        userModel = userModelMapper.transform(user);
        mainScreenView.setUserData(userModel);
      }
    });
    loadRemoteCurrentUser();
  }

  private void loadRemoteCurrentUser() {
    if (userModel != null) {
      getUserForAnalythicsByIdInteractor.getCurrentUserForAnalythics(userModel.getIdUser(),
          new Interactor.Callback<User>() {
            @Override public void onLoaded(User user) {
              sessionRepository.getCurrentUser().setAnalyticsUserType(user.getAnalyticsUserType());
              sessionRepository.getCurrentUser().setReceivedReactions(user.getReceivedReactions());
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                    /* no-op */
            }
          });
    }
  }

  private void updateActivityBadge() {
    int activities = badgeCount.get();
    if (activities > 0) {
      mainScreenView.showActivityBadge(activities);
    }
  }

  @Override public void resume() {
    updateActivityBadge();
    bus.register(this);
    if (hasBeenPaused) {
      loadCurrentUser();
      sendShotEventStats();
    }
  }

  @Override public void pause() {
    bus.unregister(this);
    hasBeenPaused = true;
  }

  @Override @Subscribe public void onBadgeChanged(BadgeChanged.Event event) {
    updateActivityBadge();
  }
}
