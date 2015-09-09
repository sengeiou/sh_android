package com.shootr.android.ui.presenter;

import android.util.Pair;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Fast;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.task.events.follows.FollowUnFollowResultEvent;
import com.shootr.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import com.shootr.android.task.jobs.follows.GetFollowUnfollowUserOnlineJob;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import javax.inject.Inject;
import javax.inject.Provider;

public class UnfollowFaketeractor implements Interactor {

    private final Bus bus;
    private final PostExecutionThread postExecutionThread;
    private final InteractorHandler interactorHandler;
    private final Provider<GetFollowUnfollowUserOnlineJob> unfollowOnlineJobProvider;
    private final Provider<GetFollowUnFollowUserOfflineJob> unfollowOfflineJobProvider;
    private final JobManager jobManager;

    private String idUser;
    private CompletedCallback callback;

    @Inject public UnfollowFaketeractor(@Main Bus bus, PostExecutionThread postExecutionThread, @Fast InteractorHandler interactorHandler,
      Provider<GetFollowUnfollowUserOnlineJob> unfollowOnlineJobProvider,
      Provider<GetFollowUnFollowUserOfflineJob> unfollowOfflineJobProvider, JobManager jobManager) {
        this.bus = bus;
        this.postExecutionThread = postExecutionThread;
        this.interactorHandler = interactorHandler;
        this.unfollowOnlineJobProvider = unfollowOnlineJobProvider;
        this.unfollowOfflineJobProvider = unfollowOfflineJobProvider;
        this.jobManager = jobManager;
    }

    protected void unfollow(String idUser, CompletedCallback callback) {
        bus.register(this);
        this.idUser = idUser;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        startUnfollowJob(idUser);
    }

    protected void startUnfollowJob(String idUser) {
        GetFollowUnFollowUserOfflineJob offlineJob = unfollowOfflineJobProvider.get();
        offlineJob.init(idUser, UserDtoFactory.UNFOLLOW_TYPE);
        jobManager.addJobInBackground(offlineJob);

        GetFollowUnfollowUserOnlineJob onlineJob = unfollowOnlineJobProvider.get();
        jobManager.addJobInBackground(onlineJob);
    }

    @Subscribe
    public void onFollowUnfollowResultReceived(FollowUnFollowResultEvent event) {
        Pair<String, Boolean> result = event.getResult();
        Boolean following = result.second;
        if (!following) {
            notifyUnfollow();
        }
    }

    private void notifyUnfollow() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onCompleted();
                bus.unregister(this);
            }
        });
    }

}
