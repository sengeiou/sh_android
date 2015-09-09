package com.shootr.android.ui.views;

public interface WelcomePageView {

    void showError(String errorMessage);

    void goToStreamList();

    void showSpinner();

    void hideGetStarted();
}
