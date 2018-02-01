package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.FavoriteAdded;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.service.StreamIsAlreadyInFavoritesException;
import javax.inject.Inject;

public class FollowStreamInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final StreamRepository localStreamRepository;
  private final ExternalStreamRepository remoteStreamRepository;
  private final BusPublisher busPublisher;
  private final ActivityRepository localActivityRepository;

  private Interactor.CompletedCallback callback;
  private ErrorCallback errorCallback;

  private String idStream;

  @Inject public FollowStreamInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamRepository localStreamRepository,
      ExternalStreamRepository remoteStreamRepository, BusPublisher busPublisher,
      @Local ActivityRepository localActivityRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localStreamRepository = localStreamRepository;
    this.remoteStreamRepository = remoteStreamRepository;
    this.busPublisher = busPublisher;
    this.localActivityRepository = localActivityRepository;
  }

  public void follow(String idStream, CompletedCallback callback,
      ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.idStream = idStream;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    localStreamRepository.follow(idStream);
    notifyAdditionToBus();
    try {
      localActivityRepository.updateFollowStreamOnActivity(idStream);
      remoteStreamRepository.follow(idStream);
      notifyLoaded();
    } catch (Exception error) {
      notifyError(new StreamIsAlreadyInFavoritesException(error));
    }
  }

  protected void notifyLoaded() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onCompleted();
      }
    });
  }

  private void notifyError(final ShootrException e) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(e);
      }
    });
  }

  protected void notifyAdditionToBus() {
    busPublisher.post(new FavoriteAdded.Event());
  }
}
