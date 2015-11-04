package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.repository.Local;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetStreamHoldingTimelineInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.ShotRepository localShotRepository;
    private String idStream;
    private Interactor.Callback callback;
    private String idUser;

    @Inject public GetStreamHoldingTimelineInteractor(
      com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
      @Local com.shootr.mobile.domain.repository.ShotRepository localShotRepository) {
        this.localShotRepository = localShotRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
    }
    //endregion

    public void loadStreamHoldingTimeline(String idStream, String idUser, Interactor.Callback<com.shootr.mobile.domain.Timeline> callback) {
        this.idStream = idStream;
        this.idUser = idUser;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadLocalShots();
    }

    private void loadLocalShots() {
        List<com.shootr.mobile.domain.Shot> shots = loadLocalShots(buildParameters());
        shots = sortShotsByPublishDate(shots);
        notifyTimelineFromShots(shots);
    }

    private List<com.shootr.mobile.domain.Shot> loadLocalShots(
      com.shootr.mobile.domain.StreamTimelineParameters timelineParameters) {
        return localShotRepository.getUserShotsForStreamTimeline(timelineParameters);
    }

    private com.shootr.mobile.domain.StreamTimelineParameters buildParameters() {
        return com.shootr.mobile.domain.StreamTimelineParameters.builder()
          .forStream(idStream)
          .forUser(idUser)
          .build();
    }

    private List<com.shootr.mobile.domain.Shot> sortShotsByPublishDate(List<com.shootr.mobile.domain.Shot> remoteShots) {
        Collections.sort(remoteShots, new com.shootr.mobile.domain.Shot.NewerAboveComparator());
        return remoteShots;
    }

    //region Result
    private void notifyTimelineFromShots(List<com.shootr.mobile.domain.Shot> shots) {
        com.shootr.mobile.domain.Timeline timeline = buildTimeline(shots);
        notifyLoaded(timeline);
    }

    private com.shootr.mobile.domain.Timeline buildTimeline(List<com.shootr.mobile.domain.Shot> shots) {
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
    //endregion

}
