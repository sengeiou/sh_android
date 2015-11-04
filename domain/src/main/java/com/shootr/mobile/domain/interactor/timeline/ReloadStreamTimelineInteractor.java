package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
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

    @Inject public ReloadStreamTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @com.shootr.mobile.domain.repository.Remote ShotRepository remoteShotRepository) {
        this.remoteShotRepository = remoteShotRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
    }
    //endregion

    public void loadStreamTimeline(String idStream, Callback<com.shootr.mobile.domain.Timeline> callback, ErrorCallback errorCallback) {
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

    private List<Shot> loadRemoteShots(com.shootr.mobile.domain.StreamTimelineParameters timelineParameters) {
        return remoteShotRepository.getShotsForStreamTimeline(timelineParameters);
    }

    private com.shootr.mobile.domain.StreamTimelineParameters buildParameters() {
        return com.shootr.mobile.domain.StreamTimelineParameters.builder()
          .forStream(idStream)
          .build();
    }

    private List<Shot> sortShotsByPublishDate(List<Shot> remoteShots) {
        Collections.sort(remoteShots, new Shot.NewerAboveComparator());
        return remoteShots;
    }

    //region Result
    private void notifyTimelineFromShots(List<Shot> shots) {
        com.shootr.mobile.domain.Timeline timeline = buildTimeline(shots);
        notifyLoaded(timeline);
    }

    private com.shootr.mobile.domain.Timeline buildTimeline(List<Shot> shots) {
        com.shootr.mobile.domain.Timeline timeline = new com.shootr.mobile.domain.Timeline();
        timeline.setShots(shots);
        return timeline;
    }

    private void notifyLoaded(final com.shootr.mobile.domain.Timeline timeline) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(timeline);
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
    //endregion

}
