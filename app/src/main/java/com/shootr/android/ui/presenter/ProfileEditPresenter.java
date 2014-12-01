package com.shootr.android.ui.presenter;

import com.shootr.android.data.SessionManager;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.ProfileEditView;
import javax.inject.Inject;
import timber.log.Timber;

public class ProfileEditPresenter {

    private ProfileEditView profileEditView;
    private SessionManager sessionManager;
    private UserModelMapper userModelMapper;
    private UserModel currentUserModel;

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
        currentUserModel = userModelMapper.toUserModel(sessionManager.getCurrentUser(), null, true);
        this.profileEditView.renderUserInfo(currentUserModel);
    }

    public void discard() {
        //TODO confirmation alert()
        profileEditView.closeScreen();
    }

    public void done() {
        UserModel updatedUserModel = this.getUpadtedUserData();
        this.saveUpdatedProfile(updatedUserModel);
    }

    private UserModel getUpadtedUserData() {
        UserModel updatedUserModel = currentUserModel.clone();
        updatedUserModel.setUsername(profileEditView.getUsername());
        updatedUserModel.setName(profileEditView.getName());
        return updatedUserModel;
    }

    private void saveUpdatedProfile(UserModel updatedUserModel) {
        //TODO launch job
        Timber.d("Update user info ;)");
        onUserProfileUpdated();
    }

    public void onUserProfileUpdated() {
        profileEditView.showUpdatedSuccessfulAlert();
        profileEditView.closeScreen();
    }
}
