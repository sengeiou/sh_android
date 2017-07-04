package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.FollowableType;
import com.shootr.mobile.domain.model.Following;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.follow.FollowRepository;
import javax.inject.Inject;

public class GetFollowingListInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final FollowRepository followRepository;

  private String idUser;
  private Long maxTimestamp;
  private Callback<Following> callback;
  private ErrorCallback errorCallback;

  @Inject public GetFollowingListInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote FollowRepository followRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.followRepository = followRepository;
  }

  public void getFollowingList(String idUser, Long maxTimestamp, Callback<Following> callback, ErrorCallback errorCallback) {
    this.idUser = idUser;
    this.maxTimestamp = maxTimestamp;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      Following following =
          followRepository.getFollowing(idUser, FollowableType.FOLLOWABLE_TYPES, maxTimestamp);
      notifyLoaded(following);
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  private void notifyLoaded(final Following result) {
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
