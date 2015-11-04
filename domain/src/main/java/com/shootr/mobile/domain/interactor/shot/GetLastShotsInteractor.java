package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.List;
import javax.inject.Inject;

public class GetLastShotsInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.ShotRepository localShotRepository;
    private final com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository;
    private final com.shootr.mobile.domain.repository.SessionRepository sessionRepository;
    private final UserRepository localUserRepository;

    private String userId;
    private Callback<List<com.shootr.mobile.domain.Shot>> callback;
    private ErrorCallback errorCallback;

    @Inject public GetLastShotsInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Local com.shootr.mobile.domain.repository.ShotRepository localShotRepository,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository,
      com.shootr.mobile.domain.repository.SessionRepository sessionRepository,
      @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
    }

    public void loadLastShots(String userId, Callback<List<com.shootr.mobile.domain.Shot>> callback, ErrorCallback errorCallback) {
        this.userId = userId;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }
    @Override public void execute() throws Exception {
        loadLastShotsFromLocal();
        loadLastShotsFromRemote();
    }

    private void loadLastShotsFromLocal() {
        List<com.shootr.mobile.domain.Shot> lastShots = localShotRepository.getShotsFromUser(userId, 4);
        notifyLoaded(lastShots);
    }

    private void loadLastShotsFromRemote() {
        try {
            List<com.shootr.mobile.domain.Shot> remoteShots = remoteShotRepository.getShotsFromUser(userId, 4);
            notifyLoaded(remoteShots);
            saveShotsForCurrentUserAndFollowing(remoteShots);
        } catch (com.shootr.mobile.domain.exception.ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void saveShotsForCurrentUserAndFollowing(List<com.shootr.mobile.domain.Shot> remoteShots) {
        if (sessionRepository.getCurrentUserId().equals(userId) || localUserRepository.isFollowing(userId)) {
            localShotRepository.putShots(remoteShots);
        }
    }

    private void notifyLoaded(final List<com.shootr.mobile.domain.Shot> result) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onLoaded(result);
            }
        });
    }

    protected void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }

}
