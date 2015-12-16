package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.GetUserFollowingInteractor;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.UserFollowsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class UserFollowsPresenter implements Presenter {

    public static final int FOLLOWING = 1;
    public static final int FOLLOWERS = 0;

    private final GetUserFollowingInteractor getUserFollowingInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final UserModelMapper userModelMapper;

    private UserFollowsView userFollowsView;
    private String userId;
    private Integer followType;

    @Inject public UserFollowsPresenter(GetUserFollowingInteractor getUserFollowingInteractor,
      ErrorMessageFactory errorMessageFactory, UserModelMapper userModelMapper) {
        this.getUserFollowingInteractor = getUserFollowingInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.userModelMapper = userModelMapper;
    }

    protected void setView(UserFollowsView userFollowsView) {
        this.userFollowsView = userFollowsView;
    }

    public void initialize(UserFollowsView userFollowsView, String userId, Integer followType) {
        setView(userFollowsView);
        this.userId = userId;
        this.followType = followType;
        retrieveUsers();
    }

    private void retrieveUsers() {
        userFollowsView.setLoadingView(true);
        if (followType.equals(FOLLOWERS)) {
            getFollowerUsers();
        } else {
            getFollowingUsers();
        }
    }

    private void getFollowingUsers() {
        getUserFollowingInteractor.obtainFollowing(userId, new Interactor.Callback<List<User>>() {
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
        } else {
            userFollowsView.showUsers(userModelMapper.transform(users));
        }
    }

    private void getFollowerUsers() {

    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
