package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetAllShotsByUserInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalShotRepository localShotRepository;
  private final ExternalShotRepository remoteShotRepository;
  private String userId;
  private Callback<List<Shot>> callback;
  private ErrorCallback errorCallback;

  @Inject public GetAllShotsByUserInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, InternalShotRepository localShotRepository,
      ExternalShotRepository remoteShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localShotRepository = localShotRepository;
    this.remoteShotRepository = remoteShotRepository;
  }

  public void loadAllShots(String userId, Callback<List<Shot>> callback,
      ErrorCallback errorCallback) {
    this.userId = userId;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    List<Shot> localShots = localShotRepository.getAllShotsFromUser(userId, StreamMode.TYPES_STREAM,
        ShotType.TYPES_SHOWN);
    if (!localShots.isEmpty()) {
      notifyShots(localShots);
    }
    try {
      List<Shot> remoteShots =
          remoteShotRepository.getAllShotsFromUser(userId, StreamMode.TYPES_STREAM,
              ShotType.TYPES_SHOWN);
      notifyShots(remoteShots);
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  protected void notifyShots(List<Shot> remoteShots) {
    if (remoteShots.size() == 1) {
      notifyLoaded(remoteShots);
    } else {
      notifyLoaded(sortShotsByPublishDate(remoteShots));
    }
  }

  private List<Shot> sortShotsByPublishDate(List<Shot> shots) {
    Collections.sort(shots, new Shot.NewerAboveComparator());
    return shots;
  }

  private void notifyLoaded(final List<Shot> result) {
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
