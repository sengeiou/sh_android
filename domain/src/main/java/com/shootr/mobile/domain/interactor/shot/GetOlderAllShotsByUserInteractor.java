package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetOlderAllShotsByUserInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository;

    private String userId;
    private Long currentOldestDate;
    private Callback<List<com.shootr.mobile.domain.Shot>> callback;
    private ErrorCallback errorCallback;

    @Inject public GetOlderAllShotsByUserInteractor(
      com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @com.shootr.mobile.domain.repository.Remote
    com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteShotRepository = remoteShotRepository;
    }

    public void loadAllShots(String userId, long currentOldestDate, Callback<List<com.shootr.mobile.domain.Shot>> callback, ErrorCallback errorCallback) {
        this.userId = userId;
        this.currentOldestDate = currentOldestDate;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            List<com.shootr.mobile.domain.Shot> remoteShots = remoteShotRepository.getAllShotsFromUserAndDate(userId, currentOldestDate);
            notifyLoaded(sortShotsByPublishDate(remoteShots));
        } catch (com.shootr.mobile.domain.exception.ShootrException error) {
            notifyError(error);
        }
    }

    private List<com.shootr.mobile.domain.Shot> sortShotsByPublishDate(List<com.shootr.mobile.domain.Shot> remoteShots) {
        Collections.sort(remoteShots, new com.shootr.mobile.domain.Shot.NewerAboveComparator());
        return remoteShots;
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
