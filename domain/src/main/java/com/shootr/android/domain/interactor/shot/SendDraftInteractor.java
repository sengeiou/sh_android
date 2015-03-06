package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.service.ShotQueueRepository;
import com.shootr.android.domain.service.ShotSender;
import com.shootr.android.domain.service.dagger.Background;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

public class SendDraftInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final ShotQueueRepository shotQueueRepository;
    private final ShotSender shotSender;

    private List<Long> queuedShotIds;

    @Inject public SendDraftInteractor(InteractorHandler interactorHandler, ShotQueueRepository shotQueueRepository,
      @Background ShotSender shotSender) {
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

    @Override public void execute() throws Throwable {
        for (Long queuedShotId : queuedShotIds) {
            QueuedShot queuedShot = shotQueueRepository.getShotQueue(queuedShotId);
            shotSender.sendShot(queuedShot.getShot(), queuedShot.getImageFile());
        }
    }
}
