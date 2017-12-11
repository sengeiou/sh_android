package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import javax.inject.Inject;

public class GetLandingStreamsInteractor implements Interactor{

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final StreamRepository streamRepository;


  private Interactor.Callback<LandingStreams> callback;
  private Interactor.ErrorCallback errorCallback;

  @Inject public GetLandingStreamsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, StreamRepository streamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.streamRepository = streamRepository;
  }

  public void getLandingStreams(Callback<LandingStreams> callback,
      ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    //TODO hacer la llamada al repository de streams y notificar el resultado
  }

  private void notifyLoaded(final LandingStreams result) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(result);
      }
    });
  }

  protected void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
}
