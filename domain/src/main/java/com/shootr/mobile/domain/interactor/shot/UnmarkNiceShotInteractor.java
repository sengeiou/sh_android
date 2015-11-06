package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.NiceAlreadyMarkedException;
import com.shootr.mobile.domain.exception.NiceNotMarkedException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.NiceShotRepository;
import com.shootr.mobile.domain.repository.Remote;
import javax.inject.Inject;

public class UnmarkNiceShotInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final NiceShotRepository localNiceShotRepository;
    private final NiceShotRepository remoteNiceShotRepository;

    private String idShot;
    private CompletedCallback completedCallback;

    @Inject
    public UnmarkNiceShotInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local NiceShotRepository localNiceShotRepository, @Remote NiceShotRepository remoteNiceShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localNiceShotRepository = localNiceShotRepository;
        this.remoteNiceShotRepository = remoteNiceShotRepository;
    }

    public void unmarkNiceShot(String idShot, CompletedCallback completedCallback) {
        this.idShot = idShot;
        this.completedCallback = completedCallback;
        this.interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            unmarkNiceInLocal();
            sendUndoNiceToRemote();
        } catch (NiceNotMarkedException e) {
            /* Ignore error and notify callback */
        }
        notifyCompleted();
    }

    private void unmarkNiceInLocal() throws NiceNotMarkedException {
        localNiceShotRepository.unmark(idShot);
    }

    protected void sendUndoNiceToRemote() {
        try {
            remoteNiceShotRepository.unmark(idShot);
        } catch (com.shootr.mobile.domain.exception.ShootrException | NiceNotMarkedException e) {
            try {
                redoNiceInLocal();
            } catch (NiceAlreadyMarkedException error) {
                /* swallow */
            }
        }
    }

    private void redoNiceInLocal() throws NiceAlreadyMarkedException {
        localNiceShotRepository.mark(idShot);
    }

    protected void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
            }
        });
    }
}
