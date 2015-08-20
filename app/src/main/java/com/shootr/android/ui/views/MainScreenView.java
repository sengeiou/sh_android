package com.shootr.android.ui.views;

import com.shootr.android.ui.model.UserModel;

public interface MainScreenView {

    void setUserData(UserModel userModel);

    void showActivityBadge(int count);
}
