package com.shootr.mobile.domain.interactor.timeline;

import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetOlderHoldingStreamTimelineInteractor implements com.shootr.mobile.domain.interactor.Interactor {

    private final com.shootr.mobile.domain.repository.SessionRepository sessionRepository;
    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository;
    private final com.shootr.mobile.domain.repository.StreamRepository localStreamRepository;

    private Long currentOldestDate;
    private Callback<com.shootr.mobile.domain.Timeline> callback;
    private ErrorCallback errorCallback;
    private String idUser;

    @Inject public GetOlderHoldingStreamTimelineInteractor(
      com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread, com.shootr.mobile.domain.repository.SessionRepository sessionRepository,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository, @com.shootr.mobile.domain.repository.Local
    com.shootr.mobile.domain.repository.StreamRepository localStreamRepository) {
        this.sessionRepository = sessionRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamRepository = localStreamRepository;
    }

    public void loadOlderHoldingStreamTimeline(String idUser, Long currentOldestDate, Callback<com.shootr.mobile.domain.Timeline> callback,
      ErrorCallback errorCallback) {
        this.idUser = idUser;
        this.currentOldestDate = currentOldestDate;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            com.shootr.mobile.domain.StreamTimelineParameters timelineParameters = buildTimelineParameters();
            List<com.shootr.mobile.domain.Shot> olderShots = remoteShotRepository.getUserShotsForStreamTimeline(timelineParameters);
            sortShotsByPublishDate(olderShots);
            notifyTimelineFromShots(olderShots);
        } catch (com.shootr.mobile.domain.exception.ShootrException error) {
            notifyError(error);
        }
    }

    private com.shootr.mobile.domain.StreamTimelineParameters buildTimelineParameters() {
        com.shootr.mobile.domain.Stream visibleStream = getVisibleStream();
        return com.shootr.mobile.domain.StreamTimelineParameters.builder() //
          .forStream(visibleStream) //
          .forUser(idUser)
          .maxDate(currentOldestDate) //
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

    private com.shootr.mobile.domain.Stream getVisibleStream() {
        String visibleStreamId = sessionRepository.getCurrentUser().getIdWatchingStream();
        if (visibleStreamId != null) {
            return localStreamRepository.getStreamById(visibleStreamId);
        }
        return null;
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
