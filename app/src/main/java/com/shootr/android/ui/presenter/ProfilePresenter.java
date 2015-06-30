package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetListingCountInteractor;
import com.shootr.android.domain.interactor.user.LogoutInteractor;
import com.shootr.android.ui.views.ProfileView;
import com.shootr.android.util.DatabaseVersionUtils;
import javax.inject.Inject;

public class ProfilePresenter implements Presenter {

    private final GetListingCountInteractor getListingCountInteractor;
    private final LogoutInteractor logoutInteractor;
    private final DatabaseVersionUtils databaseVersionUtils;
    private ProfileView profileView;
    private String profileIdUser;
    private Boolean isCurrentUser;

    @Inject public ProfilePresenter(GetListingCountInteractor getListingCountInteractor,
      LogoutInteractor logoutInteractor, DatabaseVersionUtils databaseVersionUtils) {
        this.getListingCountInteractor = getListingCountInteractor;
        this.logoutInteractor = logoutInteractor;
        this.databaseVersionUtils = databaseVersionUtils;
    }

    protected void setView(ProfileView profileView){
        this.profileView = profileView;
    }

    public void initialize(ProfileView profileView, String idUser, boolean isCurrentUser){
        this.setView(profileView);
        this.profileIdUser = idUser;
        this.isCurrentUser = isCurrentUser;
        loadCurrentUserListing();
    }

    public void setupMenuItemsVisibility() {
        if(isCurrentUser){
            profileView.showLogoutButton();
        }
    }

    public void loadCurrentUserListing() {
        getListingCountInteractor.loadListingCount(profileIdUser, new Interactor.Callback<Integer>() {
            @Override public void onLoaded(Integer numberOfListingEvents) {
                if (numberOfListingEvents > 0) {
                    profileView.showListingCount(numberOfListingEvents);
                }
            }
        });
    }

    public void clickListing() {
        profileView.navigateToListing(profileIdUser);
    }

    public void logoutSelected() {
        profileView.showLogoutInProgress();
        logoutInteractor.attempLogout(new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                databaseVersionUtils.clearDataOnLogout();
                profileView.hideLogoutInProgress();
                profileView.navigateToWelcomeScreen();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                profileView.hideLogoutInProgress();
                profileView.showError(error);
            }
        });
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
