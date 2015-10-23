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
import com.shootr.android.domain.interactor.user.RemoveUserPhotoInteractor;
import com.shootr.android.domain.interactor.user.UnfollowInteractor;
import com.shootr.android.domain.interactor.user.UploadUserPhotoInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.ProfileView;
import com.shootr.android.util.ErrorMessageFactory;
import java.io.File;
import java.util.List;
import javax.inject.Inject;

public class ProfilePresenter implements Presenter {

    public static final int ALL_SHOTS_VISIBILITY_TRESHOLD = 4;
    public static final int MAX_SHOTS_SHOWN = 3;
    private final GetUserByIdInteractor getUserByIdInteractor;
    private final GetUserByUsernameInteractor getUserByUsernameInteractor;
    private final LogoutInteractor logoutInteractor;
    private final MarkNiceShotInteractor markNiceShotInteractor;
    private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    private final ShareShotInteractor shareShotInteractor;
    private final FollowInteractor followInteractor;
    private final UnfollowInteractor unfollowInteractor;
    private final GetLastShotsInteractor getLastShotsInteractor;
    private final UploadUserPhotoInteractor uploadUserPhotoInteractor;
    private final RemoveUserPhotoInteractor removeUserPhotoInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final UserModelMapper userModelMapper;
    private final ShotModelMapper shotModelMapper;
    private ProfileView profileView;
    private String profileIdUser;
    private boolean isCurrentUser;
    private Long streamsCount;
    private String username;
    private UserModel userModel;
    private boolean hasBeenPaused = false;
    private boolean uploadingPhoto = false;

    @Inject public ProfilePresenter(GetUserByIdInteractor getUserByIdInteractor,
      GetUserByUsernameInteractor getUserByUsernameInteractor, LogoutInteractor logoutInteractor,
      MarkNiceShotInteractor markNiceShotInteractor, UnmarkNiceShotInteractor unmarkNiceShotInteractor,
      ShareShotInteractor shareShotInteractor, FollowInteractor followInteractor, UnfollowInteractor unfollowInteractor,
      GetLastShotsInteractor getLastShotsInteractor, UploadUserPhotoInteractor uploadUserPhotoInteractor,
      RemoveUserPhotoInteractor removeUserPhotoInteractor, ErrorMessageFactory errorMessageFactory, UserModelMapper userModelMapper, ShotModelMapper shotModelMapper) {
        this.getUserByIdInteractor = getUserByIdInteractor;
        this.getUserByUsernameInteractor = getUserByUsernameInteractor;
        this.logoutInteractor = logoutInteractor;
        this.markNiceShotInteractor = markNiceShotInteractor;
        this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
        this.shareShotInteractor = shareShotInteractor;
        this.followInteractor = followInteractor;
        this.unfollowInteractor = unfollowInteractor;
        this.getLastShotsInteractor = getLastShotsInteractor;
        this.uploadUserPhotoInteractor = uploadUserPhotoInteractor;
        this.removeUserPhotoInteractor = removeUserPhotoInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.userModelMapper = userModelMapper;
        this.shotModelMapper = shotModelMapper;
    }

    protected void setView(ProfileView profileView) {
        this.profileView = profileView;
    }

    protected void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public void initializeWithUsername(ProfileView profileView, String username) {
        this.username = username;
        initialize(profileView);
    }

    public void initializeWithIdUser(ProfileView profileView, String idUser) {
        this.profileIdUser = idUser;
        initialize(profileView);
        loadLatestShots(idUser);
    }

    private void initialize(ProfileView profileView) {
        this.setView(profileView);
        loadProfileUser();
    }

    private void loadProfileUser() {
        if (profileIdUser != null) {
            getUserByIdInteractor.loadUserById(profileIdUser, new Interactor.Callback<User>() {
                @Override public void onLoaded(User user) {
                    onProfileLoaded(user);
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    showErrorInView(error);
                }
            });
        } else {
            getUserByUsernameInteractor.searchUserByUsername(username, new Interactor.Callback<User>() {
                @Override public void onLoaded(User user) {
                    profileIdUser = user.getIdUser();
                    onProfileLoaded(user);
                    loadLatestShots(userModel.getIdUser());
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    showErrorInView(error);
                }
            });
        }
    }

    private void onProfileLoaded(User user) {
        this.isCurrentUser = user.isMe();
        setupMenuItemsVisibility();
        this.setUserModel(userModelMapper.transform(user));
        profileView.setUserInfo(userModel);
        loadProfileUserListing();
        setRelationshipButtonStatus(user);
        if (isCurrentUser && user.getPhoto() == null) {
            profileView.showAddPhoto();
        }
        profileView.setupAnalytics(isCurrentUser);
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

    private void loadLatestShots(String idUser) {
        getLastShotsInteractor.loadLastShots(idUser, new Interactor.Callback<List<Shot>>() {
            @Override public void onLoaded(List<Shot> shotList) {
                if (!shotList.isEmpty()) {
                    profileView.showLatestShots();
                    profileView.hideLatestShotsEmpty();
                    if (shotList.size() == ALL_SHOTS_VISIBILITY_TRESHOLD) {
                        profileView.showAllShotsButton();
                    } else {
                        profileView.hideAllShotsButton();
                    }
                    profileView.renderLastShots(shotModelMapper.transform(getLimitedShotList(shotList)));
                } else {
                    profileView.hideLatestShots();
                    profileView.showLatestShotsEmpty();
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        });
    }

    private List<Shot> getLimitedShotList(List<Shot> shotList) {
        if (shotList.size() > MAX_SHOTS_SHOWN) {
            return shotList.subList(0, MAX_SHOTS_SHOWN);
        } else {
            return shotList;
        }
    }

    protected void setupMenuItemsVisibility() {
        if (isCurrentUser) {
            profileView.showLogoutButton();
            profileView.showSupportButton();
            profileView.showChangePasswordButton();
        }
    }

    private void loadProfileUserListing() {
        streamsCount = userModel.getCreatedStreamsCount() + userModel.getFavoritedStreamsCount();
        showUserListingCount();
    }

    private void showUserListingCount() {
        if (streamsCount > 0L) {
            profileView.showListingButtonWithCount(streamsCount.intValue());
        } else {
            profileView.showListingWithoutCount();
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
                loadLatestShots(ProfilePresenter.this.userModel.getIdUser());
            }
        });
    }

    public void unmarkNiceShot(String idShot) {
        unmarkNiceShotInteractor.unmarkNiceShot(idShot, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadLatestShots(ProfilePresenter.this.userModel.getIdUser());
            }
        });
    }

    public void streamCreated(String streamId) {
        profileView.navigateToCreatedStreamDetail(streamId);
    }

    public void refreshLatestShots() {
        loadLatestShots(userModel.getIdUser());
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
        profileView.showUnfollowConfirmation(userModel.getUsername());
    }

    public void confirmUnfollow() {
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
        String website = userModel.getWebsite();
        String httpPrefix = "http://";
        String httpsPrefix = "https://";
        if (!website.contains(httpPrefix) && !website.contains(httpsPrefix)) {
            website = httpPrefix + website;
        }
        profileView.goToWebsite(website);
    }

    public void followersButtonClicked() {
        profileView.goToFollowersList(userModel.getIdUser());
    }

    public void followingButtonClicked() {
        profileView.goToFollowingList(userModel.getIdUser());
    }

    public void allShotsClicked() {
        profileView.goToAllShots(userModel.getIdUser());
    }

    public void uploadPhoto(File changedPhotoFile) {
        this.uploadingPhoto = true;
        profileView.showLoadingPhoto();
        uploadUserPhotoInteractor.uploadUserPhoto(changedPhotoFile, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                uploadingPhoto = false;
                loadProfileUser();
                loadLatestShots(userModel.getIdUser());
                profileView.hideLoadingPhoto();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                uploadingPhoto = false;
                showErrorInView(error);
                profileView.hideLoadingPhoto();
            }
        });
    }

    public void removePhoto() {
        profileView.showRemovePhotoConfirmation();
    }

    public void removePhotoConfirmed() {
        removeUserPhotoInteractor.removeUserPhoto(new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadProfileUser();
                loadLatestShots(userModel.getIdUser());
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        });
    }

    private void showErrorInView(ShootrException error) {
        profileView.showError(errorMessageFactory.getMessageForError(error));
    }

    @Override public void resume() {
        if (hasBeenPaused && userModel != null && !uploadingPhoto) {
            loadProfileUser();
            loadLatestShots(userModel.getIdUser());
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }
}
