package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetStreamHoldingTimelineInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShotRepository localShotRepository;
  private final ShotRepository remoteShotRepository;
  private String idStream;
  private Callback callback;
  private ErrorCallback errorCallback;
  private String idUser;
  private Boolean goneBackground;

  @Inject public GetStreamHoldingTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local ShotRepository localShotRepository,
      @Remote ShotRepository remoteShotRepository) {
    this.localShotRepository = localShotRepository;
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteShotRepository = remoteShotRepository;
  }
  //endregion

  public void loadStreamHoldingTimeline(String idStream, String idUser, Boolean goneBackground,
      Callback<Timeline> callback, ErrorCallback errorCallback) {
    this.idStream = idStream;
    this.idUser = idUser;
    this.callback = callback;
    this.goneBackground = goneBackground;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    List<Shot> shots = loadLocalTimeline();
    if (shots.isEmpty()) {
      shots = loadRemoteTimeline(shots);
    }
    shots = sortShotsByPublishDate(shots);
    notifyTimelineFromShots(shots);
  }

  private List<Shot> loadLocalTimeline() {
    return localShotRepository.getUserShotsForStreamTimeline(buildParameters());
  }

  private List<Shot> loadRemoteTimeline(List<Shot> shots) {
    try {
      shots = remoteShotRepository.getUserShotsForStreamTimeline(buildParameters());
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
    return shots;
  }

  private StreamTimelineParameters buildParameters() {
    return StreamTimelineParameters.builder()
        .forStream(idStream)
        .forUser(idUser)
        .realTime(!goneBackground)
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

  private void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
  //endregion
}
