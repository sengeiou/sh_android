package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.FollowableType;
import com.shootr.mobile.domain.model.Follows;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.follow.FollowRepository;
import javax.inject.Inject;

public class GetStreamFollowersListInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final FollowRepository followRepository;

  private String idStream;
  private Long maxTimestamp;
  private Callback<Follows> callback;
  private ErrorCallback errorCallback;

  @Inject public GetStreamFollowersListInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote FollowRepository followRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.followRepository = followRepository;
  }

  public void getFollowerList(String idStream, Long maxTimestamp, Callback<Follows> callback,
      ErrorCallback errorCallback) {
    this.idStream = idStream;
    this.maxTimestamp = maxTimestamp;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      Follows following =
          followRepository.getStreamFollowers(idStream, FollowableType.FOLLOWABLE_TYPES, maxTimestamp);
      notifyLoaded(following);
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  private void notifyLoaded(final Follows result) {
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
