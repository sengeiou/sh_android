package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.service.NetworkNotAvailableException;
import javax.inject.Inject;

public class GetStreamIsReadOnlyInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final StreamRepository localStreamRepository;
  private final ExternalStreamRepository remoteStreamRepository;

  private String streamId;
  private Callback<Boolean> callback;
  private ErrorCallback errorCallback;

  @Inject public GetStreamIsReadOnlyInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamRepository localStreamRepository,
      ExternalStreamRepository remoteStreamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localStreamRepository = localStreamRepository;
    this.remoteStreamRepository = remoteStreamRepository;
  }

  public void isStreamReadOnly(String streamId, Callback<Boolean> callback,
      ErrorCallback errorCallback) {
    this.streamId = streamId;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    Stream stream = localStreamRepository.getStreamById(streamId, StreamMode.TYPES_STREAM);
    if (stream == null) {
      getRemoteStream();
    } else {
      notifyLoaded(stream.isRemoved());
    }
  }

  private void getRemoteStream() {
    Stream stream;
    try {
      stream = remoteStreamRepository.getStreamById(streamId, StreamMode.TYPES_STREAM);
      if (stream == null) {
        notifyError(new NetworkNotAvailableException(new Throwable()));
      } else {
        notifyLoaded(stream.isRemoved());
      }
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private void notifyLoaded(final Boolean isRemoved) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(isRemoved);
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
