package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.List;
import javax.inject.Inject;

public class GetUserFollowersInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository remoteUserRepository;

    private String idUser;
    private Callback<List<User>> callback;
    private ErrorCallback errorCallback;

    @Inject public GetUserFollowersInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote UserRepository remoteUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteUserRepository = remoteUserRepository;
    }

    public void obtainFollowers(String idUser, Callback<List<User>> callback, ErrorCallback errorCallback) {
        this.idUser = idUser;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        obtainFollowers();
    }

    private void obtainFollowers() {
        try {
            notifyResult(remoteUserRepository.getFollowers(idUser));
        } catch (ServerCommunicationException networkError) {
            notifyError(networkError);
        }
    }

    private void notifyResult(final List<User> following) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(following);
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
