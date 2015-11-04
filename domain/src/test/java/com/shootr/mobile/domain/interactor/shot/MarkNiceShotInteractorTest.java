package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class MarkNiceShotInteractorTest {

    private static final String SHOT_ID = "shot_id";

    @Mock com.shootr.mobile.domain.repository.NiceShotRepository niceShotRepository;
    @Mock com.shootr.mobile.domain.repository.ShotRepository localShotRepository;
    @Mock com.shootr.mobile.domain.repository.NiceShotRepository remoteNiceShotRepository;
    @Mock Interactor.CompletedCallback callback;

    private MarkNiceShotInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        TestInteractorHandler interactorHandler = new TestInteractorHandler();
        TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new MarkNiceShotInteractor(interactorHandler,
          postExecutionThread,
          niceShotRepository,
          remoteNiceShotRepository);
    }

    @Test
    public void shouldNotifyCallbackAfterSendingToServer() throws Exception {
        interactor.markNiceShot(SHOT_ID, callback);

        InOrder inOrder = inOrder(callback, remoteNiceShotRepository);
        inOrder.verify(remoteNiceShotRepository).mark(SHOT_ID);
        inOrder.verify(callback).onCompleted();
    }

    @Test
    public void shouldNotifyCallbackWhenServiceFails() throws Exception {
        doThrow(new com.shootr.mobile.domain.exception.ServerCommunicationException(null)).when(remoteNiceShotRepository).mark(anyString());

        interactor.markNiceShot(SHOT_ID, callback);

        verify(callback).onCompleted();
    }

    @Test
    public void shouldUnmarkNiceWhenServerFails() throws Exception {
        doThrow(new com.shootr.mobile.domain.exception.ServerCommunicationException(null)).when(remoteNiceShotRepository).mark(anyString());

        interactor.markNiceShot(SHOT_ID, callback);

        verify(niceShotRepository).unmark(SHOT_ID);
    }

    @Test
    public void shouldNotSendToServiceWhenRepositoryFailsWithNiceAlreadyMarked() throws Exception {
        doThrow(new com.shootr.mobile.domain.exception.NiceAlreadyMarkedException()).when(niceShotRepository).mark(anyString());

        interactor.markNiceShot(SHOT_ID, callback);

        verify(remoteNiceShotRepository, never()).mark(anyString());

    }
}