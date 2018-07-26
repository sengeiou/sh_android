package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import com.shootr.mobile.domain.service.MessageSender;
import com.shootr.mobile.domain.service.dagger.Background;
import java.io.File;
import javax.inject.Inject;

public class NewShotAsReplyInteractor extends PostNewMessageInteractor {

  private final InternalShotRepository localShotRepository;
  private final ExternalShotRepository remoteShotRepository;
  private String replyParentId;
  private String idStream;

  @Inject public NewShotAsReplyInteractor(PostExecutionThread postExecutionThread,
      InteractorHandler interactorHandler, SessionRepository sessionRepository,
      @Background MessageSender messageSender, InternalShotRepository localShotRepository,
      ExternalShotRepository remoteShotRepository) {
    super(postExecutionThread, interactorHandler, sessionRepository, messageSender);
    this.localShotRepository = localShotRepository;
    this.remoteShotRepository = remoteShotRepository;
  }

  public void postNewShotAsReply(String comment, File image, String replyParentId, String idStream,
      CompletedCallback callback, ErrorCallback errorCallback) {
    this.replyParentId = replyParentId;
    this.idStream = idStream;
    super.postNewBaseMessage(comment, image, true, callback, errorCallback);
  }

  @Override protected void fillShotContextualInfo(Shot shot) {
    super.fillShotContextualInfo(shot);
    shot.setParentShotId(replyParentId);
    Shot.ShotStreamInfo streamInfo = new Shot.ShotStreamInfo();
    streamInfo.setIdStream(idStream);
    shot.setStreamInfo(streamInfo);
  }

  @Override protected void fillShotStreamInfo(Shot shot) {
    /* no-op */
  }

  @Override protected void fillPrivateMessageTargetInfo(PrivateMessage privateMessage) {
    /* no-op */
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
