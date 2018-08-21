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

public class PostNewShotInStreamInteractor extends PostNewMessageInteractor {

  private final SessionRepository sessionRepository;
  private final StreamRepository localStreamRepository;

  private String idStream;
  private String streamTitle;

  @Inject public PostNewShotInStreamInteractor(PostExecutionThread postExecutionThread,
      InteractorHandler interactorHandler, SessionRepository sessionRepository,
      @Local StreamRepository localStreamRepository, @Background MessageSender messageSender) {
    super(postExecutionThread, interactorHandler, sessionRepository, messageSender);
    this.sessionRepository = sessionRepository;
    this.localStreamRepository = localStreamRepository;
  }

  public void postNewShotInStream(String comment, File image, CompletedCallback callback,
      ErrorCallback errorCallback) {
    super.postNewBaseMessage(comment, image, true, callback, errorCallback);
  }

  public void postNewShotInStream(String comment, File image, String idStream, String streamTitle,
      CompletedCallback callback, ErrorCallback errorCallback) {
    this.idStream = idStream;
    this.streamTitle = streamTitle;
    super.postNewBaseMessage(comment, image, true, callback, errorCallback);
  }

  @Override protected void fillShotStreamInfo(Shot shot) {
    Stream stream = currentVisibleStream();
    Shot.ShotStreamInfo eventInfo = new Shot.ShotStreamInfo();
    if (idStream == null) {
      if (stream != null) {
        eventInfo.setIdStream(stream.getId());
        eventInfo.setStreamTitle(stream.getTitle());
      }
    } else {
      eventInfo.setIdStream(idStream);
      eventInfo.setStreamTitle(streamTitle);
    }
    shot.setStreamInfo(eventInfo);
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
