package com.shootr.android.ui.presenter;

import android.view.Menu;
import android.view.MenuInflater;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetListingCountInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.views.ProfileView;
import javax.inject.Inject;

public class ProfilePresenter implements Presenter {

    private final GetListingCountInteractor getListingCountInteractor;
    private final SessionRepository sessionRepository;
    private ProfileView profileView;
    private String profileIdUser;

    @Inject public ProfilePresenter(GetListingCountInteractor getListingCountInteractor,
      SessionRepository sessionRepository) {
        this.getListingCountInteractor = getListingCountInteractor;
        this.sessionRepository = sessionRepository;
    }

    protected void setView(ProfileView profileView){
        this.profileView = profileView;
    }

    public void initialize(ProfileView profileView, String idUser){
        this.setView(profileView);
        this.profileIdUser = idUser;
        loadCurrentUserListing();
    }

    public void setupOptionsMenu(Menu menu, MenuInflater inflater) {
        if(isCurrentUser()){
            profileView.createOptionsMenu(menu, inflater);
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

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }

    public void clickListing() {
        profileView.navigateToListing(profileIdUser);
    }

    private boolean isCurrentUser() {
        return profileIdUser != null && profileIdUser.equals(sessionRepository.getCurrentUserId());
    }

    public void logoutSelected() {
        profileView.showLogoutInProgress();
    }
}
