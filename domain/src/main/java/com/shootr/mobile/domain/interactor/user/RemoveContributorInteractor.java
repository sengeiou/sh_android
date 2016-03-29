package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.UserRepository;
import javax.inject.Inject;

public class RemoveContributorInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final PostExecutionThread postExecutionThread;

    private String idStream;
    private String idUser;
    private Interactor.CompletedCallback callback;
    private Interactor.ErrorCallback errorCallback;

    @Inject public RemoveContributorInteractor(InteractorHandler interactorHandler, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository, PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void removeContributor(String idStream, String idUser, Interactor.CompletedCallback callback, Interactor.ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.idUser = idUser;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        removeLocalContributor(idStream, idUser);
        removeRemoteContributor(idStream, idUser);
    }

    private void removeRemoteContributor(String idStream, String idUser) {
        //TODO: call remoteRepository
        notifyCompleted();
    }

    private void removeLocalContributor(String idStream, String idUser) {
        //TODO: call localRepository
    }

    private void notifyCompleted() {
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
