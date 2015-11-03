package com.shootr.android.ui.presenter;

import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.FindParticipantsInteractor;
import com.shootr.android.domain.interactor.user.FollowInteractor;
import com.shootr.android.domain.interactor.user.UnfollowInteractor;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.FindParticipantsView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class FindParticipantsPresenter implements Presenter {

    private final FindParticipantsInteractor findParticipantsInteractor;
    private final FollowInteractor followInteractor;
    private final UnfollowInteractor unfollowInteractor;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private FindParticipantsView findParticipantsView;
    private String idStream;
    private List<UserModel> participants;
    private String query;
    private Boolean hasBeenPaused = false;

    @Inject public FindParticipantsPresenter(FindParticipantsInteractor findParticipantsInteractor,
      FollowInteractor followInteractor, UnfollowInteractor unfollowInteractor, UserModelMapper userModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.findParticipantsInteractor = findParticipantsInteractor;
        this.followInteractor = followInteractor;
        this.unfollowInteractor = unfollowInteractor;
        this.userModelMapper = userModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(FindParticipantsView findParticipantsView) {
        this.findParticipantsView = findParticipantsView;
    }

    public void initialize(FindParticipantsView findParticipantsView, String idStream) {
        this.setView(findParticipantsView);
        this.idStream = idStream;
        this.participants = new ArrayList<>();
    }

    public void searchParticipants(String query) {
        this.query = query;
        findParticipantsView.hideEmpty();
        findParticipantsView.hideContent();
        findParticipantsView.hideKeyboard();
        findParticipantsView.showLoading();
        findParticipantsView.setCurrentQuery(query);
        findParticipantsInteractor.obtainAllParticipants(idStream, query, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                findParticipantsView.hideLoading();
                participants = userModelMapper.transform(users);
                if (!participants.isEmpty()) {
                    findParticipantsView.showContent();
                    findParticipantsView.renderParticipants(participants);
                } else {
                    findParticipantsView.showEmpty();
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                findParticipantsView.hideLoading();
                findParticipantsView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public void refreshParticipants() {
        findParticipantsView.hideEmpty();
        findParticipantsInteractor.obtainAllParticipants(idStream, query, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                participants = userModelMapper.transform(users);
                if (!participants.isEmpty()) {
                    findParticipantsView.renderParticipants(participants);
                } else {
                    findParticipantsView.showEmpty();
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                findParticipantsView.hideLoading();
                findParticipantsView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public void followUser(final UserModel userModel) {
        followInteractor.follow(userModel.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                refreshParticipantsFollowings(userModel.getIdUser(), FollowEntity.RELATIONSHIP_FOLLOWING);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        });
    }

    private void showErrorInView(ShootrException error) {
        findParticipantsView.showError(errorMessageFactory.getMessageForError(error));
    }

    public void unfollowUser(final UserModel userModel) {
        unfollowInteractor.unfollow(userModel.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                refreshParticipantsFollowings(userModel.getIdUser(), FollowEntity.RELATIONSHIP_NONE);
            }
        });
    }

    private void refreshParticipantsFollowings(String idUser, int relationshipFollowing) {
        for (UserModel participant : participants) {
            if (participant.getIdUser().equals(idUser)) {
                participant.setRelationship(relationshipFollowing);
                findParticipantsView.renderParticipants(participants);
                break;
            }
        }
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            refreshParticipants();
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }
}
