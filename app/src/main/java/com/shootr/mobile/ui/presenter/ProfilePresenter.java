package com.shootr.mobile.ui.presenter;

import android.support.annotation.NonNull;
import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.GetLastShotsInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ShareShotInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.GetBannedUsersInteractor;
import com.shootr.mobile.domain.interactor.user.GetBlockedIdUsersInteractor;
import com.shootr.mobile.domain.interactor.user.GetUserByIdInteractor;
import com.shootr.mobile.domain.interactor.user.GetUserByUsernameInteractor;
import com.shootr.mobile.domain.interactor.user.LogoutInteractor;
import com.shootr.mobile.domain.interactor.user.RemoveUserPhotoInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.interactor.user.UploadUserPhotoInteractor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.ProfileView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.shootr.mobile.util.UIObserver;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;

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
    private final GetBlockedIdUsersInteractor getBlockedIdUsersInteractor;
    private final GetBannedUsersInteractor getBannedUsersInteractor;
    private final SessionRepository sessionRepository;
    private final ErrorMessageFactory errorMessageFactory;
    private final UserModelMapper userModelMapper;
    private final ShotModelMapper shotModelMapper;
    private ProfileView profileView;
    private String profileIdUser;
    private boolean isCurrentUser;
    private String username;
    private UserModel userModel;
    private boolean hasBeenPaused = false;
    private boolean uploadingPhoto = false;
    private boolean isBlocked = false;
    private boolean isBanned = false;
    private int hadleBlockMenuCalls;

    @Inject public ProfilePresenter(GetUserByIdInteractor getUserByIdInteractor,
      GetUserByUsernameInteractor getUserByUsernameInteractor, LogoutInteractor logoutInteractor,
      MarkNiceShotInteractor markNiceShotInteractor, UnmarkNiceShotInteractor unmarkNiceShotInteractor,
      ShareShotInteractor shareShotInteractor, FollowInteractor followInteractor, UnfollowInteractor unfollowInteractor,
      GetLastShotsInteractor getLastShotsInteractor, UploadUserPhotoInteractor uploadUserPhotoInteractor,
      RemoveUserPhotoInteractor removeUserPhotoInteractor, GetBlockedIdUsersInteractor getBlockedIdUsersInteractor,
      GetBannedUsersInteractor getBannedUsersInteractor, SessionRepository sessionRepository, ErrorMessageFactory errorMessageFactory, UserModelMapper userModelMapper,
      ShotModelMapper shotModelMapper) {
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
        this.getBlockedIdUsersInteractor = getBlockedIdUsersInteractor;
        this.getBannedUsersInteractor = getBannedUsersInteractor;
        this.sessionRepository = sessionRepository;
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
        subscribeUIObserverToObservable(getLoadProfileObservable());
    }

    private void onProfileLoaded(User user) {
        this.isCurrentUser = user.isMe();
        this.setUserModel(userModelMapper.transform(user));
        setupMenuItemsVisibility();
        setupProfilePhoto(user);
        setRelationshipButtonStatus(user);
        setupVerifiedUserIcon();
        profileView.setUserInfo(userModel);
        profileView.showListing();
        renderStreamsNumber();
        profileView.setupAnalytics(isCurrentUser);
    }

    private void renderStreamsNumber() {
        Integer streamsCount = userModel.getFavoritedStreamsCount().intValue() + userModel.getCreatedStreamsCount().intValue();
        if (streamsCount > 0) {
            profileView.showStreamsCount();
            profileView.setStreamsCount(streamsCount);
        } else {
            profileView.hideStreamsCount();
        }
    }

    private void setupVerifiedUserIcon() {
        if (userModel.isVerifiedUser()) {
            profileView.showVerifiedUser();
        } else {
            profileView.hideVerifiedUser();
        }
    }

    private void setupProfilePhoto(final User user) {
        subscribeUIObserverToObservable(getProfilePhotoObservable(user));
    }

    private void setRelationshipButtonStatus(final User user) {
        subscribeUIObserverToObservable(getRelationshipButtonObservable(user));
    }

    private void loadLatestShots(final String idUser) {
        subscribeUIObserverToObservable(getLatestShotsObservable(idUser));
    }

    private List<Shot> getLimitedShotList(List<Shot> shotList) {
        if (shotList.size() > MAX_SHOTS_SHOWN) {
            return shotList.subList(0, MAX_SHOTS_SHOWN);
        } else {
            return shotList;
        }
    }

    protected void setupMenuItemsVisibility() {
        subscribeUIObserverToObservable(getMenuItemsVisibilityObservable());
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
                updateFollowingsInfo();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
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
                updateFollowingsInfo();
            }
        });
    }

    public void updateFollowingsInfo() {
        getUserByIdInteractor.loadUserById(profileIdUser, new Interactor.Callback<User>() {
            @Override public void onLoaded(User user) {
                setUserModel(userModelMapper.transform(user));
                profileView.setUserInfo(userModel);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        });
    }

    public void avatarClicked() {
        if (userModel != null && !isCurrentUser && userModel.getPhoto()  != null) {
            profileView.openPhoto(userModel.getPhoto());
        } else if (userModel != null && isCurrentUser) {
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
        if (userModel != null) {
            profileView.goToFollowersList(userModel.getIdUser());
        }
    }

    public void followingButtonClicked() {
        if (userModel != null) {
            profileView.goToFollowingList(userModel.getIdUser());
        }
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
                refreshProfile();
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
                refreshProfile();
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

    private void subscribeUIObserverToObservable(Observable<Void> observable) {
        observable.subscribe(new UIObserver<Void>() {
        });
    }

    private void refreshProfile() {
        loadProfileUser();
        loadLatestShots(userModel.getIdUser());
    }

    @NonNull private Observable<Void> getLoadProfileObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
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
        });
    }

    @NonNull private Observable<Void> getProfilePhotoObservable(final User user) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                if (isCurrentUser && user.getPhoto() == null) {
                    profileView.showAddPhoto();
                }
                subscriber.onCompleted();
            }
        });
    }

    @NonNull private Observable<Void> getRelationshipButtonObservable(final User user) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                if (isCurrentUser) {
                    profileView.showEditProfileButton();
                } else {
                    if (!user.isFollowing()) {
                        profileView.showFollowButton();
                    } else {
                        profileView.showUnfollowButton();
                    }
                }
                subscriber.onCompleted();
            }
        });
    }

    @NonNull private Observable<Void> getLatestShotsObservable(final String idUser) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
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
        });
    }

    @NonNull private Observable<Void> getMenuItemsVisibilityObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                if (isCurrentUser) {
                    profileView.showLogoutButton();
                    profileView.showSupportButton();
                    profileView.showChangePasswordButton();
                } else {
                    profileView.showReportUserButton();
                    profileView.showBlockUserButton();
                }
                subscriber.onCompleted();
            }
        });
    }

    @NonNull private Observable<Void> getBlockedIdsObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                getBlockedIdUsersInteractor.loadBlockedIdUsers(new Interactor.Callback<List<String>>() {
                    @Override public void onLoaded(final List<String> blockedIds) {
                        hadleBlockMenuCalls++;
                        isBlocked = blockedIds.contains(userModel.getIdUser());
                        handleBlockMenu(isBlocked, isBanned);
                    }
                }, new Interactor.ErrorCallback() {
                    @Override public void onError(ShootrException error) {
                        showErrorInView(error);
                    }
                });
            }
        });
    }

    @NonNull private Observable<Void> getBannedIdsObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                getBannedUsersInteractor.loadBannedIdUsers(new Interactor.Callback<List<String>>() {
                    @Override public void onLoaded(List<String> bannedIds) {
                        hadleBlockMenuCalls++;
                        isBanned = bannedIds.contains(userModel.getIdUser());
                        handleBlockMenu(isBlocked, isBanned);
                    }
                }, new Interactor.ErrorCallback() {
                    @Override public void onError(ShootrException error) {
                        showErrorInView(error);
                    }
                });
            }
        });
    }

    public void blockMenuClicked() {
        hadleBlockMenuCalls = 0;
        subscribeUIObserverToObservable(getBlockedIdsObservable());
        subscribeUIObserverToObservable(getBannedIdsObservable());
    }

    private void handleBlockMenu(Boolean isBlocked, Boolean isBanned) {
        if (hadleBlockMenuCalls == 2) {
            if (!isBlocked && !isBanned) {
                profileView.showDefaultBlockMenu(userModel);
            } else if (isBlocked && !isBanned) {
                profileView.showBlockedMenu(userModel);
            } else if (!isBlocked) {
                profileView.showBannedMenu(userModel);
            } else {
                profileView.showBlockAndBannedMenu(userModel);
            }
        }
    }

    public void unblockUserClicked() {
        profileView.unblockUser(userModel);
    }

    public void blockUserClicked() {
        profileView.blockUser(userModel);
    }

    public void reportUserClicked() {
        profileView.goToReportEmail(sessionRepository.getCurrentUserId(), userModel.getIdUser());
    }

    public void banUserClicked() {
        profileView.showBanUserConfirmation(userModel);
    }

    public void unbanUserClicked() {
        profileView.showUnbanUserConfirmation(userModel);
    }

    @Override public void resume() {
        if (hasBeenPaused && userModel != null && !uploadingPhoto) {
            refreshProfile();
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }
}
