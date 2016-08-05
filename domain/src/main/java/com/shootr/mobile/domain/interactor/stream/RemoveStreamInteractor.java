package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.utils.Preconditions;
import javax.inject.Inject;

public class RemoveStreamInteractor implements com.shootr.mobile.domain.interactor.Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final StreamRepository localStreamRepository;
  private final StreamRepository remoteStreamRepository;
  private final SessionRepository sessionRepository;
  private final UserRepository localUserRepository;
  private final UserRepository remoteUserRepository;
  private String idStream;
  private CompletedCallback completedCallback;
  private ErrorCallback errorCallback;

  @Inject public RemoveStreamInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamRepository localStreamRepository,
      @Remote StreamRepository remoteStreamRepository, SessionRepository sessionRepository,
      @Local UserRepository localUserRepository, @Remote UserRepository remoteUserRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localStreamRepository = localStreamRepository;
    this.remoteStreamRepository = remoteStreamRepository;
    this.sessionRepository = sessionRepository;
    this.localUserRepository = localUserRepository;
    this.remoteUserRepository = remoteUserRepository;
  }

  public void removeStream(String idStream, CompletedCallback completedCallback,
      ErrorCallback errorCallback) {
    this.idStream = Preconditions.checkNotNull(idStream);
    this.completedCallback = completedCallback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      remoteStreamRepository.removeStream(idStream);
      localStreamRepository.removeStream(idStream);

      User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
      removeWatching(currentUser);
      sessionRepository.setCurrentUser(currentUser);
      localUserRepository.updateWatch(currentUser);
      remoteUserRepository.updateWatch(currentUser);

      notifyCompleted();
    } catch (ServerCommunicationException networkError) {
      notifyError(networkError);
    }
  }

  private void removeWatching(User currentUser) {
    currentUser.setIdWatchingStream(null);
    currentUser.setWatchingStreamTitle(null);
    currentUser.setJoinStreamDate(null);
  }

  private void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        completedCallback.onCompleted();
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
