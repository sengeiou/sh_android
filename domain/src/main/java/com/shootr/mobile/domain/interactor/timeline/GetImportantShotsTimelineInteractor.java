package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class GetImportantShotsTimelineInteractor implements Interactor {

  private final int LIMIT_COUNT_SHOTS = 300;
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalShotRepository remoteShotRepository;
  private final SessionRepository sessionRepository;
  private final StreamRepository localStreamRepository;
  private Callback<Timeline> callback;
  private ErrorCallback errorCallback;
  private String idStream;

  @Inject public GetImportantShotsTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ExternalShotRepository remoteShotRepository,
      SessionRepository sessionRepository, @Local StreamRepository localStreamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteShotRepository = remoteShotRepository;
    this.sessionRepository = sessionRepository;
    this.localStreamRepository = localStreamRepository;
  }

  public void getImportantShotsTimeline(String streamId,
      Callback<Timeline> callback, ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.idStream = streamId;
    interactorHandler.execute(this);
  }

  private StreamTimelineParameters buildTimelineParameters() {
    return StreamTimelineParameters.builder()
        .forStream(idStream)
        .limit(LIMIT_COUNT_SHOTS)
        .build();
  }

  @Override public void execute() throws Exception {
    try {
      String lastFilteredDate = String.valueOf(new Date().getTime());
      localStreamRepository.putLastTimeFiltered(idStream, lastFilteredDate);
      sessionRepository.setLastTimeFiltered(lastFilteredDate);
      notifyLoaded(
          buildTimeline(remoteShotRepository.updateImportantShots(buildTimelineParameters())));
    } catch (ShootrException error) {
      notifyError(error);
    }
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
}
