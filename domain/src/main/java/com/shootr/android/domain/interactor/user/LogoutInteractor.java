package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.DatabaseUtils;
import com.shootr.android.domain.service.user.ShootrUserService;
import javax.inject.Inject;

public class LogoutInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrUserService shootrUserService;
    private final DatabaseUtils databaseUtils;

    private ErrorCallback errorCallback;
    private CompletedCallback completedCallback;

    @Inject public LogoutInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      ShootrUserService shootrUserService, DatabaseUtils databaseUtils) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrUserService = shootrUserService;
        this.databaseUtils = databaseUtils;
    }

    public void attempLogout(CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            shootrUserService.performLogout();
            notifyLoaded();
            databaseUtils.clearDataOnLogout();
        } catch (ShootrException shootrException) {
            handleServerError(shootrException);
        }

    }

    private void notifyLoaded() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
            }
        });
    }

    private void handleServerError(ShootrException shootrException) {
        notifyError(shootrException);
    }

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
