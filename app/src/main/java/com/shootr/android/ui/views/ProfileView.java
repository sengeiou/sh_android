package com.shootr.android.ui.views;

import android.view.Menu;
import android.view.MenuInflater;
import com.shootr.android.domain.exception.ShootrException;

public interface ProfileView {

    void showListingCount(Integer listingCount);

    void navigateToListing(String idUser);

    void createOptionsMenu(Menu menu, MenuInflater inflater);

    void showLogoutInProgress();

    void showError(ShootrException error);

    void hideLogoutInProgress();
}
