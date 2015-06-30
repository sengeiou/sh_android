package com.shootr.android.ui.views;

import com.shootr.android.domain.exception.ShootrException;

public interface ProfileView {

    void showListingCount(Integer listingCount);

    void navigateToListing(String idUser);

    void showLogoutInProgress();

    void showError(ShootrException error);

    void hideLogoutInProgress();

    void navigateToWelcomeScreen();

    void showLogoutButton();
}
