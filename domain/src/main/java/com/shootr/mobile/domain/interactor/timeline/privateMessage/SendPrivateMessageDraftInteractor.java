package com.shootr.mobile.domain.interactor.timeline.privateMessage;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.domain.service.MessageSender;
import com.shootr.mobile.domain.service.QueueRepository;
import com.shootr.mobile.domain.service.dagger.Background;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

public class SendPrivateMessageDraftInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final QueueRepository queueRepository;
  private final MessageSender messageSender;

  private List<Long> queuePrivateMessagesIds;

  @Inject public SendPrivateMessageDraftInteractor(InteractorHandler interactorHandler,
      QueueRepository queueRepository, @Background MessageSender messageSender) {
    this.interactorHandler = interactorHandler;
    this.queueRepository = queueRepository;
    this.messageSender = messageSender;
  }

  public void sendDraft(Long queuedShotId) {
    sendDrafts(Arrays.asList(queuedShotId));
  }

  public void sendDrafts(List<Long> queuedShotIds) {
    this.queuePrivateMessagesIds = queuedShotIds;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    for (Long messageShotId : queuePrivateMessagesIds) {
      QueuedShot queuedShot = queueRepository.getQueue(messageShotId, QueueRepository.MESSAGE_TYPE);
      messageSender.sendMessage(queuedShot.getBaseMessage(), queuedShot.getImageFile());
    }
  }
}
