package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.QueuedShot;
import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.service.ShotQueueRepository;
import com.shootr.mobile.domain.service.ShotSender;
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
  @Mock ShotQueueRepository shotQueueRepository;
  @Mock ShotSender shotSender;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    sendDraftInteractor =
        new SendDraftInteractor(interactorHandler, shotQueueRepository, shotSender);
  }

  @Test public void shouldSendShotGotFromShotQueue() throws Exception {
    when(shotQueueRepository.getShotQueue(QUEUED_SHOT_ID)).thenReturn(queuedShot());

    sendDraftInteractor.sendDraft(QUEUED_SHOT_ID);

    verify(shotSender).sendShot(any(Shot.class), any(File.class));
  }

  private QueuedShot queuedShot() {
    QueuedShot queuedShot = new QueuedShot();
    queuedShot.setShot(new Shot());
    queuedShot.setImageFile(new File("anyString"));
    return queuedShot;
  }
}