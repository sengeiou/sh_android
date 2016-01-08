package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.service.user.ShootrUserService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import javax.inject.Inject;

public class SendPasswordResetEmailInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrUserService shootrUserService;

    private ErrorCallback errorCallback;
    private CompletedCallback completedCallback;

    private String idUser;
    private LocaleProvider localeProvider;

    @Inject public SendPasswordResetEmailInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ShootrUserService shootrUserService, LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrUserService = shootrUserService;
        this.localeProvider = localeProvider;
    }

    public void sendPasswordResetEmail(String idUser, CompletedCallback completedCallback,
      ErrorCallback errorCallback) {
        this.idUser = idUser;
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            shootrUserService.sendPasswordResetEmail(idUser, localeProvider.getLanguage());
            notifyCompleted();
        } catch (ShootrException error) {
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

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }

}
