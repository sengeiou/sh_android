package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.RecentStreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.utils.TimeUtils;
import javax.inject.Inject;

public class SelectStreamInteractor implements Interactor {

  //region Dependencies
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final StreamRepository localStreamRepository;
  private final ExternalStreamRepository remoteStreamRepository;
  private final UserRepository localUserRepository;
  private final UserRepository remoteUserRepository;
  private final SessionRepository sessionRepository;
  private final TimeUtils timeUtils;
  private final RecentStreamRepository recentStreamRepository;

  private String idSelectedStream;
  private Callback<StreamSearchResult> callback;
  private ErrorCallback errorCallback;

  @Inject public SelectStreamInteractor(final InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamRepository localStreamRepository,
      ExternalStreamRepository remoteStreamRepository, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository, SessionRepository sessionRepository,
      TimeUtils timeUtils, RecentStreamRepository recentStreamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localStreamRepository = localStreamRepository;
    this.remoteStreamRepository = remoteStreamRepository;
    this.localUserRepository = localUserRepository;
    this.remoteUserRepository = remoteUserRepository;
    this.sessionRepository = sessionRepository;
    this.timeUtils = timeUtils;
    this.recentStreamRepository = recentStreamRepository;
  }
  //endregion

  public void selectStream(String idStream, Callback<StreamSearchResult> callback,
      ErrorCallback errorCallback) {
    this.idSelectedStream = idStream;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
    Stream selectedStream = getSelectedStream();
    recentStreamRepository.putRecentStream(selectedStream, getCurrentTime());
    if (isSelectingCurrentWatchingStream(currentUser)) {
      notifyLoaded(selectedStream);
    } else {

      if (selectedStream != null) {
        User updatedUser = updateUserWithStreamInfo(currentUser, selectedStream);
        sessionRepository.setTimelineFilterActivated(false);
        sessionRepository.setCurrentUser(updatedUser);
        notifyLoaded(selectedStream);
        remoteUserRepository.updateWatch(updatedUser);
      } else {
        notifyError(new ServerCommunicationException(new Throwable()));
      }
    }
  }

  private Stream getSelectedStream() {
    Stream selectedStream =
        localStreamRepository.getStreamById(idSelectedStream, StreamMode.TYPES_STREAM);
    if (selectedStream == null) {
      try {
        selectedStream =
            remoteStreamRepository.getStreamById(idSelectedStream, StreamMode.TYPES_STREAM);
      } catch (ServerCommunicationException error) {
        notifyError(error);
      }
    }
    return selectedStream;
  }

  private boolean isSelectingCurrentWatchingStream(User currentUser) {
    return idSelectedStream.equals(currentUser.getIdWatchingStream());
  }

  protected User updateUserWithStreamInfo(User currentUser, Stream selectedStream) {
    currentUser.setIdWatchingStream(selectedStream.getId());
    currentUser.setWatchingStreamTitle(selectedStream.getTitle());
    currentUser.setJoinStreamDate(getCurrentTime());
    return currentUser;
  }

  private long getCurrentTime() {
    return timeUtils.getCurrentTime();
  }

  private StreamSearchResult attachWatchNumber(Stream stream) {
    StreamSearchResult streamSearchResult = new StreamSearchResult();
    streamSearchResult.setStream(stream);
    return streamSearchResult;
  }

  private void notifyLoaded(final Stream selectedStream) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(attachWatchNumber(selectedStream));
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
