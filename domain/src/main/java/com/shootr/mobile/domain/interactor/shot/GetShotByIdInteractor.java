package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import javax.inject.Inject;

public class GetShotByIdInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository remoteShotRepository;

    private String idShot;
    private Callback<Shot> callback;
    private ErrorCallback errorCallback;

    @Inject public GetShotByIdInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote ShotRepository remoteShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteShotRepository = remoteShotRepository;
    }

    public void loadShot(String idShot, Callback<Shot> callback, ErrorCallback errorCallback) {
        this.idShot = idShot;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            Shot remoteShot = remoteShotRepository.getShot(idShot);
            notifyLoaded(remoteShot);
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    private void notifyLoaded(final Shot shot) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(shot);
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
