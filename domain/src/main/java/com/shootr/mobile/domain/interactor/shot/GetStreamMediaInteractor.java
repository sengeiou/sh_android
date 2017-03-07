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
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import com.shootr.mobile.domain.repository.shot.ShotRepository;
import java.util.List;
import javax.inject.Inject;

public class GetStreamMediaInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalShotRepository remoteShotRepository;
  private final InternalShotRepository localShotRepository;
  private ErrorCallback errorCallback;

  private String idStream;
  private Callback<List<Shot>> callback;

  @Inject public GetStreamMediaInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ExternalShotRepository remoteShotRepository,
      InternalShotRepository localShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteShotRepository = remoteShotRepository;
    this.localShotRepository = localShotRepository;
  }

  public void getStreamMedia(String idStream, Callback<List<Shot>> callback,
      ErrorCallback errorCallback) {
    this.idStream = idStream;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      getMediaFromLocal();
      getMediaFromRemote();
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private void getMediaFromLocal() {
    List<Shot> shots = getShotsFromRepository(localShotRepository, Long.MAX_VALUE);
    notifyLoaded(shots);
  }

  private void getMediaFromRemote() {
    List<Shot> shots = getShotsFromRepository(remoteShotRepository, Long.MAX_VALUE);
    notifyLoaded(shots);
  }

  private List<Shot> getShotsFromRepository(ShotRepository shotRepository,
      Long maxTimestamp) {
    return shotRepository.getMediaByIdStream(idStream, maxTimestamp,
        StreamMode.TYPES_STREAM, ShotType.TYPES_SHOWN);
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
