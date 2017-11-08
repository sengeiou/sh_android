package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import java.util.List;
import javax.inject.Inject;

public class GetViewOnlyStreamTimelineInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalShotRepository localShotRepository;

  private String idStream;
  private Callback callback;
  private Boolean goneBackground;

  @Inject public GetViewOnlyStreamTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, InternalShotRepository localShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localShotRepository = localShotRepository;
  }

  public void loadStreamTimeline(String idStream, Boolean goneBackground,
      Callback<Timeline> callback) {
    this.idStream = idStream;
    this.goneBackground = goneBackground;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    loadLocalTimeline();
  }

  private void loadLocalTimeline() {
    List<Shot> shotsForStreamTimeline = getShots();
    notifyTimelineFromShots(shotsForStreamTimeline);
  }

  private List<Shot> getShots() {
    StreamTimelineParameters streamTimelineParameters = buildParameters();
    return localShotRepository.getShotsForStreamTimeline(streamTimelineParameters);
  }

  private StreamTimelineParameters buildParameters() {
    return StreamTimelineParameters.builder().forStream(idStream).realTime(goneBackground).build();
  }

  private void notifyTimelineFromShots(List<Shot> shots) {
    Timeline timeline = buildTimeline(shots);
    timeline.setFirstCall(false);
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
}
