package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.stream.MuteRepository;
import java.util.List;
import javax.inject.Inject;

public class GetMutedStreamsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final MuteRepository localMuteRepository;
  private final MuteRepository remoteMuteRepository;
  private boolean onlyLocal = false;
  private Callback<List<String>> callback;

  @Inject public GetMutedStreamsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local MuteRepository localMuteRepository,
      @Remote MuteRepository remoteMuteRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localMuteRepository = localMuteRepository;
    this.remoteMuteRepository = remoteMuteRepository;
  }

  public void loadMutedStreamsIdsFromLocal(Callback<List<String>> callback) {
    this.onlyLocal = true;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  public void loadMutedStreamIds() {
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    tryLoadingLocalMutesAndThenRemote();
  }

  private void tryLoadingLocalMutesAndThenRemote() {
    List<String> mutedIdStreams = localMuteRepository.getMutedIdStreams();
    if (!onlyLocal) {
      loadRemoteMutes();
    } else {
      notifyResult(mutedIdStreams);
    }
  }

  private void loadRemoteMutes() {
    try {
      remoteMuteRepository.getMutedIdStreams();
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
