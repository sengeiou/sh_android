package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.exception.NiceAlreadyMarkedException;
import com.shootr.android.domain.exception.NiceNotMarkedException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.NiceShotRepository;
import com.shootr.android.domain.repository.Remote;
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
      @Remote NiceShotRepository remoteNiceShotRepository) {
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
        } catch (NiceAlreadyMarkedException e) {
            /* Ignore error and notify callback */
        }
        notifyCompleted();
    }

    private void sendNiceToServer() throws NiceNotMarkedException {
        try {
            remoteNiceShotRepository.mark(idShot);
        } catch (ShootrException | NiceAlreadyMarkedException e) {
            try {
                undoNiceInLocal();
            } catch (NiceNotMarkedException error) {
                /* swallow */
            }
        }
    }

    private void markNiceInLocal() throws NiceAlreadyMarkedException {
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
