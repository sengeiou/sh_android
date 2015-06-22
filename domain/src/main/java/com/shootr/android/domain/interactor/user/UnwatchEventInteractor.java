package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import javax.inject.Inject;

public class UnwatchEventInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;

    private CompletedCallback completedCallback;

    @Inject public UnwatchEventInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
    }

    public void unwatchEvent(CompletedCallback completedCallback) {
        this.completedCallback = completedCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        notifyCompleted();
    }

    private void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
            }
        });
    }
}
