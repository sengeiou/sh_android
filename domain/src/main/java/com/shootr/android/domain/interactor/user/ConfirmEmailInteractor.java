package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.service.user.ShootrUserService;
import javax.inject.Inject;

public class ConfirmEmailInteractor implements Interactor{

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrUserService shootrUserService;

    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    @Inject public ConfirmEmailInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      ShootrUserService shootrUserService) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrUserService = shootrUserService;
    }

    public void confirmEmail(CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.callback = completedCallback;
        this.errorCallback = errorCallback;
        this.interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            shootrUserService.requestEmailConfirmation();
            notifyLoaded();
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }

    }

    private void notifyLoaded() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onCompleted();
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
