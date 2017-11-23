package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import javax.inject.Inject;

public class GetConnectionTimesInteractor implements Interactor {

  private static final long CONNECTION_TIMES = 3;

  private final StreamRepository localStreamRepository;
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private String idStream;
  private boolean storeConnection;
  private Callback<Long> callback;

  @Inject public GetConnectionTimesInteractor(@Local StreamRepository localStreamRepository,
      InteractorHandler interactorHandler, PostExecutionThread postExecutionThread) {
    this.localStreamRepository = localStreamRepository;
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
  }

  public void getConnectionTimes(String idStream, boolean storeConnection, Callback<Long> callback) {
    this.idStream = idStream;
    this.callback = callback;
    this.storeConnection = storeConnection;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    long connectionTimes = localStreamRepository.getConnectionTimes(idStream);
    storeConnection(connectionTimes);
    notifyLoaded(connectionTimes);
  }

  private void storeConnection(long connectionTimes) {
    if (storeConnection) {
      if (connectionTimes <= CONNECTION_TIMES) {
        localStreamRepository.storeConnection(idStream, connectionTimes + 1);
      }
    }
  }

  protected void notifyLoaded(final long times) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(times);
      }
    });
  }
}
