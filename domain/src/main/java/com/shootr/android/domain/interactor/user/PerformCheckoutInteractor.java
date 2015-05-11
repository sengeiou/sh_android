package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.service.user.ShootrUserService;

import javax.inject.Inject;

public class PerformCheckoutInteractor implements Interactor{
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrUserService shootrUserService;
    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;

    private String idEvent;

    @Inject
    public PerformCheckoutInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
                                    ShootrUserService shootrUserService) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrUserService = shootrUserService;
    }

    public void performCheckout(String idEvent, CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        this.idEvent = idEvent;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        try {
            shootrUserService.checkOutCurrentEvent();
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
