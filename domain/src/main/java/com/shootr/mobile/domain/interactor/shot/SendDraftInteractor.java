package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.interactor.Interactor;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

public class SendDraftInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.service.ShotQueueRepository shotQueueRepository;
    private final com.shootr.mobile.domain.service.ShotSender shotSender;

    private List<Long> queuedShotIds;

    @Inject public SendDraftInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler, com.shootr.mobile.domain.service.ShotQueueRepository shotQueueRepository,
      @com.shootr.mobile.domain.service.dagger.Background com.shootr.mobile.domain.service.ShotSender shotSender) {
        this.interactorHandler = interactorHandler;
        this.shotQueueRepository = shotQueueRepository;
        this.shotSender = shotSender;
    }

    public void sendDraft(Long queuedShotId) {
        sendDrafts(Arrays.asList(queuedShotId));
    }

    public void sendDrafts(List<Long> queuedShotIds) {
        this.queuedShotIds = queuedShotIds;

        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        for (Long queuedShotId : queuedShotIds) {
            com.shootr.mobile.domain.QueuedShot queuedShot = shotQueueRepository.getShotQueue(queuedShotId);
            shotSender.sendShot(queuedShot.getShot(), queuedShot.getImageFile());
        }
    }
}
