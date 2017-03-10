package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import java.util.List;
import javax.inject.Inject;

public class GetOlderStreamMediaInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalShotRepository remoteShotRepository;
  private ErrorCallback errorCallback;

  private String idStream;
  private Interactor.Callback<List<Shot>> callback;
  private Long maxTimestamp;

  @Inject public GetOlderStreamMediaInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ExternalShotRepository remoteShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteShotRepository = remoteShotRepository;
  }

  public void getOlderStreamMedia(String idStream, Long maxTimestamp, Callback<List<Shot>> callback,
      ErrorCallback errorCallback) {
    this.idStream = idStream;
    this.maxTimestamp = maxTimestamp;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      getMediaFromRemote();
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private void getMediaFromRemote() {
    List<Shot> shots = remoteShotRepository.getMediaByIdStream(idStream, maxTimestamp,
        StreamMode.TYPES_STREAM, ShotType.TYPES_SHOWN);
    notifyLoaded(shots);
  }

  private void notifyLoaded(final List<Shot> shots) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(shots);
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