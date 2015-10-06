package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.StreamRepository;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class RestoreStreamInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamRepository localStreamRepository;
    private final StreamRepository remoteStreamRepository;
    private String idStream;
    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;

    @Inject
    public RestoreStreamInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local StreamRepository localStreamRepository, @Remote StreamRepository remoteStreamRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamRepository = localStreamRepository;
        this.remoteStreamRepository = remoteStreamRepository;
    }

    public void restoreStream(String idStream, CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.idStream = checkNotNull(idStream);
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
        } catch (ServerCommunicationException networkError) {
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

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
