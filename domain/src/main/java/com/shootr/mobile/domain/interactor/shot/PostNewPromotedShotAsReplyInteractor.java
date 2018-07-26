package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import com.shootr.mobile.domain.service.MessageSender;
import com.shootr.mobile.domain.service.dagger.Background;
import java.io.File;
import javax.inject.Inject;

public class PostNewPromotedShotAsReplyInteractor extends PostNewMessageInteractor {

  private final InternalShotRepository localShotRepository;
  private final ExternalShotRepository remoteShotRepository;
  private String replyParentId;
  private String idStream;

  @Inject public PostNewPromotedShotAsReplyInteractor(PostExecutionThread postExecutionThread,
      InteractorHandler interactorHandler, SessionRepository sessionRepository,
      @Background MessageSender messageSender, InternalShotRepository localShotRepository,
      ExternalShotRepository remoteShotRepository) {
    super(postExecutionThread, interactorHandler, sessionRepository, messageSender);
    this.localShotRepository = localShotRepository;
    this.remoteShotRepository = remoteShotRepository;
  }

  public void postNewPromotedShotAsReply(String comment, File image, String replyParentId,
      String receipt, String idStream, CompletedCallback callback, ErrorCallback errorCallback) {
    this.replyParentId = replyParentId;
    this.idStream = idStream;
    super.postNewPromotedShot(comment, image, receipt, callback, errorCallback);
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
}
