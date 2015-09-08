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

public class FollowFaketeractor implements Interactor {

    private final Bus bus;
    private final PostExecutionThread postExecutionThread;
    private final InteractorHandler interactorHandler;
    private final Provider<GetFollowUnfollowUserOnlineJob> followOnlineJobProvider;
    private final Provider<GetFollowUnFollowUserOfflineJob> followOfflineJobProvider;
    private final JobManager jobManager;
    private String idUser;
    private CompletedCallback callback;

    @Inject public FollowFaketeractor(@Main Bus bus, PostExecutionThread postExecutionThread, @Fast InteractorHandler interactorHandler,
      Provider<GetFollowUnfollowUserOnlineJob> followOnlineJobProvider,
      Provider<GetFollowUnFollowUserOfflineJob> followOfflineJobProvider, JobManager jobManager) {
        this.bus = bus;
        this.postExecutionThread = postExecutionThread;
        this.interactorHandler = interactorHandler;
        this.followOnlineJobProvider = followOnlineJobProvider;
        this.followOfflineJobProvider = followOfflineJobProvider;
        this.jobManager = jobManager;
    }

    protected void follow(String idUser, CompletedCallback callback) {
        bus.register(this);
        this.idUser = idUser;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        startfollowUnfollowJob(idUser);
    }

    protected void startfollowUnfollowJob(String idUser) {
        GetFollowUnFollowUserOfflineJob offlineJob = followOfflineJobProvider.get();
        offlineJob.init(idUser, UserDtoFactory.FOLLOW_TYPE);
        jobManager.addJobInBackground(offlineJob);

        GetFollowUnfollowUserOnlineJob onlineJob = followOnlineJobProvider.get();
        jobManager.addJobInBackground(onlineJob);
    }

    @Subscribe
    public void onFollowUnfollowResultReceived(FollowUnFollowResultEvent event) {
        Pair<String, Boolean> result = event.getResult();
        Boolean following = result.second;
        if (following) {
            notifyFollow();
        }
    }

    private void notifyFollow() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onCompleted();
                bus.unregister(this);
            }
        });
    }
}
