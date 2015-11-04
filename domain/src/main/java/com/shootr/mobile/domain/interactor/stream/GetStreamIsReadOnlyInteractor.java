package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class GetStreamIsReadOnlyInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.StreamRepository localStreamRepository;
    private final com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository;

    private String streamId;
    private Callback<Boolean> callback;

    @Inject
    public GetStreamIsReadOnlyInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local
    com.shootr.mobile.domain.repository.StreamRepository localStreamRepository,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamRepository = localStreamRepository;
        this.remoteStreamRepository = remoteStreamRepository;
    }

    public void isStreamReadOnly(String streamId, Callback<Boolean> callback) {
        this.streamId = streamId;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        com.shootr.mobile.domain.Stream stream = localStreamRepository.getStreamById(streamId);
        if (stream == null) {
            stream = remoteStreamRepository.getStreamById(streamId);
        }
        checkNotNull(stream);
        notifyLoaded(stream.isRemoved());
    }

    private void notifyLoaded(final Boolean isRemoved) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onLoaded(isRemoved);
            }
        });
    }
}
