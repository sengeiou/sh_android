package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.service.MessageSender;
import com.shootr.mobile.domain.service.QueueRepository;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SendDraftInteractorTest {

  public static final long QUEUED_SHOT_ID = 0L;
  private SendDraftInteractor sendDraftInteractor;
  @Mock QueueRepository queueRepository;
  @Mock MessageSender messageSender;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    sendDraftInteractor =
        new SendDraftInteractor(interactorHandler, queueRepository, messageSender);
  }

  @Test public void shouldSendShotGotFromShotQueue() throws Exception {
    when(queueRepository.getQueue(QUEUED_SHOT_ID, QueueRepository.SHOT_TYPE)).thenReturn(queuedShot());

    sendDraftInteractor.sendDraft(QUEUED_SHOT_ID);

    verify(messageSender).sendMessage(any(Shot.class), any(File.class));
  }

  private QueuedShot queuedShot() {
    QueuedShot queuedShot = new QueuedShot();
    queuedShot.setBaseMessage(new Shot());
    queuedShot.setImageFile(new File("anyString"));
    return queuedShot;
  }
}