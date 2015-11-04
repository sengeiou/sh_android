package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.UserModel;

public interface MainScreenView {

    void setUserData(UserModel userModel);

    void showActivityBadge(int count);
}
