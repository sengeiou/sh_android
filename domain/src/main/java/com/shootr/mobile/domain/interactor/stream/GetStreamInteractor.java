package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.repository.Local;
import javax.inject.Inject;

public class GetStreamInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.StreamRepository localStreamRepository;
    private final com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository;

    private String idStream;
    private Callback callback;

    @Inject public GetStreamInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local com.shootr.mobile.domain.repository.StreamRepository localStreamRepository, @com.shootr.mobile.domain.repository.Remote
    com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamRepository = localStreamRepository;
        this.remoteStreamRepository = remoteStreamRepository;
    }

    public void loadStream(String idStream, Callback callback) {
        this.idStream = idStream;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        com.shootr.mobile.domain.Stream localStream = localStreamRepository.getStreamById(idStream);
        if (localStream != null) {
            notifyLoaded(localStream);
        } else {
            com.shootr.mobile.domain.Stream remoteStream = remoteStreamRepository.getStreamById(idStream);
            if (remoteStream != null) {
                notifyLoaded(remoteStream);
            } else {
              //TODO notify error...
            }
        }
    }

    private void notifyLoaded(final com.shootr.mobile.domain.Stream stream) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(stream);
            }
        });
    }

    public interface Callback {

        void onLoaded(com.shootr.mobile.domain.Stream stream);

    }
}
