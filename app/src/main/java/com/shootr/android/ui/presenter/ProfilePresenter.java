package com.shootr.android.ui.presenter;

import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetUserListingEventsNumberInteractor;
import com.shootr.android.ui.views.ProfileView;
import javax.inject.Inject;

public class ProfilePresenter implements Presenter {

    private final GetUserListingEventsNumberInteractor getUserListingEventsNumberInteractor;

    private ProfileView profileView;

    @Inject public ProfilePresenter(GetUserListingEventsNumberInteractor getUserListingEventsNumberInteractor) {
        this.getUserListingEventsNumberInteractor = getUserListingEventsNumberInteractor;
    }

    protected void setView(ProfileView profileView){
        this.profileView = profileView;
    }

    public void initialize(ProfileView profileView){
        this.setView(profileView);
        getCurrentUserListing();
    }

    public void getCurrentUserListing() {
        getUserListingEventsNumberInteractor.getUserListingEventsNumber(new Interactor.Callback<Integer>() {
            @Override public void onLoaded(Integer numberOfListingEvents) {
                if(numberOfListingEvents>0){
                    profileView.showListing(numberOfListingEvents);
                }
            }
        });
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
