package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import javax.inject.Inject;

public class GetUserByIdInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final UserRepository localUserRepository;
  private final UserRepository remoteUserRepository;
  private final SessionRepository sessionRepository;

  private Callback<User> callback;
  private ErrorCallback errorCallback;
  private String userId;
  private Boolean onlyLocal = false;

  @Inject public GetUserByIdInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository, SessionRepository sessionRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localUserRepository = localUserRepository;
    this.remoteUserRepository = remoteUserRepository;
    this.sessionRepository = sessionRepository;
  }

  public void loadUserById(String userId, Boolean onlyLocal, Callback<User> callback,
      ErrorCallback errorCallback) {
    this.onlyLocal = onlyLocal;
    this.userId = userId;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    if (userId != null) {
      loadLocalUser();
    }
    if (sessionRepository.getCurrentUserId() != null) {
      if (!sessionRepository.getCurrentUserId().equals(userId) || !onlyLocal) {
        loadRemoteUser();
      }
    }
  }

  private void loadLocalUser() {
    User localUser = localUserRepository.getUserById(userId);
    if (localUser != null) {
      notifyResult(localUser);
    }
  }

  private void loadRemoteUser() {
    try {
      notifyResult(remoteUserRepository.getUserById(userId));
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private void notifyResult(final User user) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(user);
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
