package com.shootr.android.ui.presenter;

import android.content.Context;
import android.util.Pair;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.ShootrApplication;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.FindParticipantsInteractor;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.task.events.follows.FollowUnFollowResultEvent;
import com.shootr.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import com.shootr.android.task.jobs.follows.GetFollowUnfollowUserOnlineJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.FindParticipantsView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class FindParticipantsPresenter implements Presenter {

    private final FindParticipantsInteractor findParticipantsInteractor;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private final JobManager jobManager;
    private final Bus bus;

    private FindParticipantsView findParticipantsView;
    private String idStream;
    private List<UserModel> participants;
    private String query;
    private Boolean hasBeenPaused = false;

    @Inject public FindParticipantsPresenter(FindParticipantsInteractor findParticipantsInteractor,
      UserModelMapper userModelMapper, ErrorMessageFactory errorMessageFactory, JobManager jobManager, @Main Bus bus) {
        this.findParticipantsInteractor = findParticipantsInteractor;
        this.userModelMapper = userModelMapper;
        this.errorMessageFactory = errorMessageFactory;
        this.jobManager = jobManager;
        this.bus = bus;
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
                }else{
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
                }else{
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
                findParticipantsView.renderParticipants(participants);
                break;
            }
        }
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            refreshParticipants();
        }
        bus.register(this);
    }

    @Override public void pause() {
        hasBeenPaused = true;
        bus.unregister(this);
    }
}
