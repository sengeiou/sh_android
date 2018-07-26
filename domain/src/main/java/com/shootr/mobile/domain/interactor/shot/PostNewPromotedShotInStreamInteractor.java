package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.service.MessageSender;
import com.shootr.mobile.domain.service.dagger.Background;
import java.io.File;
import javax.inject.Inject;

public class PostNewPromotedShotInStreamInteractor extends PostNewMessageInteractor {

  private final SessionRepository sessionRepository;
  private final StreamRepository localStreamRepository;

  @Inject public PostNewPromotedShotInStreamInteractor(PostExecutionThread postExecutionThread,
      InteractorHandler interactorHandler, SessionRepository sessionRepository,
      @Local StreamRepository localStreamRepository, @Background MessageSender messageSender) {
    super(postExecutionThread, interactorHandler, sessionRepository, messageSender);
    this.sessionRepository = sessionRepository;
    this.localStreamRepository = localStreamRepository;
  }

  public void postNewPromotedShotInStream(String comment, File image, String receipt, CompletedCallback callback,
      ErrorCallback errorCallback) {
    super.postNewPromotedShot(comment, image, receipt, callback, errorCallback);
  }

  @Override protected void fillShotStreamInfo(Shot shot) {
    Stream stream = currentVisibleStream();
    if (stream != null) {
      Shot.ShotStreamInfo eventInfo = new Shot.ShotStreamInfo();
      eventInfo.setIdStream(stream.getId());
      eventInfo.setStreamTitle(stream.getTitle());
      shot.setStreamInfo(eventInfo);
    }
  }

  @Override protected void fillPrivateMessageTargetInfo(PrivateMessage privateMessage) {
    /* no-op */
  }

  private Stream currentVisibleStream() {
    String visibleStreamId = sessionRepository.getCurrentUser().getIdWatchingStream();
    if (visibleStreamId != null) {
      return localStreamRepository.getStreamById(visibleStreamId, StreamMode.TYPES_STREAM);
    } else {
      return null;
    }
  }
}
