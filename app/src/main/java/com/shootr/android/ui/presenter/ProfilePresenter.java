package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.android.domain.interactor.shot.ShareShotInteractor;
import com.shootr.android.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.android.domain.interactor.stream.GetListingCountInteractor;
import com.shootr.android.domain.interactor.user.FollowInteractor;
import com.shootr.android.domain.interactor.user.LogoutInteractor;
import com.shootr.android.domain.interactor.user.UnfollowInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.views.ProfileView;
import com.shootr.android.util.ErrorMessageFactory;
import javax.inject.Inject;

public class ProfilePresenter implements Presenter {

    private final GetListingCountInteractor getListingCountInteractor;
    private final LogoutInteractor logoutInteractor;
    private final MarkNiceShotInteractor markNiceShotInteractor;
    private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    private final ShareShotInteractor shareShotInteractor;
    private final FollowInteractor followInteractor;
    private final UnfollowInteractor unfollowInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private ProfileView profileView;
    private String profileIdUser;
    private Boolean isCurrentUser;

    @Inject public ProfilePresenter(GetListingCountInteractor getListingCountInteractor,
      LogoutInteractor logoutInteractor,
      MarkNiceShotInteractor markNiceShotInteractor,
      UnmarkNiceShotInteractor unmarkNiceShotInteractor,
      ShareShotInteractor shareShotInteractor,
      FollowInteractor followInteractor,
      UnfollowInteractor unfollowInteractor,
      ErrorMessageFactory errorMessageFactory) {
        this.getListingCountInteractor = getListingCountInteractor;
        this.logoutInteractor = logoutInteractor;
        this.markNiceShotInteractor = markNiceShotInteractor;
        this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
        this.shareShotInteractor = shareShotInteractor;
        this.followInteractor = followInteractor;
        this.unfollowInteractor = unfollowInteractor;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(ProfileView profileView){
        this.profileView = profileView;
    }

    public void initialize(ProfileView profileView, String idUser, boolean isCurrentUser){
        this.setView(profileView);
        this.profileIdUser = idUser;
        setCurrentUser(isCurrentUser);
        loadCurrentUserListing();
        setupMenuItemsVisibility();
    }

    protected void setCurrentUser(boolean isCurrentUser) {
        this.isCurrentUser = isCurrentUser;
    }

    protected void setupMenuItemsVisibility() {
        if(isCurrentUser){
            profileView.showLogoutButton();
            profileView.showSupportButton();
            profileView.showChangePasswordButton();
        }
    }

    public void loadCurrentUserListing() {
        getListingCountInteractor.loadListingCount(profileIdUser, new Interactor.Callback<Integer>() {
            @Override public void onLoaded(Integer numberOfListingStreams) {
                if (numberOfListingStreams > 0) {
                    profileView.showListingCount(numberOfListingStreams);
                } else {
                    showCurrentUserOpenStream();
                }
            }
        });
    }

    private void showCurrentUserOpenStream() {
        if(isCurrentUser){
            profileView.showOpenStream();
        }
    }

    public void clickListing() {
        profileView.navigateToListing(profileIdUser);
    }

    public void logoutSelected() {
        profileView.showLogoutInProgress();
        logoutInteractor.attempLogout(new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                profileView.navigateToWelcomeScreen();
            }
        }, new Interactor.ErrorCallback() {
            @Override
            public void onError(ShootrException error) {
                profileView.hideLogoutInProgress();
                profileView.showError();
            }
        });
    }

    public void markNiceShot(String idShot) {
        markNiceShotInteractor.markNiceShot(idShot, new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                profileView.loadLastShots();
            }
        });
    }

    public void unmarkNiceShot(String idShot) {
        unmarkNiceShotInteractor.unmarkNiceShot(idShot, new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                profileView.loadLastShots();
            }
        });
    }

    public void streamCreated(String streamId) {
        profileView.navigateToCreatedStreamDetail(streamId);
    }

    public void shareShot(ShotModel shotModel) {
        shareShotInteractor.shareShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                profileView.showShotShared();
            }
        }, new Interactor.ErrorCallback() {
            @Override
            public void onError(ShootrException error) {
                profileView.showError();
            }
        });
    }

    public void follow() {
        followInteractor.follow(profileIdUser, new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                profileView.setFollowing(true);
            }
        });
    }

    public void unfollow() {
        unfollowInteractor.unfollow(profileIdUser, new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                profileView.setFollowing(false);
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
