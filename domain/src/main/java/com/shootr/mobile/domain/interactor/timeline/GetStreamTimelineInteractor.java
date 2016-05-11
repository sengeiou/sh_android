package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.StreamTimelineParameters;
import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.ShotRepository;
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
  private Boolean goneBackground;

  @Inject public GetStreamTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local ShotRepository localShotRepository) {
    this.localShotRepository = localShotRepository;
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
  }
  //endregion

  public void loadStreamTimeline(String idStream, Boolean goneBackground,
      Callback<Timeline> callback) {
    this.idStream = idStream;
    this.goneBackground = goneBackground;
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
    return StreamTimelineParameters.builder().forStream(idStream).realTime(goneBackground).build();
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
