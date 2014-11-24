package com.shootr.android.ui.views;

import com.shootr.android.ui.model.UserModel;

public interface ProfileEditView {

    void renderUserInfo(UserModel currentUserModel);

    void hideKeyboard();
}
