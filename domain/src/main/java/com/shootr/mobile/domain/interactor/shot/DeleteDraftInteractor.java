package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.interactor.Interactor;
import javax.inject.Inject;

public class DeleteDraftInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.service.ShotQueueRepository shotQueueRepository;

    private Long queuedShotId;
    private Callback callback;

    @Inject public DeleteDraftInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler, com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
      com.shootr.mobile.domain.service.ShotQueueRepository shotQueueRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shotQueueRepository = shotQueueRepository;
    }

    public void deleteDraft(Long queuedShotId, Callback callback) {
        this.queuedShotId = queuedShotId;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        com.shootr.mobile.domain.QueuedShot queuedShot = shotQueueRepository.getShotQueue(queuedShotId);
        shotQueueRepository.remove(queuedShot);
        notifyDeleted();
    }

    private void notifyDeleted() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onDeleted();
            }
        });
    }

    public interface Callback {

        void onDeleted();
    }
}
