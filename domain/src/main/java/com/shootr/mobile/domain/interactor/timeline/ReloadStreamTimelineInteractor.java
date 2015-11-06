package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.StreamTimelineParameters;
import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class ReloadStreamTimelineInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository remoteShotRepository;
    private String idStream;
    private Callback callback;
    private ErrorCallback errorCallback;

    @Inject
    public ReloadStreamTimelineInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote ShotRepository remoteShotRepository) {
        this.remoteShotRepository = remoteShotRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
    }
    //endregion

    public void loadStreamTimeline(String idStream, Callback<Timeline> callback, ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            List<Shot> shots = loadRemoteShots(buildParameters());
            shots = sortShotsByPublishDate(shots);
            notifyTimelineFromShots(shots);
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private List<Shot> loadRemoteShots(StreamTimelineParameters timelineParameters) {
        return remoteShotRepository.getShotsForStreamTimeline(timelineParameters);
    }

    private StreamTimelineParameters buildParameters() {
        return StreamTimelineParameters.builder().forStream(idStream).build();
    }

    private List<Shot> sortShotsByPublishDate(List<Shot> remoteShots) {
        Collections.sort(remoteShots, new Shot.NewerAboveComparator());
        return remoteShots;
    }

    //region Result
    private void notifyTimelineFromShots(List<Shot> shots) {
        Timeline timeline = buildTimeline(shots);
        notifyLoaded(timeline);
    }

    private Timeline buildTimeline(List<Shot> shots) {
        Timeline timeline = new Timeline();
        timeline.setShots(shots);
        return timeline;
    }

    private void notifyLoaded(final Timeline timeline) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(timeline);
            }
        });
    }

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
    //endregion
}
