package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SocketRepository;
import javax.inject.Inject;

public class GetPromotedTermsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final SocketRepository socketRepository;

  private CompletedCallback callback;
  private String idStream;

  @Inject public GetPromotedTermsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote SocketRepository socketRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.socketRepository = socketRepository;
  }

  public void getPromotedTerms(String idStream, CompletedCallback callback) {
    this.callback = callback;
    this.idStream = idStream;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    socketRepository.getPromotedTerms(idStream);
    notifyComplete();
  }

  private void notifyComplete() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onCompleted();
      }
    });
  }
}
