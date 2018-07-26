package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SocketRepository;
import javax.inject.Inject;

public class VerifyReceiptInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final SocketRepository socketRepository;

  private Callback<Boolean> callback;
  private String receipt;

  @Inject public VerifyReceiptInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote SocketRepository socketRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.socketRepository = socketRepository;
  }

  public void verifyReceipt(String receipt, Callback<Boolean> callback) {
    this.callback = callback;
    this.receipt = receipt;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    socketRepository.verifyReceipt(receipt);
    notify(true);
  }

  private void notify(final boolean result) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(result);
      }
    });
  }
}
