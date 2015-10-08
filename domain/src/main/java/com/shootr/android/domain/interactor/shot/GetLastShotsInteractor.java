package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import java.util.List;
import javax.inject.Inject;

public class GetLastShotsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository localShotRepository;
    private final ShotRepository remoteShotRepository;

    private String userId;
    private Callback<List<Shot>> callback;
    private ErrorCallback errorCallback;

    @Inject public GetLastShotsInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local ShotRepository localShotRepository, @Remote ShotRepository remoteShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
    }

    public void loadLastShots(String userId, Callback<List<Shot>> callback, ErrorCallback errorCallback) {
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
        List<Shot> lastShots = localShotRepository.getShotsFromUser(userId, 4);
        notifyLoaded(lastShots);
    }

    private void loadLastShotsFromRemote() {
        try {
            List<Shot> lastShots = remoteShotRepository.getShotsFromUser(userId, 4);
            notifyLoaded(lastShots);
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void notifyLoaded(final List<Shot> result) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
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
