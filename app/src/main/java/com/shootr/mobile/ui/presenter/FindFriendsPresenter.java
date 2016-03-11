package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.FindParticipantsInteractor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.FindFriendsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class FindFriendsPresenter implements Presenter {

    private final FindParticipantsInteractor findParticipantsInteractor;
    private final FollowInteractor followInteractor;
    private final UnfollowInteractor unfollowInteractor;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private FindFriendsView findFriendsView;
    private List<UserModel> friends;
    private String query;
    private Boolean hasBeenPaused = false;

    @Inject public FindFriendsPresenter(FindParticipantsInteractor findParticipantsInteractor,
      FollowInteractor followInteractor, UnfollowInteractor unfollowInteractor, UserModelMapper userModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.findParticipantsInteractor = findParticipantsInteractor;
        this.followInteractor = followInteractor;
        this.unfollowInteractor = unfollowInteractor;
        this.userModelMapper = userModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(FindFriendsView findFriendsView) {
        this.findFriendsView = findFriendsView;
    }

    public void initialize(FindFriendsView findFriendsView) {
        this.setView(findFriendsView);
        this.friends = new ArrayList<>();
    }

    public void searchFriends(String query) {
        this.query = query;
        findFriendsView.hideEmpty();
        findFriendsView.hideContent();
        findFriendsView.hideKeyboard();
        findFriendsView.showLoading();
        findFriendsView.setCurrentQuery(query);
        //TODO same logic with the new interactor
        //findParticipantsInteractor.obtainAllParticipants(query, new Interactor.Callback<List<User>>() {
        //    @Override public void onLoaded(List<User> users) {
        //        findFriendsView.hideLoading();
        //        friends = userModelMapper.transform(users);
        //        if (!friends.isEmpty()) {
        //            findFriendsView.showContent();
        //            findFriendsView.renderFriends(friends);
        //        } else {
        //            findFriendsView.showEmpty();
        //        }
        //    }
        //}, new Interactor.ErrorCallback() {
        //    @Override public void onError(ShootrException error) {
        //        findFriendsView.hideLoading();
        //        findFriendsView.showError(errorMessageFactory.getMessageForError(error));
        //    }
        //});
    }

    public void refreshFriends() {
        findFriendsView.hideEmpty();
        //TODO same logic with the new interactor
        //findParticipantsInteractor.obtainAllParticipants(query, new Interactor.Callback<List<User>>() {
        //    @Override public void onLoaded(List<User> users) {
        //        friends = userModelMapper.transform(users);
        //        if (!friends.isEmpty()) {
        //            findFriendsView.renderFriends(friends);
        //        } else {
        //            findFriendsView.showEmpty();
        //        }
        //    }
        //}, new Interactor.ErrorCallback() {
        //    @Override public void onError(ShootrException error) {
        //        findFriendsView.hideLoading();
        //        findFriendsView.showError(errorMessageFactory.getMessageForError(error));
        //    }
        //});
    }

    public void followUser(final UserModel userModel) {
        followInteractor.follow(userModel.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                refreshFriendsFollowings(userModel.getIdUser(), FollowEntity.RELATIONSHIP_FOLLOWING);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        });
    }

    private void showErrorInView(ShootrException error) {
        findFriendsView.showError(errorMessageFactory.getMessageForError(error));
    }

    public void unfollowUser(final UserModel userModel) {
        unfollowInteractor.unfollow(userModel.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                refreshFriendsFollowings(userModel.getIdUser(), FollowEntity.RELATIONSHIP_NONE);
            }
        });
    }

    private void refreshFriendsFollowings(String idUser, int relationshipFollowing) {
        for (UserModel friend : friends) {
            if (friend.getIdUser().equals(idUser)) {
                friend.setRelationship(relationshipFollowing);
                findFriendsView.renderFriends(friends);
                break;
            }
        }
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            refreshFriends();
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }
}