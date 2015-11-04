package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.interactor.Interactor;
import javax.inject.Inject;

public class SendPasswordResetEmailInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.service.user.ShootrUserService shootrUserService;

    private ErrorCallback errorCallback;
    private CompletedCallback completedCallback;

    private String idUser;

    @Inject public SendPasswordResetEmailInteractor(
      com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread, com.shootr.mobile.domain.service.user.ShootrUserService shootrUserService) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrUserService = shootrUserService;
    }

    public void sendPasswordResetEmail(String idUser, CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.idUser = idUser;
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            shootrUserService.sendPasswordResetEmail(idUser);
            notifyCompleted();
        } catch (com.shootr.mobile.domain.exception.ShootrException error) {
            notifyError(error);
        }
    }

    private void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
