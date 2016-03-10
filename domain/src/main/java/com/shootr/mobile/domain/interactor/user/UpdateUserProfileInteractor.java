package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import javax.inject.Inject;

public class UpdateUserProfileInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;

    private Interactor.CompletedCallback callback;
    private Interactor.ErrorCallback errorCallback;

    @Inject public UpdateUserProfileInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
    }

    public void updateProfile(CompletedCallback callback, ErrorCallback errorCallback) {

    }

    @Override public void execute() throws Exception {

    }
}
