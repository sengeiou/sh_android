package com.shootr.android.ui.presenter;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.GetCurrentUserInteractor;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.MainTabbedView;
import javax.inject.Inject;

public class CurrentUserPresenter implements Presenter {

    private final GetCurrentUserInteractor getCurrentUserInteractor;
    private final UserModelMapper userModelMapper;

    private MainTabbedView mainTabbedView;

    @Inject public CurrentUserPresenter(GetCurrentUserInteractor getCurrentUserInteractor, UserModelMapper userModelMapper) {
        this.getCurrentUserInteractor = getCurrentUserInteractor;
        this.userModelMapper = userModelMapper;
    }

    public void initialize(MainTabbedView mainTabbedView){
        this.mainTabbedView = mainTabbedView;
    }

    public void getCurrentUser() throws Throwable {
        getCurrentUserInteractor.getCurrentUser(new Interactor.Callback<User>() {
            @Override public void onLoaded(User user) {
                UserModel userModel = userModelMapper.transform(user);
                mainTabbedView.setUserInformationInToolbar(userModel);
            }
        });
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
