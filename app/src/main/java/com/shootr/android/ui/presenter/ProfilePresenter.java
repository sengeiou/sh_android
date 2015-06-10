package com.shootr.android.ui.presenter;

import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetUserListingEventsNumberInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.views.ProfileView;
import javax.inject.Inject;

public class ProfilePresenter implements Presenter {

    private final GetUserListingEventsNumberInteractor getUserListingEventsNumberInteractor;
    private final SessionRepository sessionRepository;
    private ProfileView profileView;
    private String idUser;
    private Integer listingCount;

    @Inject public ProfilePresenter(GetUserListingEventsNumberInteractor getUserListingEventsNumberInteractor,
      SessionRepository sessionRepository) {
        this.getUserListingEventsNumberInteractor = getUserListingEventsNumberInteractor;
        this.sessionRepository = sessionRepository;
    }

    protected void setView(ProfileView profileView){
        this.profileView = profileView;
    }

    public void initialize(ProfileView profileView){
        this.setView(profileView);
        this.idUser = sessionRepository.getCurrentUserId();
        getCurrentUserListing();
    }

    public void getCurrentUserListing() {
        getUserListingEventsNumberInteractor.getUserListingEventsNumber(new Interactor.Callback<Integer>() {
            @Override public void onLoaded(Integer numberOfListingEvents) {
                listingCount = numberOfListingEvents;
                if (numberOfListingEvents > 0) {
                    profileView.showListing(numberOfListingEvents);
                }
            }
        });
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }

    public void clickListing() {
        profileView.navigateToListing(idUser);
    }
}
