package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import javax.inject.Inject;

public class GetEventInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamRepository localStreamRepository;
    private final StreamRepository remoteStreamRepository;

    private String idEvent;
    private Callback callback;

    @Inject public GetEventInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread, @Local
    StreamRepository localStreamRepository,
      @Remote StreamRepository remoteStreamRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamRepository = localStreamRepository;
        this.remoteStreamRepository = remoteStreamRepository;
    }

    public void loadEvent(String idEvent, Callback callback) {
        this.idEvent = idEvent;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        Stream localStream = localStreamRepository.getStreamById(idEvent);
        if (localStream != null) {
            notifyLoaded(localStream);
        } else {
            Stream remoteStream = remoteStreamRepository.getStreamById(idEvent);
            if (remoteStream != null) {
                notifyLoaded(remoteStream);
            } else {
              //TODO notify error...
            }
        }
    }

    private void notifyLoaded(final Stream stream) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(stream);
            }
        });
    }

    public interface Callback {

        void onLoaded(Stream stream);

    }
}
