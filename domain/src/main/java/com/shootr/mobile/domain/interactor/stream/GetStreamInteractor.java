package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.StreamRepository;

import javax.inject.Inject;

public class GetStreamInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamRepository localStreamRepository;
    private final StreamRepository remoteStreamRepository;

    private String idStream;
    private Callback callback;

    @Inject public GetStreamInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local StreamRepository localStreamRepository, @Remote StreamRepository remoteStreamRepository) {
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
        try {
            loadRemoteStream();
        }catch (ServerCommunicationException error){
            loadLocalStream();
        }
    }

    private void loadRemoteStream(){
        Stream remoteStream = remoteStreamRepository.getStreamById(idStream);
        if (remoteStream != null) {
            notifyLoaded(remoteStream);
        }else{
            // TODO: Notify error
        }
    }

    private void loadLocalStream(){
        Stream localStream = localStreamRepository.getStreamById(idStream);
        notifyLoaded(localStream);
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
