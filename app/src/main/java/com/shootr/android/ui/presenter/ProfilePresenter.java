package com.shootr.android.ui.presenter;

import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetListingCountInteractor;
import com.shootr.android.ui.views.ProfileView;
import javax.inject.Inject;

public class ProfilePresenter implements Presenter {

    private final GetListingCountInteractor getListingCountInteractor;
    private ProfileView profileView;
    private String profileIdUser;

    @Inject public ProfilePresenter(GetListingCountInteractor getListingCountInteractor) {
        this.getListingCountInteractor = getListingCountInteractor;
    }

    protected void setView(ProfileView profileView){
        this.profileView = profileView;
    }

    public void initialize(ProfileView profileView, String idUser){
        this.setView(profileView);
        this.profileIdUser = idUser;
        loadCurrentUserListing();
    }

    public void loadCurrentUserListing() {
        getListingCountInteractor.getListingCount(profileIdUser, new Interactor.Callback<Integer>() {
            @Override public void onLoaded(Integer numberOfListingEvents) {
                if (numberOfListingEvents > 0) {
                    profileView.showListingCount(numberOfListingEvents);
                }
            }
        });
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }

    public void clickListing() {
        profileView.navigateToListing(profileIdUser);
    }
}
