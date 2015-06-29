package com.shootr.android.ui.views;

import android.view.Menu;
import android.view.MenuInflater;

public interface ProfileView {

    void showListingCount(Integer listingCount);

    void navigateToListing(String idUser);

    void createOptionsMenu(Menu menu, MenuInflater inflater);

    void showLogoutInProgress();
}
