package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import javax.inject.Inject;

public class ShareShotInteractor implements Interactor {

    private final ShotRepository remoteShotRepository;
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private String idShot;
    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;

    @Inject public ShareShotInteractor(@Remote ShotRepository remoteShotRepository, InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread) {
        this.remoteShotRepository = remoteShotRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
    }

    public void shareShot(String idShot, CompletedCallback callback, ErrorCallback errorCallback) {
        this.idShot = idShot;
        this.completedCallback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            remoteShotRepository.shareShot(idShot);
            notifyCompleted();
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    protected void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
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
