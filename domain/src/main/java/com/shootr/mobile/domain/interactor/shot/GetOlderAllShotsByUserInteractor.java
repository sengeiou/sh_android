package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.ShotType;
import com.shootr.mobile.domain.StreamMode;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetOlderAllShotsByUserInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShotRepository remoteShotRepository;

  private String userId;
  private Long currentOldestDate;
  private Callback<List<Shot>> callback;
  private ErrorCallback errorCallback;

  @Inject public GetOlderAllShotsByUserInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote ShotRepository remoteShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteShotRepository = remoteShotRepository;
  }

  public void loadAllShots(String userId, long currentOldestDate, Callback<List<Shot>> callback,
      ErrorCallback errorCallback) {
    this.userId = userId;
    this.currentOldestDate = currentOldestDate;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      List<Shot> remoteShots =
          remoteShotRepository.getAllShotsFromUserAndDate(userId, currentOldestDate,
              StreamMode.TYPES_STREAM, ShotType.TYPES_SHOWN);
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

  private List<Shot> sortShotsByPublishDate(List<Shot> remoteShots) {
    Collections.sort(remoteShots, new Shot.NewerAboveComparator());
    return remoteShots;
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
