package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.data.prefs.ActivityBadgeCount;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.bus.BadgeChanged;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.discover.SendDeviceInfoInteractor;
import com.shootr.mobile.domain.interactor.user.GetCurrentUserInteractor;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.MainScreenView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import javax.inject.Inject;

public class MainScreenPresenter implements Presenter, BadgeChanged.Receiver {

    private final GetCurrentUserInteractor getCurrentUserInteractor;
    private final SendDeviceInfoInteractor sendDeviceInfoInteractor;
    private final UserModelMapper userModelMapper;
    private final IntPreference badgeCount;
    private final Bus bus;

    private MainScreenView mainScreenView;
    private boolean hasBeenPaused = false;

    @Inject public MainScreenPresenter(GetCurrentUserInteractor getCurrentUserInteractor,
      SendDeviceInfoInteractor sendDeviceInfoInteractor, UserModelMapper userModelMapper,
      @ActivityBadgeCount IntPreference badgeCount, @Main Bus bus) {
        this.getCurrentUserInteractor = getCurrentUserInteractor;
        this.sendDeviceInfoInteractor = sendDeviceInfoInteractor;
        this.userModelMapper = userModelMapper;
        this.badgeCount = badgeCount;
        this.bus = bus;
    }

    protected void setView(MainScreenView mainScreenView) {
        this.mainScreenView = mainScreenView;
    }

    public void initialize(MainScreenView mainScreenView) {
        setView(mainScreenView);
        this.loadCurrentUser();
        this.sendDeviceInfo();
        this.updateActivityBadge();
    }

    private void sendDeviceInfo() {
        sendDeviceInfoInteractor.sendDeviceInfo();
    }

    private void loadCurrentUser() {
        getCurrentUserInteractor.getCurrentUser(new Interactor.Callback<User>() {
            @Override public void onLoaded(User user) {
                UserModel userModel = userModelMapper.transform(user);
                mainScreenView.setUserData(userModel);
            }
        });
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
