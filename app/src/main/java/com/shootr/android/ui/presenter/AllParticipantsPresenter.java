package com.shootr.android.ui.presenter;

import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.android.domain.interactor.user.GetAllParticipantsInteractor;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.AllParticipantsView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class AllParticipantsPresenter implements Presenter {

    private final GetAllParticipantsInteractor getAllParticipantsInteractor;
    private final SelectStreamInteractor selectStreamInteractor;
    private final FollowFaketeractor followFaketeractor;
    private final UnfollowFaketeractor unfollowFaketeractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final UserModelMapper userModelMapper;

    private AllParticipantsView allParticipantsView;
    private List<UserModel> participants;
    private Boolean hasBeenPaused = false;
    private String idStream;

    @Inject public AllParticipantsPresenter(GetAllParticipantsInteractor getAllParticipantsInteractor,
      SelectStreamInteractor selectStreamInteractor, FollowFaketeractor followFaketeractor,
      UnfollowFaketeractor unfollowFaketeractor, ErrorMessageFactory errorMessageFactory, UserModelMapper userModelMapper) {
        this.getAllParticipantsInteractor = getAllParticipantsInteractor;
        this.selectStreamInteractor = selectStreamInteractor;
        this.followFaketeractor = followFaketeractor;
        this.unfollowFaketeractor = unfollowFaketeractor;
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

    private void refreshAllParticipants() {
        allParticipantsView.hideEmpty();
        getAllParticipantsInteractor.obtainAllParticipants(idStream, Long.MAX_VALUE, false, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                renderParticipants(users);
            }
        }, new Interactor.ErrorCallback() {
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
        followFaketeractor.follow(userModel.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                refreshParticipantsFollowings(userModel.getIdUser(), FollowEntity.RELATIONSHIP_FOLLOWING);
            }
        });
    }

    public void unfollowUser(final UserModel userModel) {
        unfollowFaketeractor.unfollow(userModel.getIdUser(), new Interactor.CompletedCallback() {
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
        getAllParticipantsInteractor.obtainAllParticipants(idStream, item.getJoinStreamTimestamp(), true, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                List<UserModel> newParticipants = userModelMapper.transform(users);
                if (!newParticipants.isEmpty()) {
                    allParticipantsView.renderParticipantsBelow(newParticipants);
                } else {
                    allParticipantsView.hideProgressView();

                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                allParticipantsView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }
}
