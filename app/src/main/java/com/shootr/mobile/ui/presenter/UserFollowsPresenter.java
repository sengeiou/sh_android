package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.GetFollowingListInteractor;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.GetUserFollowersInteractor;
import com.shootr.mobile.domain.interactor.user.GetUserFollowingInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.model.Following;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.UserFollowsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class UserFollowsPresenter implements Presenter {

    public static final int FOLLOWERS = 0;

    private final GetUserFollowingInteractor getUserFollowingInteractor;
    private final GetUserFollowersInteractor getUserFollowersInteractor;
    private final GetFollowingListInteractor getFollowingListInteractor;
    private final FollowInteractor followInteractor;
    private final UnfollowInteractor unfollowInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final UserModelMapper userModelMapper;

    private UserFollowsView userFollowsView;
    private String userId;
    private Integer followType;
    private Integer page;
    private boolean isInLastPage = false;

    @Inject public UserFollowsPresenter(GetUserFollowingInteractor getUserFollowingInteractor,
        GetUserFollowersInteractor getUserFollowersInteractor,
        GetFollowingListInteractor getFollowingListInteractor, FollowInteractor followInteractor,
        UnfollowInteractor unfollowInteractor, ErrorMessageFactory errorMessageFactory,
        UserModelMapper userModelMapper) {
        this.getUserFollowingInteractor = getUserFollowingInteractor;
        this.getUserFollowersInteractor = getUserFollowersInteractor;
        this.getFollowingListInteractor = getFollowingListInteractor;
        this.followInteractor = followInteractor;
        this.unfollowInteractor = unfollowInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.userModelMapper = userModelMapper;
    }

    public void setView(UserFollowsView userFollowsView) {
        this.userFollowsView = userFollowsView;
    }

    public void initialize(UserFollowsView userFollowsView, String userId, Integer followType) {
        setView(userFollowsView);
        this.page = 0;
        this.userId = userId;
        this.followType = followType;
        retrieveUsers();
    }

    private void retrieveUsers() {
        userFollowsView.setLoadingView(true);
        if (followType.equals(FOLLOWERS)) {
            getFollowerUsers();
            userFollowsView.registerAnalytics(true);
        } else {
            getFollowingUsers();
            userFollowsView.registerAnalytics(false);
        }
    }

    private void getFollowingUsers() {
        /*getUserFollowingInteractor.obtainFollowing(userId, page, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                userFollowsView.setLoadingView(false);
                handleUsersInView(users);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                userFollowsView.setLoadingView(false);
                userFollowsView.showError(errorMessageFactory.getMessageForError(error));
            }
        });*/

        getFollowingListInteractor.getFollowingList(userId, 0L, new Interactor.Callback<Following>() {
            @Override public void onLoaded(Following following) {
                userFollowsView.setLoadingView(false);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {

            }
        });

    }

    private void getFollowerUsers() {
        getUserFollowersInteractor.obtainFollowers(userId, page, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                userFollowsView.setLoadingView(false);
                handleUsersInView(users);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                userFollowsView.setLoadingView(false);
                userFollowsView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public void handleUsersInView(List<User> users) {
        if (users.isEmpty()) {
            userFollowsView.setEmpty(true);
            handleEmptyMessageInView();
        } else {
            userFollowsView.showUsers(userModelMapper.transform(users));
        }
    }

    public void handleEmptyMessageInView() {
        if (showingFollowers()) {
            userFollowsView.showNoFollowers();
        } else {
            userFollowsView.showNoFollowing();
        }
    }

    public void follow(final UserModel user) {
        followInteractor.follow(user.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                userFollowsView.updateFollow(user.getIdUser(), true);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                userFollowsView.showUserBlockedError();
            }
        });
    }

    public void unfollow(final UserModel user) {
        unfollowInteractor.unfollow(user.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                userFollowsView.updateFollow(user.getIdUser(), false);
            }
        });
    }

    public void makeNextRemoteSearch() {
        page++;
        userFollowsView.showProgressView();
        if (!isInLastPage) {
            handleNextRemoteSearch();
        } else {
            userFollowsView.hideProgressView();
        }
    }

    public void handleNextRemoteSearch() {
        if (showingFollowers()) {
            nextFollowersSearch();
        } else {
            nextFollowingSearch();
        }
    }

    public void nextFollowingSearch() {
        getUserFollowingInteractor.obtainFollowing(userId, page, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                List<UserModel> olderUsers = userModelMapper.transform(users);
                if (!olderUsers.isEmpty()) {
                    userFollowsView.renderUsersBelow(olderUsers);
                } else {
                    isInLastPage = true;
                }
                userFollowsView.hideProgressView();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                userFollowsView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public void nextFollowersSearch() {
        getUserFollowersInteractor.obtainFollowers(userId, page, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                List<UserModel> olderUsers = userModelMapper.transform(users);
                if (!olderUsers.isEmpty()) {
                    userFollowsView.renderUsersBelow(olderUsers);
                } else {
                    isInLastPage = true;
                }
                userFollowsView.hideProgressView();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                userFollowsView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public boolean showingFollowers() {
        return followType == FOLLOWERS;
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        isInLastPage = false;
    }
}
