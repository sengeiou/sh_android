package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.StreamRepository;

import javax.inject.Inject;

public class ShareStreamInteractor implements Interactor {

    private final StreamRepository remoteStreamRepository;
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private String idStream;
    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;

    @Inject
    public ShareStreamInteractor(@Remote StreamRepository remoteStreamRepository, InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread) {
        this.remoteStreamRepository = remoteStreamRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
    }

    public void shareStream(String idStream, CompletedCallback callback, ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.completedCallback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            remoteStreamRepository.shareStream(idStream);
            notifyCompleted();
        } catch (ServerCommunicationException networkError) {
            notifyError(networkError);
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
