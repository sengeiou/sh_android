package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Shot;
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
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MarkNiceShotInteractorTest {

    private static final String LOCAL_SHOT_ID = "local_id";
    private static final String NON_LOCAL_SHOT_ID = "nolocal_id";

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
          niceShotRepository,
          localShotRepository);
    }

    @Test
    public void shouldIncreaseNiceCountInLocalWhenShotExists() throws Exception {
        when(localShotRepository.getShot(LOCAL_SHOT_ID)).thenReturn(localShotWithZeroNices());

        interactor.markNiceShot(LOCAL_SHOT_ID, callback);

        verify(localShotRepository).putShot(localShotWithOneNice());
    }

    @Test
    public void shouldNotPutShotInLocalWhenShotDoesntExist() throws Exception {
        when(localShotRepository.getShot(NON_LOCAL_SHOT_ID)).thenReturn(null);

        interactor.markNiceShot(NON_LOCAL_SHOT_ID, callback);

        verify(localShotRepository, never()).putShot(any(Shot.class));
    }

    @Test
    public void shouldMarkNiceWhenShotExists() throws Exception {
        when(localShotRepository.getShot(LOCAL_SHOT_ID)).thenReturn(localShotWithZeroNices());

        interactor.markNiceShot(LOCAL_SHOT_ID, callback);

        verify(niceShotRepository).mark(LOCAL_SHOT_ID);
    }

    @Test
    public void shouldMarkNiceWhenShotDoesntExist() throws Exception {
        when(localShotRepository.getShot(NON_LOCAL_SHOT_ID)).thenReturn(null);

        interactor.markNiceShot(NON_LOCAL_SHOT_ID, callback);

        verify(niceShotRepository).mark(NON_LOCAL_SHOT_ID);
    }

    @Test
    public void shouldMarkNiceBeforeNotifyingCallback() throws Exception {
        interactor.markNiceShot(LOCAL_SHOT_ID, callback);

        InOrder inOrder = inOrder(niceShotRepository, callback);
        inOrder.verify(niceShotRepository).mark(LOCAL_SHOT_ID);
        inOrder.verify(callback).onCompleted();
    }

    @Test
    public void shouldNotifyCallbackBeforeSendingToServer() throws Exception {
        interactor.markNiceShot(LOCAL_SHOT_ID, callback);

        InOrder inOrder = inOrder(callback, shootrShotService);
        inOrder.verify(callback).onCompleted();
        inOrder.verify(shootrShotService).markNiceShot(LOCAL_SHOT_ID);
    }

    @Test
    public void shouldUndoIncreaseNiceCountInLocalWhenServerFailsIfShotExists() throws Exception {
        doThrow(new ServerCommunicationException(null)).when(shootrShotService).markNiceShot(anyString());
        when(localShotRepository.getShot(LOCAL_SHOT_ID)).thenReturn(localShotWithZeroNices());

        interactor.markNiceShot(LOCAL_SHOT_ID, callback);

        verify(localShotRepository, atLeastOnce()).putShot(localShotWithZeroNices());

    }


    @Test
    public void shouldNotPutShotInLocalWhenServerFailsIfShotDoesntExist() throws Exception {
        doThrow(new ServerCommunicationException(null)).when(shootrShotService).markNiceShot(anyString());
        when(localShotRepository.getShot(NON_LOCAL_SHOT_ID)).thenReturn(null);

        interactor.markNiceShot(NON_LOCAL_SHOT_ID, callback);

        verify(localShotRepository, never()).putShot(any(Shot.class));
    }

    @Test
    public void shouldNotifyCallbackTwiceWhenServiceFails() throws Exception {
        doThrow(new ServerCommunicationException(null)).when(shootrShotService).markNiceShot(anyString());

        interactor.markNiceShot(LOCAL_SHOT_ID, callback);

        verify(callback, times(2)).onCompleted();
    }

    @Test
    public void shouldUnmarkNiceWhenServerFails() throws Exception {
        doThrow(new ServerCommunicationException(null)).when(shootrShotService).markNiceShot(anyString());

        interactor.markNiceShot(LOCAL_SHOT_ID, callback);

        verify(niceShotRepository).unmark(LOCAL_SHOT_ID);
    }

    private Shot localShotWithZeroNices() {
        Shot shot = new Shot();
        shot.setIdShot(LOCAL_SHOT_ID);
        shot.setNiceCount(0);
        return shot;
    }

    private Shot localShotWithOneNice() {
        Shot shot = new Shot();
        shot.setIdShot(LOCAL_SHOT_ID);
        shot.setNiceCount(1);
        return shot;
    }
}