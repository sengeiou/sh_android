package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.views.base.DataTransferView;

public interface EmailLoginView extends DataTransferView {

    void goToTimeline();

    String getUsernameOrEmail();

    String getPassword();

    void disableLoginButton();

    void enableLoginButton();
}
