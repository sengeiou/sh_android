package com.shootr.mobile.domain.interactor.timeline.privateMessage;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageTimeline;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.privateMessage.PrivateMessageRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetOlderPrivateMessageChannelTimelineInteractor implements Interactor {

  private final PrivateMessageRepository remotePrivateMessageRepository;
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;

  private Long currentOldestDate;
  private String idTargetUser;
  private Callback<PrivateMessageTimeline> callback;
  private ErrorCallback errorCallback;

  @Inject public GetOlderPrivateMessageChannelTimelineInteractor(
      @Remote PrivateMessageRepository remotePrivateMessageRepository, InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread) {
    this.remotePrivateMessageRepository = remotePrivateMessageRepository;
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
  }

  public void loadOlderTimeline(String idTargetUser, Long currentOldestDate, Callback<PrivateMessageTimeline> callback,
      ErrorCallback errorCallback) {
    this.idTargetUser = idTargetUser;
    this.currentOldestDate = currentOldestDate;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {

    try {
      PrivateMessageTimeline timeline =
          remotePrivateMessageRepository.getOlderPrivateMessages(idTargetUser,
              currentOldestDate);
      notifyLoaded(timeline);
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  private List<Shot> sortShotsByPublishDate(List<Shot> remoteShots) {
    Collections.sort(remoteShots, new Shot.NewerAboveComparator());
    return remoteShots;
  }

  private void notifyLoaded(final PrivateMessageTimeline timeline) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(timeline);
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

  //endregion
}
