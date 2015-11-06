package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
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

    @Inject
    public DeleteShotInteractor(@Local ShotRepository localShotRepository, @Remote ShotRepository remoteShotRepository,
      @Local ActivityRepository localActivityRepository, PostExecutionThread postExecutionThread,
      InteractorHandler interactorHandler) {
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

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
}