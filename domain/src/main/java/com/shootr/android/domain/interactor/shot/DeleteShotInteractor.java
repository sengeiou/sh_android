package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import javax.inject.Inject;

public class DeleteShotInteractor implements Interactor {

    private final ShotRepository localShotRepository;
    private final ShotRepository remoteShotRepository;
    private final ActivityRepository localActivityRepository;
    private final PostExecutionThread postExecutionThread;
    private final InteractorHandler interactorHandler;
    private String idShot;
    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;

    @Inject public DeleteShotInteractor(@Local ShotRepository localShotRepository, @Remote ShotRepository remoteShotRepository,
      @Local ActivityRepository localActivityRepository, PostExecutionThread postExecutionThread, InteractorHandler interactorHandler) {
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.localActivityRepository = localActivityRepository;
        this.postExecutionThread = postExecutionThread;
        this.interactorHandler = interactorHandler;
    }

    public void deleteShot(String idShot, CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.idShot = idShot;
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            localShotRepository.deleteShot(idShot);
            remoteShotRepository.deleteShot(idShot);
            localActivityRepository.deleteActivitiesWithShot(idShot);
            notifyLoaded();
        } catch (ServerCommunicationException networkError) {
            notifyError(networkError);
        }
    }

    private void notifyLoaded() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
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
}