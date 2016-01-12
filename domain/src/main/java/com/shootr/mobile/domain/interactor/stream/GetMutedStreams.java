package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.MuteRepository;
import com.shootr.mobile.domain.repository.Remote;
import java.util.List;
import javax.inject.Inject;

public class GetMutedStreams implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final MuteRepository localMuteRepository;
    private final MuteRepository remoteMuteRepository;

    private Callback<List<String>> callback;
    private ErrorCallback errorCallback;

    @Inject
    public GetMutedStreams(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local MuteRepository localMuteRepository, @Remote MuteRepository remoteMuteRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localMuteRepository = localMuteRepository;
        this.remoteMuteRepository = remoteMuteRepository;
    }

    public void loadMutedStreamIds(Callback<List<String>> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        tryLoadingLocalMutesAndThenRemote();
    }

    private void tryLoadingLocalMutesAndThenRemote() {
        List<String> mutedIdStreams = localMuteRepository.getMutedIdStreams();
        if (mutedIdStreams == null) {
            loadRemoteMutes();
        } else {
            notifyResult(mutedIdStreams);
        }
    }

    private void loadRemoteMutes() {
        try {
            notifyResult(remoteMuteRepository.getMutedIdStreams());
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void notifyResult(final List<String> user) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(user);
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
