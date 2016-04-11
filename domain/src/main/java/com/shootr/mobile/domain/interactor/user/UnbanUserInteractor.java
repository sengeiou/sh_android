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

public class UnbanUserInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FollowRepository localFollowRepository;
    private final FollowRepository remoteFollowRepository;

    private String idUser;
    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    @Inject public UnbanUserInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local FollowRepository localFollowRepository, @Remote FollowRepository remoteFollowRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localFollowRepository = localFollowRepository;
        this.remoteFollowRepository = remoteFollowRepository;
    }

    public void unban(String idUser, CompletedCallback callback, ErrorCallback errorCallback) {
        this.idUser = checkNotNull(idUser);
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            remoteFollowRepository.unban(idUser);
            localFollowRepository.unban(idUser);
            notifyCompleted();
        } catch (ServerCommunicationException error) {
            notifyError(error);
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
