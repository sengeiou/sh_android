package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.GetLastShotsInteractor;
import com.shootr.android.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.android.domain.interactor.shot.ShareShotInteractor;
import com.shootr.android.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.android.domain.interactor.user.FollowInteractor;
import com.shootr.android.domain.interactor.user.GetUserByIdInteractor;
import com.shootr.android.domain.interactor.user.GetUserByUsernameInteractor;
import com.shootr.android.domain.interactor.user.LogoutInteractor;
import com.shootr.android.domain.interactor.user.UnfollowInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.ProfileView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class ProfilePresenter implements Presenter {

    private final GetUserByIdInteractor getUserByIdInteractor;
    private final GetUserByUsernameInteractor getUserByUsernameInteractor;
    private final LogoutInteractor logoutInteractor;
    private final MarkNiceShotInteractor markNiceShotInteractor;
    private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    private final ShareShotInteractor shareShotInteractor;
    private final FollowInteractor followInteractor;
    private final UnfollowInteractor unfollowInteractor;
    private final GetLastShotsInteractor getLastShotsInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final UserModelMapper userModelMapper;
    private ProfileView profileView;
    private String profileIdUser;
    private boolean isCurrentUser;
    private Long streamsCount;
    private String username;
    private UserModel userModel;

    @Inject public ProfilePresenter(GetUserByIdInteractor getUserByIdInteractor,
      GetUserByUsernameInteractor getUserByUsernameInteractor, LogoutInteractor logoutInteractor,
      MarkNiceShotInteractor markNiceShotInteractor, UnmarkNiceShotInteractor unmarkNiceShotInteractor,
      ShareShotInteractor shareShotInteractor, FollowInteractor followInteractor, UnfollowInteractor unfollowInteractor,
      GetLastShotsInteractor getLastShotsInteractor, ErrorMessageFactory errorMessageFactory, UserModelMapper userModelMapper) {
        this.getUserByIdInteractor = getUserByIdInteractor;
        this.getUserByUsernameInteractor = getUserByUsernameInteractor;
        this.logoutInteractor = logoutInteractor;
        this.markNiceShotInteractor = markNiceShotInteractor;
        this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
        this.shareShotInteractor = shareShotInteractor;
        this.followInteractor = followInteractor;
        this.unfollowInteractor = unfollowInteractor;
        this.getLastShotsInteractor = getLastShotsInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.userModelMapper = userModelMapper;
    }

    protected void setView(ProfileView profileView){
        this.profileView = profileView;
    }

    protected void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public void initializeWithUsername(ProfileView profileView, String username){
        this.username = username;
        initialize(profileView);
    }

    public void initializeWithIdUser(ProfileView profileView, String idUser){
        this.profileIdUser = idUser;
        initialize(profileView);
    }

    private void initialize(ProfileView profileView) {
        this.setView(profileView);
        loadProfileUser();
        setupMenuItemsVisibility();
    }

    private void loadProfileUser() {
        if (profileIdUser != null) {
            getUserByIdInteractor.loadUserById(profileIdUser, new Interactor.Callback<User>() {
                @Override public void onLoaded(User user) {
                    loadProfileInfo(user);
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    showErrorInView(error);
                }
            });
        } else {
            getUserByUsernameInteractor.searchUserByUsername(username, new Interactor.Callback<User>() {
                @Override public void onLoaded(User user) {
                    loadProfileInfo(user);
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    showErrorInView(error);
                }
            });
        }
    }

    private void showErrorInView(ShootrException error) {
        profileView.showError(errorMessageFactory.getMessageForError(error));
    }

    private void loadProfileInfo(User user) {
        this.isCurrentUser = user.isMe();
        this.setUserModel(userModelMapper.transform(user));
        profileView.setUserInfo(userModel);
        loadProfileUserListing();
        loadLatestShots();
        setRelationshipButtonStatus(user);
        if (isCurrentUser && user.getPhoto() == null) {
            profileView.showAddPhoto();
        }
    }

    private void setRelationshipButtonStatus(User user) {
        if (isCurrentUser) {
            profileView.showEditProfileButton();
        } else {
            if (!user.isFollowing()) {
                profileView.showFollowButton();
            } else {
                profileView.showUnfollowButton();
            }
        }
    }

    private void loadLatestShots() {
        getLastShotsInteractor.loadLastShots(userModel.getIdUser(), new Interactor.Callback<List<Shot>>() {
            @Override public void onLoaded(List<Shot> shotList) {
                //TODO remove magic number
                if (shotList.size() == 4) {
                    profileView.showAllShots();
                } else {
                    profileView.hideAllShots();
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        });
    }

    protected void setupMenuItemsVisibility() {
        if(isCurrentUser){
            profileView.showLogoutButton();
            profileView.showSupportButton();
            profileView.showChangePasswordButton();
        }
    }

    private void loadProfileUserListing() {
        if (isCurrentUser) {
            streamsCount = userModel.getCreatedStreamsCount();
            showCurrentUserListingCount();
        } else {
            streamsCount = userModel.getCreatedStreamsCount() + userModel.getFavoritedStreamsCount();
            showAnotherUserListingCount();
        }
    }

    private void showCurrentUserListingCount() {
        if (streamsCount > 0L) {
            profileView.showListingButtonWithCount(streamsCount.intValue());
        } else {
            showCurrentUserOpenStream();
        }
    }

    private void showAnotherUserListingCount() {
        if (streamsCount > 0L) {
            profileView.showListingButtonWithCount(streamsCount.intValue());
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
        profileView.navigateToListing(profileIdUser, isCurrentUser);
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
                profileView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public void markNiceShot(String idShot) {
        markNiceShotInteractor.markNiceShot(idShot, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadLatestShots();
            }
        });
    }

    public void unmarkNiceShot(String idShot) {
        unmarkNiceShotInteractor.unmarkNiceShot(idShot, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadLatestShots();
            }
        });
    }

    public void streamCreated(String streamId) {
        profileView.navigateToCreatedStreamDetail(streamId);
    }

    public void refreshLatestShots() {
        loadLatestShots();
    }

    public void shareShot(ShotModel shotModel) {
        shareShotInteractor.shareShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                profileView.showShotShared();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                profileView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public void follow() {
        followInteractor.follow(profileIdUser, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                profileView.setFollowing(true);
            }
        });
    }

    public void unfollow() {
        unfollowInteractor.unfollow(profileIdUser, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                profileView.setFollowing(false);
            }
        });
    }

    public void avatarClicked() {
        if (!isCurrentUser) {
            profileView.openPhoto(userModel.getPhoto());
        } else {
            boolean shouldShowRemovePhoto = userModel.getPhoto() != null;
            profileView.openEditPhotoMenu(shouldShowRemovePhoto);
        }
    }

    public void websiteClicked() {
        profileView.goToWebsite(userModel.getWebsite());
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }

    public void followersButtonClicked() {
        profileView.goToFollowersList(userModel.getIdUser());
    }

    public void followingButtonClicked() {
        profileView.goToFollowingList(userModel.getIdUser());
    }
}
