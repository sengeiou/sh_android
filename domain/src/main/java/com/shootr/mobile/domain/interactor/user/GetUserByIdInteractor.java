package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.UserRepository;
import javax.inject.Inject;

public class GetUserByIdInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;

    private Callback<User> callback;
    private ErrorCallback errorCallback;
    private String userId;

    @Inject public GetUserByIdInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local UserRepository localUserRepository, @Remote UserRepository remoteUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
    }

    public void loadUserById(String userId, Callback<User> callback, ErrorCallback errorCallback) {
        this.userId = userId;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadLocalUser();
        loadRemoteUser();
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
