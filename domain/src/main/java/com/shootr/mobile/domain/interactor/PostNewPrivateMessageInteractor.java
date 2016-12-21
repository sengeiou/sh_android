package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.shot.PostNewMessageInteractor;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.service.MessageSender;
import com.shootr.mobile.domain.service.dagger.Background;
import java.io.File;
import javax.inject.Inject;

public class PostNewPrivateMessageInteractor extends PostNewMessageInteractor {

  private String idTargetUser;

  @Inject public PostNewPrivateMessageInteractor(PostExecutionThread postExecutionThread,
      InteractorHandler interactorHandler, SessionRepository sessionRepository,
      @Background MessageSender messageSender) {
    super(postExecutionThread, interactorHandler, sessionRepository, messageSender);
  }

  public void postNewPrivateMessage(String comment, File image, String idTargetUser, CompletedCallback callback,
      ErrorCallback errorCallback) {
    this.idTargetUser = idTargetUser;
    super.postNewBaseMessage(comment, image, false, callback, errorCallback);
  }

  @Override protected void fillShotStreamInfo(Shot shot) {
    /* no-op */
  }

  @Override protected void fillPrivateMessageTargetInfo(PrivateMessage privateMessage) {
    privateMessage.setIdTargetUser(idTargetUser);
  }
}
