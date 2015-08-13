package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.exception.NiceAlreadyMarkedException;
import com.shootr.android.domain.exception.NiceNotMarkedException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.NiceShotRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.service.shot.ShootrShotService;
import javax.inject.Inject;

public class UnmarkNiceShotInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrShotService shootrShotService;
    private final NiceShotRepository niceShotRepository;

    private String idShot;
    private CompletedCallback completedCallback;

    @Inject public UnmarkNiceShotInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      ShootrShotService shootrShotService,
      NiceShotRepository niceShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrShotService = shootrShotService;
        this.niceShotRepository = niceShotRepository;
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
        niceShotRepository.unmark(idShot);
    }

    protected void sendUndoNiceToRemote() throws NiceAlreadyMarkedException {
        try {
            shootrShotService.unmarkNiceShot(idShot);
        } catch (ServerCommunicationException e) {
            redoNiceInLocal();
        }
    }

    private void redoNiceInLocal() throws NiceAlreadyMarkedException {
        niceShotRepository.mark(idShot);
    }

    protected void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                completedCallback.onCompleted();
            }
        });
    }
}
