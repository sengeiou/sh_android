package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotDetail;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.nice.NicerRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetShotDetailInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalShotRepository localShotRepository;
  private final ExternalShotRepository remoteShotRepository;
  private final NicerRepository nicerRepository;

  private String idShot;
  private Callback<ShotDetail> callback;
  private ErrorCallback errorCallback;
  private boolean localOnly;

  @Inject public GetShotDetailInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, InternalShotRepository localShotRepository,
      ExternalShotRepository remoteShotRepository, NicerRepository nicerRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localShotRepository = localShotRepository;
    this.remoteShotRepository = remoteShotRepository;
    this.nicerRepository = nicerRepository;
  }

  public void loadShotDetail(String idShot, Boolean localOnly, Callback<ShotDetail> callback,
      ErrorCallback errorCallback) {
    this.idShot = idShot;
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.localOnly = localOnly;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      if (!localOnly) {
        loadRemoteShotDetail();
      }
      loadLocalShotDetail();
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  private void loadLocalShotDetail() {
    ShotDetail localShotDetail =
        localShotRepository.getShotDetail(idShot, StreamMode.TYPES_STREAM, ShotType.TYPES_SHOWN);
    orderParents(localShotDetail);
    localShotDetail.setNicers(nicerRepository.getNicers(idShot));
    notifyLoaded(reorderReplies(localShotDetail));
  }

  private void orderParents(ShotDetail localShotDetail) {
    if (localShotDetail.getParents() != null) {
      List<Shot> unorderedParents = localShotDetail.getParents();
      List<Shot> reorderedParents = orderParentsShots(unorderedParents);
      localShotDetail.setParents(reorderedParents);
    }
  }

  private void loadRemoteShotDetail() {
    ShotDetail remoteShotDetail =
        remoteShotRepository.getShotDetail(idShot, StreamMode.TYPES_STREAM, ShotType.TYPES_SHOWN);
    orderParents(remoteShotDetail);
    notifyLoaded(reorderReplies(remoteShotDetail));
    localShotRepository.putShot(remoteShotDetail.getShot());
  }

  private ShotDetail reorderReplies(ShotDetail shotDetail) {
    List<Shot> unorderedReplies = shotDetail.getReplies();
    List<Shot> reorderedReplies = orderShots(unorderedReplies);
    shotDetail.setReplies(reorderedReplies);
    return shotDetail;
  }

  private List<Shot> orderShots(List<Shot> replies) {
    Collections.sort(replies, new Shot.NewerBelowComparator());
    return replies;
  }

  private List<Shot> orderParentsShots(List<Shot> replies) {
    Collections.sort(replies, new Shot.NewerBelowComparator());
    return replies;
  }

  private void notifyLoaded(final ShotDetail shotDetail) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(shotDetail);
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
