package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.user.UserRepository;
import javax.inject.Inject;

public class GetUserForAnalythicsByIdInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final UserRepository remoteUserRepository;

  private Callback<User> callback;
  private ErrorCallback errorCallback;
  private String userId;

  @Inject public GetUserForAnalythicsByIdInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote UserRepository remoteUserRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteUserRepository = remoteUserRepository;
  }

  @Override public void execute() throws Exception {
    loadRemoteUser();
  }

  public void getCurrentUserForAnalythics(String idUser, Callback<User> callback,
      ErrorCallback errorCallback) {
    this.userId = idUser;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  private void loadRemoteUser() {
    try {
      notifyResult(remoteUserRepository.getUserForAnalythicsById(userId));
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
