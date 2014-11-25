package com.shootr.android.ui.presenter;

import com.shootr.android.data.SessionManager;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.ProfileEditView;
import javax.inject.Inject;

public class ProfileEditPresenter {

    private ProfileEditView profileEditView;
    private SessionManager sessionManager;
    private UserModelMapper userModelMapper;

    @Inject public ProfileEditPresenter(SessionManager sessionManager, UserModelMapper userModelMapper) {
        this.sessionManager = sessionManager;
        this.userModelMapper = userModelMapper;
    }

    public void initialize(ProfileEditView profileEditView) {
        this.profileEditView = profileEditView;
        this.fillCurrentUserData();
        this.profileEditView.hideKeyboard();
    }

    private void fillCurrentUserData() {
        UserModel currentUserModel = userModelMapper.toUserModel(sessionManager.getCurrentUser(), null, true);
        this.profileEditView.renderUserInfo(currentUserModel);
    }
}
