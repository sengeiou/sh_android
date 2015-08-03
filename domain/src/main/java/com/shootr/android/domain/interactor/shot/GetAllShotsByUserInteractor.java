package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetAllShotsByUserInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository localShotRepository;
    private final ShotRepository remoteShotRepository;
    private String userId;
    private Callback<List<Shot>> callback;
    private ErrorCallback errorCallback;

    @Inject public GetAllShotsByUserInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local ShotRepository localShotRepository, @Remote ShotRepository remoteShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
    }

    public void loadAllShots(String userId, Callback<List<Shot>> callback, ErrorCallback errorCallback) {
        this.userId = userId;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        List<Shot> localShots = localShotRepository.getAllShotsFromUser(userId);
        if (!localShots.isEmpty()) {
            notifyLoaded(sortShotsByPublishDate(localShots));
        }
        try {
            List<Shot> remoteShots = remoteShotRepository.getAllShotsFromUser(userId);
            notifyLoaded(sortShotsByPublishDate(remoteShots));
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    private List<Shot> sortShotsByPublishDate(List<Shot> remoteShots) {
        Collections.sort(remoteShots, new Shot.NewerAboveComparator());
        return remoteShots;
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
