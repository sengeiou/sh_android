package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.domain.repository.UserRepository;

import java.util.List;

import javax.inject.Inject;

public class GetLastShotsInteractor implements Interactor {

    public static final int LAST_SHOTS_TRESHOLD = 11;
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository localShotRepository;
    private final ShotRepository remoteShotRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;

    private String userId;
    private Callback<List<Shot>> callback;
    private ErrorCallback errorCallback;

    @Inject public GetLastShotsInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local ShotRepository localShotRepository, @Remote ShotRepository remoteShotRepository,
      SessionRepository sessionRepository, @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
    }

    public void loadLastShots(String userId, Callback<List<Shot>> callback, ErrorCallback errorCallback) {
        this.userId = userId;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadLastShotsFromRemote();
    }

    private void loadLastShotsFromLocal() {
        List<Shot> lastShots = localShotRepository.getShotsFromUser(userId, LAST_SHOTS_TRESHOLD);
        notifyLoaded(lastShots);
    }

    private void loadLastShotsFromRemote() {
        try {
            List<Shot> remoteShots = remoteShotRepository.getShotsFromUser(userId, LAST_SHOTS_TRESHOLD);
            notifyLoaded(remoteShots);
            saveShotsForCurrentUserAndFollowing(remoteShots);
        } catch (ServerCommunicationException error) {
            loadLastShotsFromLocal();
            notifyError(error);
        }
    }

    private void saveShotsForCurrentUserAndFollowing(List<Shot> remoteShots) {
        if (userId.equals(sessionRepository.getCurrentUserId()) || localUserRepository.isFollowing(userId)) {
            localShotRepository.putShots(remoteShots);
        }
    }

    private void notifyLoaded(final List<Shot> result) {
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
