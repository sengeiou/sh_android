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
        } catch (ServerCommunicationException | NiceAlreadyMarkedException | ShotRemovedException e) {
            try {
                undoNiceInLocal();
            } catch (ShotRemovedException error) {
                throw new IllegalArgumentException("ShotRemovedException should not be thrown here");
            } catch (NiceNotMarkedException error) {
                /* swallow */
            }
        }
    }

    private void markNiceInLocal() throws NiceAlreadyMarkedException, ShotRemovedException {
        localNiceShotRepository.mark(idShot);
    }

    private void undoNiceInLocal() throws NiceNotMarkedException, ShotRemovedException {
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
