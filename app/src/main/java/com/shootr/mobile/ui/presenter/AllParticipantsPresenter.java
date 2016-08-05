package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.GetAllParticipantsInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.AllParticipantsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class AllParticipantsPresenter implements Presenter {

    private final GetAllParticipantsInteractor getAllParticipantsInteractor;
    private final SelectStreamInteractor selectStreamInteractor;
    private final FollowInteractor followInteractor;
    private final UnfollowInteractor unfollowInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final UserModelMapper userModelMapper;

    private AllParticipantsView allParticipantsView;
    private List<UserModel> participants;
    private Boolean hasBeenPaused = false;
    private String idStream;

    @Inject public AllParticipantsPresenter(GetAllParticipantsInteractor getAllParticipantsInteractor,
      SelectStreamInteractor selectStreamInteractor, FollowInteractor followInteractor,
      UnfollowInteractor unfollowInteractor, ErrorMessageFactory errorMessageFactory, UserModelMapper userModelMapper) {
        this.getAllParticipantsInteractor = getAllParticipantsInteractor;
        this.selectStreamInteractor = selectStreamInteractor;
        this.followInteractor = followInteractor;
        this.unfollowInteractor = unfollowInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.userModelMapper = userModelMapper;
    }

    protected void setView(AllParticipantsView allParticipantsView) {
        this.allParticipantsView = allParticipantsView;
    }

    public void initialize(AllParticipantsView allParticipantsView, String idStream) {
        this.setView(allParticipantsView);
        this.idStream = idStream;
        this.loadAllParticipants();
    }

    private void loadAllParticipants() {
        allParticipantsView.hideEmpty();
        allParticipantsView.showLoading();
        getAllParticipantsInteractor.obtainAllParticipants(idStream,
          Long.MAX_VALUE,
          false,
          new Interactor.Callback<List<User>>() {
              @Override public void onLoaded(List<User> users) {
                  allParticipantsView.hideLoading();
                  allParticipantsView.showAllParticipantsList();
                  renderParticipants(users);
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  allParticipantsView.showError(errorMessageFactory.getMessageForError(error));
              }
          });
    }

    protected void refreshAllParticipants() {
        allParticipantsView.hideEmpty();
        getAllParticipantsInteractor.obtainAllParticipants(idStream,
          Long.MAX_VALUE,
          false,
          new Interactor.Callback<List<User>>() {
              @Override public void onLoaded(List<User> users) {
                  renderParticipants(users);
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  allParticipantsView.showError(errorMessageFactory.getMessageForError(error));
              }
          });
    }

    private void renderParticipants(List<User> users) {
        List<UserModel> userModels = userModelMapper.transform(users);
        participants = userModels;
        if (!participants.isEmpty()) {
            allParticipantsView.renderAllParticipants(userModels);
        } else {
            allParticipantsView.showEmpty();
        }
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
        allParticipantsView.showError(errorMessageFactory.getMessageForError(error));
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
                allParticipantsView.renderAllParticipants(participants);
                break;
            }
        }
    }

    private void selectStream() {
        selectStreamInteractor.selectStream(idStream, new Interactor.Callback<StreamSearchResult>() {
            @Override public void onLoaded(StreamSearchResult streamSearchResult) {
                /* no-op */
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                /* no-op */
            }
        });
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            refreshAllParticipants();
            selectStream();
        }
    }

    @Override public void pause() {
        this.hasBeenPaused = true;
    }

    public void searchClicked() {
        allParticipantsView.goToSearchParticipants();
    }

    public void makeNextRemoteSearch(UserModel item) {
        allParticipantsView.showProgressView();
        getAllParticipantsInteractor.obtainAllParticipants(idStream,
          item.getJoinStreamTimestamp(),
          true,
          new Interactor.Callback<List<User>>() {
              @Override public void onLoaded(List<User> users) {
                  List<UserModel> newParticipants = userModelMapper.transform(users);
                  if (!newParticipants.isEmpty()) {
                      allParticipantsView.renderParticipantsBelow(newParticipants);
                  } else {
                      allParticipantsView.hideProgressView();
                  }
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  allParticipantsView.showError(errorMessageFactory.getMessageForError(error));
              }
          });
    }
}
