package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.StreamTimelineParameters;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.ShotRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetStreamTimelineInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository localShotRepository;
    private String idStream;
    private Callback callback;

    @Inject public GetStreamTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Local ShotRepository localShotRepository) {
        this.localShotRepository = localShotRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
    }
    //endregion

    public void loadStreamTimeline(String idStream, Callback<Timeline> callback) {
        this.idStream = idStream;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadLocalShots();
    }

    private void loadLocalShots() {
        List<Shot> shots = loadLocalShots(buildParameters());
        shots = sortShotsByPublishDate(shots);
        notifyTimelineFromShots(shots);
    }

    private List<Shot> loadLocalShots(StreamTimelineParameters timelineParameters) {
        return localShotRepository.getShotsForStreamTimeline(timelineParameters);
    }

    private StreamTimelineParameters buildParameters() {
        return StreamTimelineParameters.builder()
          .forStream(idStream)
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

    private void notifyLoaded(final Timeline timeline) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(timeline);
            }
        });
    }
    //endregion
}
