package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;

public class GetOlderStreamTimelineInteractor
    implements com.shootr.mobile.domain.interactor.Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalShotRepository remoteShotRepository;
  private final StreamRepository localStreamRepository;

  private Long currentOldestDate;
  private String streamId;
  private boolean filterActivated;
  private Callback<Timeline> callback;
  private ErrorCallback errorCallback;

  @Inject public GetOlderStreamTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      ExternalShotRepository remoteShotRepository,
      @Local StreamRepository localStreamRepository) {
    this.remoteShotRepository = remoteShotRepository;
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localStreamRepository = localStreamRepository;
  }

  public void loadOlderStreamTimeline(String streamId, boolean filterActivated, Long currentOldestDate, Callback<Timeline> callback,
      ErrorCallback errorCallback) {
    this.streamId = streamId;
    this.filterActivated = filterActivated;
    this.currentOldestDate = currentOldestDate;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      StreamTimelineParameters timelineParameters = buildTimelineParameters();
      List<Shot> olderShots = remoteShotRepository.getShotsForStreamTimeline(timelineParameters);
      sortShotsByPublishDate(olderShots);
      if (filterActivated) {
        filterOlderShots(olderShots);
      }
      notifyTimelineFromShots(olderShots);
    } catch (ShootrException error) {
      notifyError(error);
    } catch (NullPointerException ignored) {
      /* no-op */
    }
  }

  private void filterOlderShots(List<Shot> olderShots) {
    Iterator<Shot> iterator = olderShots.iterator();
    while (iterator.hasNext()) {
      Shot shot = iterator.next();
      if (shot.isPadding()) {
        iterator.remove();
      }
    }
  }

  private StreamTimelineParameters buildTimelineParameters() {
    Stream visibleStream = getVisibleStream();
    return StreamTimelineParameters.builder() //
        .forStream(visibleStream) //
        .maxDate(currentOldestDate) //
        .realTime(true) //
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
    String visibleStreamId = streamId;
    if (visibleStreamId != null) {
      return localStreamRepository.getStreamById(visibleStreamId, StreamMode.TYPES_STREAM);
    } else {
      return null;
    }
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
