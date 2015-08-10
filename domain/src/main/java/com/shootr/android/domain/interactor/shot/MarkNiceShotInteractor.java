package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.NiceShotRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.service.shot.ShootrShotService;
import javax.inject.Inject;

public class MarkNiceShotInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrShotService shootrShotService;
    private final NiceShotRepository niceShotRepository;
    private final ShotRepository localShotRepository;

    private String idShot;
    private CompletedCallback completedCallback;

    @Inject public MarkNiceShotInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      ShootrShotService shootrShotService, NiceShotRepository niceShotRepository, @Local ShotRepository localShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrShotService = shootrShotService;
        this.niceShotRepository = niceShotRepository;
        this.localShotRepository = localShotRepository;
    }

    public void markNiceShot(String idShot, CompletedCallback completedCallback) {
        this.idShot = idShot;
        this.completedCallback = completedCallback;
        this.interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        niceShotRepository.mark(idShot);
        incrementLocalCount();
        notifyCompleted();
        shootrShotService.markNiceShot(idShot);
    }

    protected void incrementLocalCount() {
        Shot shot = localShotRepository.getShot(idShot);
        if (shot != null) {
            int niceCount = shot.getNiceCount();
            shot.setNiceCount(niceCount + 1);
            localShotRepository.putShot(shot);
        }
    }

    protected void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                completedCallback.onCompleted();
            }
        });
    }
}
