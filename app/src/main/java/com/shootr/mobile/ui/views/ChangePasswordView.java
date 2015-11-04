package com.shootr.mobile.ui.views;

public interface ChangePasswordView {

    void showCurrentPasswordError(String errorMessage);

    void showNewPasswordError(String errorMessage);

    void showNewPasswordAgainError(String errorMessage);

    void showError(String errorMessage);

    void navigateToWelcomeScreen();

    void showLogoutInProgress();

    void hideLogoutInProgress();
}
