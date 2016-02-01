package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import java.util.Date;
import javax.inject.Inject;

public class PinToProfileInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository remoteShotRepository;

    private String idShot;
    private CompletedCallback completedCallback;

    @Inject PinToProfileInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread, @Remote
    ShotRepository remoteShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteShotRepository = remoteShotRepository;
    }

    public void pinShot(String idShot, CompletedCallback completedCallback){
        this.idShot = idShot;
        this.completedCallback = completedCallback;
        this.interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            sendHideShotToServer();
        } catch (ServerCommunicationException e) {
            /* Ignore error and notify callback */
        }
        notifyCompleted();
    }

    private void sendHideShotToServer(){
        remoteShotRepository.pinShot(idShot);
    }

    private void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
            }
        });
    }
}
