package com.shootr.android.ui.views;

public interface ChangePasswordView {

    void showCurrentPasswordError(String errorMessage);

    void showNewPasswordError(String errorMessage);

    void showNewPasswordAgainError(String errorMessage);

    void showError(String errorMessage);

    void navigateToWelcomeScreen();
}
