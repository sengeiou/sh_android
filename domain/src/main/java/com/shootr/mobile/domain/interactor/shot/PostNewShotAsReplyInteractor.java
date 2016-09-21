package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import com.shootr.mobile.domain.service.ShotSender;
import com.shootr.mobile.domain.service.dagger.Background;
import java.io.File;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class PostNewShotAsReplyInteractor extends PostNewShotInteractor {

  private final InternalShotRepository localShotRepository;
  private final ExternalShotRepository remoteShotRepository;
  private String replyParentId;

  @Inject public PostNewShotAsReplyInteractor(PostExecutionThread postExecutionThread,
      InteractorHandler interactorHandler, SessionRepository sessionRepository,
      @Background ShotSender shotSender, InternalShotRepository localShotRepository,
      ExternalShotRepository remoteShotRepository) {
    super(postExecutionThread, interactorHandler, sessionRepository, shotSender);
    this.localShotRepository = localShotRepository;
    this.remoteShotRepository = remoteShotRepository;
  }

  public void postNewShotAsReply(String comment, File image, String replyParentId,
      CompletedCallback callback, ErrorCallback errorCallback) {
    this.replyParentId = replyParentId;
    super.postNewShot(comment, image, callback, errorCallback);
  }

  @Override protected void fillShotContextualInfo(Shot shot) {
    super.fillShotContextualInfo(shot);
    fillReplyInfo(shot);
  }

  @Override protected void fillShotStreamInfo(Shot shot) {
    Shot parentShot = getParentShot();
    checkNotNull(parentShot, "Parent shot not found with id=%s", replyParentId);
    shot.setStreamInfo(parentShot.getStreamInfo());
  }

  private void fillReplyInfo(Shot shot) {
    Shot parentShot = getParentShot();
    shot.setParentShotId(parentShot.getIdShot());
    shot.setParentShotUserId(parentShot.getUserInfo().getIdUser());
    shot.setParentShotUsername(parentShot.getUserInfo().getUsername());
  }

  private Shot getParentShot() {
    Shot parentShot =
        localShotRepository.getShot(replyParentId, StreamMode.TYPES_STREAM, ShotType.TYPES_SHOWN);
    if (parentShot == null) {
      parentShot = remoteShotRepository.getShot(replyParentId, StreamMode.TYPES_STREAM,
          ShotType.TYPES_SHOWN);
    }
    return parentShot;
  }
}
