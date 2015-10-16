package com.shootr.android.ui.views;

import com.shootr.android.ui.views.base.DataTransferView;

public interface EmailLoginView extends DataTransferView {

    void goToTimeline();

    String getUsernameOrEmail();

    String getPassword();

    void disableLoginButton();

    void enableLoginButton();

}
