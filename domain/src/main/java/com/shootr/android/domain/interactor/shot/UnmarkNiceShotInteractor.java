package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.exception.NiceAlreadyMarkedException;
import com.shootr.android.domain.exception.NiceNotMarkedException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShotRemovedException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.NiceShotRepository;
import com.shootr.android.domain.repository.Remote;
import javax.inject.Inject;

public class UnmarkNiceShotInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final NiceShotRepository localNiceShotRepository;
    private final NiceShotRepository remoteNiceShotRepository;

    private String idShot;
    private CompletedCallback completedCallback;

    @Inject public UnmarkNiceShotInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Local NiceShotRepository localNiceShotRepository,
      @Remote NiceShotRepository remoteNiceShotRepository) {
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
        try {
            localNiceShotRepository.unmark(idShot);
        } catch (ShotRemovedException e) {
            e.printStackTrace();
        }
    }

    protected void sendUndoNiceToRemote() throws NiceAlreadyMarkedException {
        try {
            remoteNiceShotRepository.unmark(idShot);
        } catch (ServerCommunicationException | NiceNotMarkedException e) {
            redoNiceInLocal();
        } catch (ShotRemovedException e) {
            /* Swallow */
        }
    }

    private void redoNiceInLocal() {
        try {
            localNiceShotRepository.mark(idShot);
        } catch (NiceAlreadyMarkedException | ShotRemovedException e) {
            e.printStackTrace();
        }
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
