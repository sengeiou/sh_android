package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.user.UserRepository;
import javax.inject.Inject;

public class GetUserByUsernameInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;
    private final PostExecutionThread postExecutionThread;
    private Callback<User> callback;
    private ErrorCallback errorCallback;

    private String username;

    @Inject
    public GetUserByUsernameInteractor(InteractorHandler interactorHandler, @Remote UserRepository remoteUserRepository,
      @Local UserRepository localUserRepository, PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void searchUserByUsername(String username, Callback<User> callback, ErrorCallback errorCallback) {
        this.username = username;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        User localUser = loadLocalUser();
        loadRemoteUser(localUser);
    }

    private void loadRemoteUser(User localUser) {
        try {
            User remoteUser = remoteUserRepository.getUserByUsername(username);
            notifyResult(remoteUser);
            if (localUser != null) {
                localUserRepository.putUser(remoteUser);
            }
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    private User loadLocalUser() {
        User localUser = localUserRepository.getUserByUsername(username);
        if (localUser != null) {
            notifyResult(localUser);
        }
        return localUser;
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
