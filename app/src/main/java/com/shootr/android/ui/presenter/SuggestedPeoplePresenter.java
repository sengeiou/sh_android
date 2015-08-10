package com.shootr.android.ui.presenter;

import android.content.Context;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.ShootrApplication;
import com.shootr.android.domain.SuggestedPeople;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.GetSuggestedPeopleInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import com.shootr.android.task.jobs.follows.GetFollowUnfollowUserOnlineJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.SuggestedPeopleView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SuggestedPeoplePresenter implements Presenter {

    private final SessionRepository sessionRepository;
    private final GetSuggestedPeopleInteractor getSuggestedPeopleInteractor;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private final JobManager jobManager;
    private SuggestedPeopleView suggestedPeopleView;

    @Inject public SuggestedPeoplePresenter(SessionRepository sessionRepository,
      GetSuggestedPeopleInteractor getSuggestedPeopleInteractor, UserModelMapper userModelMapper,
      ErrorMessageFactory errorMessageFactory, JobManager jobManager) {
        this.sessionRepository = sessionRepository;
        this.getSuggestedPeopleInteractor = getSuggestedPeopleInteractor;
        this.userModelMapper = userModelMapper;
        this.errorMessageFactory = errorMessageFactory;
        this.jobManager = jobManager;
    }

    protected void setView(SuggestedPeopleView suggestedPeopleView) {
        this.suggestedPeopleView = suggestedPeopleView;
    }

    public void initialize(SuggestedPeopleView suggestedPeopleView) {
        setView(suggestedPeopleView);
        obtainSuggestedPeople();
    }

    private void obtainSuggestedPeople() {
        if(sessionRepository.getCurrentUser().getNumFollowings() != 0) {
            getSuggestedPeopleInteractor.obtainSuggestedPeople(new Interactor.Callback<List<SuggestedPeople>>() {
                @Override public void onLoaded(List<SuggestedPeople> suggestedPeoples) {
                    List<UserModel> users = new ArrayList<>();
                    for (SuggestedPeople suggestedPeople : suggestedPeoples) {
                        users.add(userModelMapper.transform(suggestedPeople.getUser()));
                    }
                    suggestedPeopleView.renderSuggestedPeopleList(users);
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    suggestedPeopleView.showError(errorMessageFactory.getMessageForError(error));
                }
            });
        }
    }

    public void followUser(UserModel user, Context context){
        startFollowUnfollowUserJob(user, context, UserDtoFactory.FOLLOW_TYPE);
    }

    public void unfollowUser(UserModel user, Context context){
        startFollowUnfollowUserJob(user, context, UserDtoFactory.UNFOLLOW_TYPE);
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

    @Override public void resume() {
        obtainSuggestedPeople();
    }

    @Override public void pause() {
        /* no-op */
    }
}
