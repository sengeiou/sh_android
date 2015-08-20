package com.shootr.android.ui.presenter;

import com.shootr.android.domain.User;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.GetCurrentUserInteractor;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.MainScreenView;
import javax.inject.Inject;

public class MainScreenPresenter implements Presenter {

    private final GetCurrentUserInteractor getCurrentUserInteractor;
    private final UserModelMapper userModelMapper;

    private MainScreenView mainScreenView;
    private boolean hasBeenPaused = false;

    @Inject public MainScreenPresenter(GetCurrentUserInteractor getCurrentUserInteractor,
      UserModelMapper userModelMapper) {
        this.getCurrentUserInteractor = getCurrentUserInteractor;
        this.userModelMapper = userModelMapper;
    }

    public void initialize(MainScreenView mainScreenView) {
        this.mainScreenView = mainScreenView;
        this.loadCurrentUser();
    }

    private void loadCurrentUser() {
        getCurrentUserInteractor.getCurrentUser(new Interactor.Callback<User>() {
            @Override public void onLoaded(User user) {
                UserModel userModel = userModelMapper.transform(user);
                mainScreenView.setUserData(userModel);
            }
        });
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            loadCurrentUser();
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }
}
