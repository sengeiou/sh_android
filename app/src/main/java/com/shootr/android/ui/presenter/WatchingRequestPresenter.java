package com.shootr.android.ui.presenter;

import android.os.Handler;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.gcm.event.RequestWatchByPushEvent;
import com.shootr.android.task.events.info.WatchingInfoResult;
import com.shootr.android.task.events.timeline.WatchingPeopleNumberEvent;
import com.shootr.android.task.events.timeline.WatchingRequestPendingEvent;
import com.shootr.android.task.jobs.info.GetWatchingInfoJob;
import com.shootr.android.task.jobs.info.SetWatchingInfoOfflineJob;
import com.shootr.android.task.jobs.info.SetWatchingInfoOnlineJob;
import com.shootr.android.task.jobs.timeline.GetWatchingPeopleNumberJob;
import com.shootr.android.task.jobs.timeline.GetWatchingRequestsPendingJob;
import com.shootr.android.ui.model.WatchingRequestModel;
import com.shootr.android.ui.views.WatchingRequestView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import dagger.ObjectGraph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class WatchingRequestPresenter implements Presenter{

    private final JobManager jobManager;
    private final Bus bus;

    private WatchingRequestView watchingRequestView;
    private ObjectGraph objectGraph;
    private WatchingRequestStack watchingRequestsPendingStack;
    private WatchingRequestModel currentRequest;
    private Integer peopleWatchingCount;

    @Inject public WatchingRequestPresenter(JobManager jobManager, Bus bus) {
        this.jobManager = jobManager;
        this.bus = bus;
    }

    public void initialize(WatchingRequestView watchingRequestView, ObjectGraph objectGraph) {
        this.watchingRequestView = watchingRequestView;
        this.objectGraph = objectGraph;
        this.retrieveData();
    }

    private void retrieveData() {
        GetWatchingInfoJob getWatchingInfoJob = objectGraph.get(GetWatchingInfoJob.class);
        getWatchingInfoJob.init(true);
        jobManager.addJobInBackground(getWatchingInfoJob);
    }

    @Subscribe public void onRequestWatchByPush(RequestWatchByPushEvent event){
        retrieveData();
    }

    @Subscribe
    public void onInfoDataRefreshed(WatchingInfoResult event) {
        startUpdateNotificationBadge();
        startRetrievingWatchingRequests();
    }

    private void startUpdateNotificationBadge() {
        GetWatchingPeopleNumberJob job = objectGraph.get(GetWatchingPeopleNumberJob.class);
        jobManager.addJobInBackground(job);
    }

    @Subscribe
    public void onNumberReceived(WatchingPeopleNumberEvent event) {
        this.peopleWatchingCount = event.getResult();
        watchingRequestView.setWatchingPeopleCount(peopleWatchingCount);
    }


    public void menuCreated() {
        if (peopleWatchingCount != null) {
            watchingRequestView.setWatchingPeopleCount(peopleWatchingCount);
        }
    }

    private void startRetrievingWatchingRequests() {
        GetWatchingRequestsPendingJob getWatchingRequestsPendingJob =
          objectGraph.get(GetWatchingRequestsPendingJob.class);
        jobManager.addJobInBackground(getWatchingRequestsPendingJob);
    }

    @Subscribe
    public void onWatchingRequestsPendingReceived(WatchingRequestPendingEvent event) {
        List<WatchingRequestModel> watchingRequests = event.getResult();
        if (watchingRequests != null && !watchingRequests.isEmpty()) {
            watchingRequestsPendingStack = new WatchingRequestStack(watchingRequests);
            showNextWatchingRequest();
        }
    }

    private void showNextWatchingRequest() {
        if (watchingRequestsPendingStack.hasMore()) {
            this.currentRequest = watchingRequestsPendingStack.next();
            watchingRequestView.showWatchingRequest(currentRequest);
        } else {
            Timber.d("No more requests ;)");
        }
    }

    private void hideCurrentWatchingRequest() {
        watchingRequestView.hideWatchingRequest();
    }

    private void hideCurrentRequestAndShowNext() {
        hideCurrentWatchingRequest();
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                if (watchingRequestsPendingStack != null) {
                    showNextWatchingRequest();
                }
            }
        }, 1000);
    }

    public void answerCurrentWatchingRequestPositive() {
        answerCurrentRequestAndShowNext(WatchEntity.STATUS_WATCHING);
    }

    public void answerCurrentWatchingRequestNegative() {
        answerCurrentRequestAndShowNext(WatchEntity.STATUS_REJECT);
    }

    private void answerCurrentRequestAndShowNext(Long status) {
        Long matchId = currentRequest.getMatchId();
        watchingRequestsPendingStack.remove(currentRequest);
        currentRequest = null;

        sendWatchingStatus(status, matchId);
        hideCurrentRequestAndShowNext();
    }

    private void sendWatchingStatus(Long status, Long matchId) {
        SetWatchingInfoOfflineJob jobOffline = objectGraph.get(SetWatchingInfoOfflineJob.class);
        jobOffline.init(matchId, status, null);
        jobManager.addJobInBackground(jobOffline);
        SetWatchingInfoOnlineJob jobOnline = objectGraph.get(SetWatchingInfoOnlineJob.class);
        jobManager.addJobInBackground(jobOnline);
    }

    @Override public void resume() {
        bus.register(this);
        retrieveData();
    }

    @Override public void pause() {
        bus.unregister(this);
    }

    private class WatchingRequestStack {

        private List<WatchingRequestModel> watchingRequestModels;

        private WatchingRequestStack(List<WatchingRequestModel> watchingRequestModels) {
            this.watchingRequestModels = watchingRequestModels;
        }

        public void setElements(Collection<WatchingRequestModel> elements) {
            watchingRequestModels = new ArrayList<>(elements);
        }

        public WatchingRequestModel next() {
            return watchingRequestModels.get(0);
        }

        public void remove(WatchingRequestModel currentRequest) {
            watchingRequestModels.remove(currentRequest);
        }

        public boolean hasMore() {
            return !watchingRequestModels.isEmpty();
        }
    }
}
