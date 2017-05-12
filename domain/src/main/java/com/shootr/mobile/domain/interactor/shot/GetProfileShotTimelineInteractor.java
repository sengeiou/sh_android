package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.ProfileShotTimeline;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import javax.inject.Inject;

public class GetProfileShotTimelineInteractor implements Interactor {

  private final static int PROFILE_COUNT = 11;
  private final static int ALL_SHOTS_COUNT = 25;

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalShotRepository remoteShotRepository;
  private final InternalShotRepository internalShotRepository;
  private final SessionRepository sessionRepository;

  private String idUser;
  private Long maxTimestamp;
  private boolean isProfile;
  private boolean isUndo = false;
  private Callback<ProfileShotTimeline> callback;
  private ErrorCallback errorCallback;

  @Inject public GetProfileShotTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ExternalShotRepository remoteShotRepository,
      InternalShotRepository internalShotRepository, SessionRepository sessionRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteShotRepository = remoteShotRepository;
    this.internalShotRepository = internalShotRepository;
    this.sessionRepository = sessionRepository;
  }

  public void loadProfileShotTimeline(String userId, Long maxTimestamp,
      Callback<ProfileShotTimeline> callback, ErrorCallback errorCallback) {
    this.idUser = userId;
    this.maxTimestamp = maxTimestamp;
    this.isProfile = false;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  public void loadProfileShotTimelineUndo(String userId, Callback<ProfileShotTimeline> callback,
      ErrorCallback errorCallback) {
    this.idUser = userId;
    this.isProfile = true;
    this.isUndo = true;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  public void loadProfileShotTimeline(String userId, Callback<ProfileShotTimeline> callback,
      ErrorCallback errorCallback) {
    this.idUser = userId;
    this.isProfile = true;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    if (sessionRepository.getCurrentUserId().equals(idUser) && isProfile) {
      notifyLoaded(
          internalShotRepository.getProfileShotTimeline(idUser, maxTimestamp, PROFILE_COUNT));
      if (isUndo) {
        return;
      }
    }

    try {
      ProfileShotTimeline profileShotTimeline =
          remoteShotRepository.getProfileShotTimeline(idUser, maxTimestamp,
              isProfile ? PROFILE_COUNT : ALL_SHOTS_COUNT);
      notifyLoaded(profileShotTimeline);
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  private void notifyLoaded(final ProfileShotTimeline result) {
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
