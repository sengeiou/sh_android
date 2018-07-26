package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.ReceiptType;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Sendable;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.service.MessageSender;
import com.shootr.mobile.domain.service.dagger.Background;
import java.io.File;
import java.util.Date;

public abstract class PostNewMessageInteractor implements Interactor {

  private final PostExecutionThread postExecutionThread;
  private final InteractorHandler interactorHandler;
  private final SessionRepository sessionRepository;
  private final MessageSender messageSender;
  private String comment;
  private File imageFile;
  private boolean isShot;
  private String receipt;
  private CompletedCallback callback;
  private ErrorCallback errorCallback;

  public PostNewMessageInteractor(PostExecutionThread postExecutionThread,
      InteractorHandler interactorHandler, SessionRepository sessionRepository,
      @Background MessageSender messageSender) {
    this.postExecutionThread = postExecutionThread;
    this.interactorHandler = interactorHandler;
    this.sessionRepository = sessionRepository;
    this.messageSender = messageSender;
  }

  public void postNewBaseMessage(String comment, File image, Boolean isShot, CompletedCallback callback,
      ErrorCallback errorCallback) {
    this.comment = comment;
    this.imageFile = image;
    this.isShot = isShot;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  public void postNewPromotedShot(String comment, File image, String receipt, CompletedCallback callback,
      ErrorCallback errorCallback) {
    this.comment = comment;
    this.imageFile = image;
    this.isShot = true;
    this.receipt = receipt;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      Sendable sendable = createSendable();
      notifyReadyToSend();
      sendToServer(sendable);
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  private void sendToServer(Sendable shot) {
    messageSender.sendMessage(shot, imageFile);
  }

  private Sendable createSendable() {
    if (isShot) {
      return createShotFromParameters();
    } else {
      return createMessageFromParameters();
    }
  }

  private BaseMessage createShotFromParameters() {
    Shot shot = new Shot();
    shot.setComment(filterComment(comment));
    shot.setPublishDate(new Date());
    fillShotContextualInfo(shot);
    shot.setType(ShotType.COMMENT);
    setupReceipt(shot);
    return shot;
  }

  private void setupReceipt(Shot shot) {
    if (receipt != null) {
      Shot.PromotedShotParams promotedShotParams = new Shot.PromotedShotParams();
      promotedShotParams.setData(receipt);
      promotedShotParams.setType(ReceiptType.CUSTOM);
      shot.setPromotedShotParams(promotedShotParams);
    }
  }

  private BaseMessage createMessageFromParameters() {
    PrivateMessage privateMessage = new PrivateMessage();
    privateMessage.setComment(filterComment(comment));
    privateMessage.setPublishDate(new Date());
    fillPrivateMessageTargetInfo(privateMessage);
    fillMessageUserInfo(privateMessage);
    return privateMessage;
  }

  private String filterComment(String comment) {
    if (comment != null && comment.isEmpty()) {
      return null;
    }
    return comment;
  }

  protected void fillShotContextualInfo(Shot shot) {
    fillMessageUserInfo(shot);
    fillShotStreamInfo(shot);
  }

  private void fillMessageUserInfo(BaseMessage baseMessage) {
    User currentUser = sessionRepository.getCurrentUser();
    BaseMessage.BaseMessageUserInfo userInfo = new BaseMessage.BaseMessageUserInfo();

    userInfo.setIdUser(currentUser.getIdUser());
    userInfo.setAvatar(currentUser.getPhoto());
    userInfo.setUsername(currentUser.getUsername());

    baseMessage.setUserInfo(userInfo);
  }

  protected abstract void fillShotStreamInfo(Shot shot);

  protected abstract void fillPrivateMessageTargetInfo(PrivateMessage privateMessage);

  private void notifyReadyToSend() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onCompleted();
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
