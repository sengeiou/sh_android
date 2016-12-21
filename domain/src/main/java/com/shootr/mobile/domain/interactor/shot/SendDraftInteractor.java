package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.domain.service.QueueRepository;
import com.shootr.mobile.domain.service.MessageSender;
import com.shootr.mobile.domain.service.dagger.Background;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

public class SendDraftInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final QueueRepository queueRepository;
  private final MessageSender messageSender;

  private List<Long> queuedShotIds;

  @Inject public SendDraftInteractor(InteractorHandler interactorHandler,
      QueueRepository queueRepository, @Background MessageSender messageSender) {
    this.interactorHandler = interactorHandler;
    this.queueRepository = queueRepository;
    this.messageSender = messageSender;
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
      QueuedShot queuedShot = queueRepository.getQueue(queuedShotId, QueueRepository.SHOT_TYPE);
      messageSender.sendMessage(queuedShot.getBaseMessage(), queuedShot.getImageFile());
    }
  }
}
