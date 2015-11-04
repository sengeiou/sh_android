package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.utils.Preconditions;
import javax.inject.Inject;

public class RestoreStreamInteractor implements com.shootr.mobile.domain.interactor.Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.StreamRepository localStreamRepository;
    private final com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository;
    private String idStream;
    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;

    @Inject
    public RestoreStreamInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler, com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
      @com.shootr.mobile.domain.repository.Local
      com.shootr.mobile.domain.repository.StreamRepository localStreamRepository, @com.shootr.mobile.domain.repository.Remote
    com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamRepository = localStreamRepository;
        this.remoteStreamRepository = remoteStreamRepository;
    }

    public void restoreStream(String idStream, CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.idStream = Preconditions.checkNotNull(idStream);
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        try {
            remoteStreamRepository.restoreStream(idStream);
            localStreamRepository.restoreStream(idStream);
            notifyCompleted();
        } catch (com.shootr.mobile.domain.exception.ServerCommunicationException networkError) {
            notifyError(networkError);
        }
    }

    private void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                completedCallback.onCompleted();
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
