package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.GetListingCountInteractor;
import com.shootr.android.domain.interactor.user.LogoutInteractor;
import com.shootr.android.ui.views.ProfileView;
import javax.inject.Inject;

public class ProfilePresenter implements Presenter {

    private final GetListingCountInteractor getListingCountInteractor;
    private final LogoutInteractor logoutInteractor;
    private ProfileView profileView;
    private String profileIdUser;
    private Boolean isCurrentUser;

    @Inject public ProfilePresenter(GetListingCountInteractor getListingCountInteractor,
      LogoutInteractor logoutInteractor) {
        this.getListingCountInteractor = getListingCountInteractor;
        this.logoutInteractor = logoutInteractor;
    }

    protected void setView(ProfileView profileView){
        this.profileView = profileView;
    }

    public void initialize(ProfileView profileView, String idUser, boolean isCurrentUser){
        this.setView(profileView);
        this.profileIdUser = idUser;
        setCurrentUser(isCurrentUser);
        loadCurrentUserListing();
        setupLogoutVisibility();
    }

    protected void setCurrentUser(boolean isCurrentUser) {
        this.isCurrentUser = isCurrentUser;
    }

    protected void setupLogoutVisibility() {
        if(isCurrentUser){
            profileView.showLogoutButton();
        }
    }

    public void loadCurrentUserListing() {
        getListingCountInteractor.loadListingCount(profileIdUser, new Interactor.Callback<Integer>() {
            @Override public void onLoaded(Integer numberOfListingStreams) {
                if (numberOfListingStreams > 0) {
                    profileView.showListingCount(numberOfListingStreams);
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
                profileView.navigateToWelcomeScreen();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                profileView.hideLogoutInProgress();
                profileView.showError();
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
