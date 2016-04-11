package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.exception.NiceAlreadyMarkedException;
import com.shootr.mobile.domain.exception.NiceNotMarkedException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShotNotFoundException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.NiceShotRepository;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;

import javax.inject.Inject;

public class MarkNiceShotInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final NiceShotRepository localNiceShotRepository;
    private final NiceShotRepository remoteNiceShotRepository;
    private final ShotRepository localShotRepository;
    private final ShotRepository remoteShotRepository;

    private String idShot;
    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;

    @Inject public MarkNiceShotInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local NiceShotRepository localNiceShotRepository, @Remote NiceShotRepository remoteNiceShotRepository,
      @Local ShotRepository localShotRepository, @Remote ShotRepository remoteShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localNiceShotRepository = localNiceShotRepository;
        this.remoteNiceShotRepository = remoteNiceShotRepository;
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
    }

    public void markNiceShot(String idShot, CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.idShot = idShot;
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        this.interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        sendNiceToServer();
        notifyCompleted();
    }

    private void sendNiceToServer() throws NiceNotMarkedException {
        try {
            remoteNiceShotRepository.mark(idShot);
            markNiceInLocal();
        } catch (ServerCommunicationException | NiceAlreadyMarkedException error) {
            notifyError(new ShootrException() {});
        }
    }

    private void markNiceInLocal() throws NiceAlreadyMarkedException {
        try {
            localNiceShotRepository.mark(idShot);
            Shot shot = getShotFromLocalIfExists();
            shot.setNiceCount(shot.getNiceCount() + 1);
            localShotRepository.putShot(shot);
        } catch (ShotNotFoundException error) {
            /* swallow */
        }catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private Shot getShotFromLocalIfExists() {
        Shot shot = localShotRepository.getShot(idShot);
        if (shot == null) {
            shot = remoteShotRepository.getShot(idShot);
        }
        return shot;
    }

    private void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
            }
        });
    }

    protected void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
