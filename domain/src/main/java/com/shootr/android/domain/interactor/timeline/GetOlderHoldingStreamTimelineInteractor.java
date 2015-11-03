package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamTimelineParameters;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.StreamRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetOlderHoldingStreamTimelineInteractor implements Interactor {

    private final SessionRepository sessionRepository;
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository remoteShotRepository;
    private final StreamRepository localStreamRepository;

    private Long currentOldestDate;
    private Callback<Timeline> callback;
    private ErrorCallback errorCallback;
    private String idUser;

    @Inject public GetOlderHoldingStreamTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, SessionRepository sessionRepository,
      @Remote ShotRepository remoteShotRepository, @Local StreamRepository localStreamRepository) {
        this.sessionRepository = sessionRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamRepository = localStreamRepository;
    }

    public void loadOlderHoldingStreamTimeline(String idUser, Long currentOldestDate, Callback<Timeline> callback,
      ErrorCallback errorCallback) {
        this.idUser = idUser;
        this.currentOldestDate = currentOldestDate;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            StreamTimelineParameters timelineParameters = buildTimelineParameters();
            List<Shot> olderShots = remoteShotRepository.getUserShotsForStreamTimeline(timelineParameters);
            sortShotsByPublishDate(olderShots);
            notifyTimelineFromShots(olderShots);
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    private StreamTimelineParameters buildTimelineParameters() {
        Stream visibleStream = getVisibleStream();
        return StreamTimelineParameters.builder() //
          .forStream(visibleStream) //
          .forUser(idUser)
          .maxDate(currentOldestDate) //
          .build();
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

    private Stream getVisibleStream() {
        String visibleStreamId = sessionRepository.getCurrentUser().getIdWatchingStream();
        if (visibleStreamId != null) {
            return localStreamRepository.getStreamById(visibleStreamId);
        }
        return null;
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
