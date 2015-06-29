package com.shootr.android.ui.presenter;

import android.view.Menu;
import android.view.MenuInflater;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetListingCountInteractor;
import com.shootr.android.domain.interactor.user.GetCurrentUserInteractor;
import com.shootr.android.domain.interactor.user.LogoutInteractor;
import com.shootr.android.ui.views.ProfileView;
import com.shootr.android.util.DatabaseVersionUtils;
import javax.inject.Inject;

public class ProfilePresenter implements Presenter {

    private final GetListingCountInteractor getListingCountInteractor;
    private final GetCurrentUserInteractor getCurrentUserInteractor;
    private final LogoutInteractor logoutInteractor;
    private final DatabaseVersionUtils databaseVersionUtils;
    private ProfileView profileView;
    private String profileIdUser;

    @Inject public ProfilePresenter(GetListingCountInteractor getListingCountInteractor,
      GetCurrentUserInteractor getCurrentUserInteractor, LogoutInteractor logoutInteractor,
      DatabaseVersionUtils databaseVersionUtils) {
        this.getListingCountInteractor = getListingCountInteractor;
        this.getCurrentUserInteractor = getCurrentUserInteractor;
        this.logoutInteractor = logoutInteractor;
        this.databaseVersionUtils = databaseVersionUtils;
    }

    protected void setView(ProfileView profileView){
        this.profileView = profileView;
    }

    public void initialize(ProfileView profileView, String idUser){
        this.setView(profileView);
        this.profileIdUser = idUser;
        loadCurrentUserListing();
    }

    public void setupOptionsMenu(final Menu menu, final MenuInflater inflater) {
        if(profileIdUser != null) {
            attempLogout(menu, inflater);
        }
    }

    private void attempLogout(final Menu menu, final MenuInflater inflater) {
        getCurrentUserInteractor.getCurrentUser(new Interactor.Callback<User>() {
            @Override public void onLoaded(User user) {
                if(profileIdUser.equals(user.getIdUser())){
                    profileView.createOptionsMenu(menu, inflater);
                }
            }
        });
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

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
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
}
