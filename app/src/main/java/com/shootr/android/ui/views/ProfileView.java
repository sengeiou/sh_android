package com.shootr.android.ui.views;

public interface ProfileView {

    void showListingCount(Integer listingCount);

    void navigateToListing(String idUser);

    void showLogoutInProgress();

    void showError();

    void hideLogoutInProgress();

    void navigateToWelcomeScreen();

    void showLogoutButton();

    void showOpenStream();
}
