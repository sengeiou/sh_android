package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.FollowRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class BanUserInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FollowRepository localFollowRepository;
    private final FollowRepository remoteFollowRepository;

    private String idUser;
    private Interactor.CompletedCallback callback;
    private Interactor.ErrorCallback errorCallback;

    @Inject public BanUserInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local FollowRepository localFollowRepository, @Remote FollowRepository remoteFollowRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localFollowRepository = localFollowRepository;
        this.remoteFollowRepository = remoteFollowRepository;
    }

    public void ban(String idUser, Interactor.CompletedCallback callback, Interactor.ErrorCallback errorCallback) {
        this.idUser = checkNotNull(idUser);
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        localFollowRepository.ban(idUser);
        try {
            remoteFollowRepository.ban(idUser);
            notifyCompleted();
        } catch (ServerCommunicationException e) {
            localFollowRepository.unban(idUser);
            notifyError(e);
        }
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
