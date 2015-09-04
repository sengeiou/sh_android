package com.shootr.android.ui.presenter;

import android.content.Context;
import android.util.Pair;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.ShootrApplication;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.android.domain.interactor.user.GetAllParticipantsInteractor;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.task.events.follows.FollowUnFollowResultEvent;
import com.shootr.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import com.shootr.android.task.jobs.follows.GetFollowUnfollowUserOnlineJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.AllParticipantsView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class AllParticipantsPresenter implements Presenter {

    private final GetAllParticipantsInteractor getAllParticipantsInteractor;
    private final SelectStreamInteractor selectStreamInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final UserModelMapper userModelMapper;
    private final JobManager jobManager;
    private final Bus bus;

    private AllParticipantsView allParticipantsView;
    private List<UserModel> participants;
    private Boolean hasBeenPaused = false;
    private String idStream;

    @Inject public AllParticipantsPresenter(GetAllParticipantsInteractor getAllParticipantsInteractor,
      SelectStreamInteractor selectStreamInteractor, ErrorMessageFactory errorMessageFactory, UserModelMapper userModelMapper, JobManager jobManager, @Main Bus bus) {
        this.getAllParticipantsInteractor = getAllParticipantsInteractor;
        this.selectStreamInteractor = selectStreamInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.userModelMapper = userModelMapper;
        this.jobManager = jobManager;
        this.bus = bus;
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
        getAllParticipantsInteractor.obtainAllParticipants(idStream, Long.MAX_VALUE, false, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                allParticipantsView.hideLoading();
                allParticipantsView.showAllParticipantsList();
                renderParticipants(users);
            }
        }, new Interactor.ErrorCallback() {
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

    public void followUser(UserModel userModel, Context context) {
        startFollowUnfollowUserJob(userModel, context, UserDtoFactory.FOLLOW_TYPE);
    }

    public void unfollowUser(UserModel userModel, Context context) {
        startFollowUnfollowUserJob(userModel, context, UserDtoFactory.UNFOLLOW_TYPE);
    }

    public void startFollowUnfollowUserJob(UserModel userVO, Context context, int followType){
        //Proceso de insercción en base de datos
        GetFollowUnFollowUserOfflineJob job2 = ShootrApplication.get(context).getObjectGraph().get(
          GetFollowUnFollowUserOfflineJob.class);
        job2.init(userVO.getIdUser(),followType);
        jobManager.addJobInBackground(job2);

        //Al instante
        GetFollowUnfollowUserOnlineJob
          job = ShootrApplication.get(context).getObjectGraph().get(GetFollowUnfollowUserOnlineJob.class);
        jobManager.addJobInBackground(job);
    }

    @Subscribe
    public void onFollowUnfollowReceived(FollowUnFollowResultEvent event){
        Pair<String, Boolean> result = event.getResult();
        String idUser = result.first;
        Boolean following = result.second;
        for (int i = 0; i < participants.size(); i++) {
            UserModel userModel = participants.get(i);
            if (userModel.getIdUser().equals(idUser)) {
                userModel.setRelationship(following? FollowEntity.RELATIONSHIP_FOLLOWING : FollowEntity.RELATIONSHIP_NONE);
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
        bus.register(this);
    }

    @Override public void pause() {
        this.hasBeenPaused = true;
        bus.unregister(this);
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
