package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.MuteRepository;
import com.shootr.mobile.domain.repository.Remote;

import java.util.List;

import javax.inject.Inject;

public class GetMutedStreamsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final MuteRepository localMuteRepository;
    private final MuteRepository remoteMuteRepository;

    private Callback<List<String>> callback;

    @Inject
    public GetMutedStreamsInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local MuteRepository localMuteRepository, @Remote MuteRepository remoteMuteRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localMuteRepository = localMuteRepository;
        this.remoteMuteRepository = remoteMuteRepository;
    }

    public void loadMutedStreamIds(Callback<List<String>> callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        tryLoadingLocalMutesAndThenRemote();
    }

    private void tryLoadingLocalMutesAndThenRemote() {
        List<String> mutedIdStreams = localMuteRepository.getMutedIdStreams();
        if (mutedIdStreams.isEmpty()) {
            loadRemoteMutes();
        } else {
            notifyResult(mutedIdStreams);
        }
    }

    private void loadRemoteMutes() {
        try {
            notifyResult(remoteMuteRepository.getMutedIdStreams());
        } catch (ServerCommunicationException error) {
            /* swallow silently */
        }
    }

    private void notifyResult(final List<String> user) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(user);
            }
        });
    }

}
