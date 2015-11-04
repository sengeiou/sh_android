package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.NiceNotMarkedException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.NiceShotRepository;
import javax.inject.Inject;

public class MarkNiceShotInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final NiceShotRepository localNiceShotRepository;
    private final NiceShotRepository remoteNiceShotRepository;

    private String idShot;
    private CompletedCallback completedCallback;

    @Inject public MarkNiceShotInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Local NiceShotRepository localNiceShotRepository,
      @com.shootr.mobile.domain.repository.Remote NiceShotRepository remoteNiceShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localNiceShotRepository = localNiceShotRepository;
        this.remoteNiceShotRepository = remoteNiceShotRepository;
    }

    public void markNiceShot(String idShot, CompletedCallback completedCallback) {
        this.idShot = idShot;
        this.completedCallback = completedCallback;
        this.interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            markNiceInLocal();
            sendNiceToServer();
        } catch (com.shootr.mobile.domain.exception.NiceAlreadyMarkedException e) {
            /* Ignore error and notify callback */
        }
        notifyCompleted();
    }

    private void sendNiceToServer() throws NiceNotMarkedException {
        try {
            remoteNiceShotRepository.mark(idShot);
        } catch (com.shootr.mobile.domain.exception.ShootrException | com.shootr.mobile.domain.exception.NiceAlreadyMarkedException e) {
            try {
                undoNiceInLocal();
            } catch (NiceNotMarkedException error) {
                /* swallow */
            }
        }
    }

    private void markNiceInLocal() throws com.shootr.mobile.domain.exception.NiceAlreadyMarkedException {
        localNiceShotRepository.mark(idShot);
    }

    private void undoNiceInLocal() throws NiceNotMarkedException {
        localNiceShotRepository.unmark(idShot);
    }

    private void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                completedCallback.onCompleted();
            }
        });
    }
}
