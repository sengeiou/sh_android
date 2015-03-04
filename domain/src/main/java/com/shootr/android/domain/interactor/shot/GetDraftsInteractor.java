package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.service.ShotQueueRepository;
import java.util.List;
import javax.inject.Inject;

public class GetDraftsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotQueueRepository shotQueueRepository;

    private Callback callback;

    @Inject public GetDraftsInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      ShotQueueRepository shotQueueRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shotQueueRepository = shotQueueRepository;
    }

    public void loadDrafts(Callback callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        List<QueuedShot> failedShots = shotQueueRepository.getFailedShotQueue();
        callback.onLoaded(failedShots);
    }

    public interface Callback {

        void onLoaded(List<QueuedShot> drafts);
    }
}
