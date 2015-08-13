package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.NiceShotRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.service.shot.ShootrShotService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

public class MarkNiceShotInteractorTest {

    private static final String SHOT_ID = "shot_id";

    @Mock ShootrShotService shootrShotService;
    @Mock NiceShotRepository niceShotRepository;
    @Mock ShotRepository localShotRepository;
    @Mock Interactor.CompletedCallback callback;

    private MarkNiceShotInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        TestInteractorHandler interactorHandler = new TestInteractorHandler();
        TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new MarkNiceShotInteractor(interactorHandler,
          postExecutionThread,
          shootrShotService,
          niceShotRepository);
    }

    @Test
    public void shouldNotifyCallbackAfterSendingToServer() throws Exception {
        interactor.markNiceShot(SHOT_ID, callback);

        InOrder inOrder = inOrder(callback, shootrShotService);
        inOrder.verify(shootrShotService).markNiceShot(SHOT_ID);
        inOrder.verify(callback).onCompleted();
    }

    @Test
    public void shouldNotifyCallbackWhenServiceFails() throws Exception {
        doThrow(new ServerCommunicationException(null)).when(shootrShotService).markNiceShot(anyString());

        interactor.markNiceShot(SHOT_ID, callback);

        verify(callback).onCompleted();
    }

    @Test
    public void shouldUnmarkNiceWhenServerFails() throws Exception {
        doThrow(new ServerCommunicationException(null)).when(shootrShotService).markNiceShot(anyString());

        interactor.markNiceShot(SHOT_ID, callback);

        verify(niceShotRepository).unmark(SHOT_ID);
    }
}