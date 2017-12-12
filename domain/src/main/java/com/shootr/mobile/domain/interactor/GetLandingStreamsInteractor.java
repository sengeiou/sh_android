package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamListSynchronizationRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.utils.TimeUtils;
import javax.inject.Inject;

public class GetLandingStreamsInteractor implements Interactor {

  private static final Long REFRESH_THRESHOLD_30_SECONDS_IN_MILLIS = 30L * 1000L;

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalStreamRepository remoteStreamRepository;
  private final StreamRepository localStreamRepository;
  private final StreamListSynchronizationRepository streamListSynchronizationRepository;
  private final TimeUtils timeUtils;


  private Interactor.Callback<LandingStreams> callback;
  private Interactor.ErrorCallback errorCallback;

  @Inject public GetLandingStreamsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ExternalStreamRepository streamRepository,
      @Local StreamRepository localStreamRepository,
      StreamListSynchronizationRepository streamListSynchronizationRepository, TimeUtils timeUtils) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteStreamRepository = streamRepository;
    this.localStreamRepository = localStreamRepository;
    this.streamListSynchronizationRepository = streamListSynchronizationRepository;
    this.timeUtils = timeUtils;
  }

  public void getLandingStreams(Callback<LandingStreams> callback,
      ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {

    LandingStreams landingStreams = localStreamRepository.getLandingStreams();
    notifyLoaded(landingStreams);

    Long currentTime = timeUtils.getCurrentTime();
    if (minimumRefreshTimePassed(currentTime) || landingStreams == null) {
      try {
        refreshStreams();
        streamListSynchronizationRepository.setStreamsRefreshDate(currentTime);
      } catch (ShootrException error) {
        notifyError(error);
      }
    }
  }

  private void refreshStreams() {
    LandingStreams landingStreams =
        remoteStreamRepository.getLandingStreams();
    notifyLoaded(landingStreams);
    localStreamRepository.putLandingStreams(landingStreams);
  }


  private boolean minimumRefreshTimePassed(Long currentTime) {
    Long streamsLastRefreshDate = streamListSynchronizationRepository.getStreamsRefreshDate();
    Long minimumTimeToRefresh = streamsLastRefreshDate + REFRESH_THRESHOLD_30_SECONDS_IN_MILLIS;
    return minimumTimeToRefresh < currentTime;
  }


  private void notifyLoaded(final LandingStreams result) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(result);
      }
    });
  }

  protected void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
}
