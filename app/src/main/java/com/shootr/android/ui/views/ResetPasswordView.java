package com.shootr.android.ui.views;

public interface ResetPasswordView {

    void disableResetButton();

    void enableResetButton();

    String getUsernameOrEmail();

    void showLoading();

    void resetButtonToNormalStatus();

    void showError(String errorMessage);
}
