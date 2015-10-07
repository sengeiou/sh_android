package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.android.domain.interactor.shot.ShareShotInteractor;
import com.shootr.android.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.android.domain.interactor.user.FollowInteractor;
import com.shootr.android.domain.interactor.user.LogoutInteractor;
import com.shootr.android.domain.interactor.user.UnfollowInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.views.ProfileView;
import com.shootr.android.util.ErrorMessageFactory;
import javax.inject.Inject;

public class ProfilePresenter implements Presenter {

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
    private Long streamsCount;

    @Inject public ProfilePresenter(LogoutInteractor logoutInteractor,
      MarkNiceShotInteractor markNiceShotInteractor, UnmarkNiceShotInteractor unmarkNiceShotInteractor,
      ShareShotInteractor shareShotInteractor, FollowInteractor followInteractor, UnfollowInteractor unfollowInteractor,
      ErrorMessageFactory errorMessageFactory) {
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

    public void loadCurrentUserListing(UserModel userModel) {
        streamsCount = userModel.getCreatedStreamsCount() + userModel.getFavoritedStreamsCount();
        if (isCurrentUser) {
            showCurrentUserListingCount();
        } else {
            showAnotherUserListingCount();
        }
    }

    private void showCurrentUserListingCount() {
        if (streamsCount > 0L) {
            profileView.showListingWithCount(streamsCount.intValue());
        } else {
            showCurrentUserOpenStream();
        }
    }

    private void showAnotherUserListingCount() {
        if (streamsCount > 0L) {
            profileView.showListingWithCount(streamsCount.intValue());
        } else {
            profileView.showListingWithoutCount();
        }
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
