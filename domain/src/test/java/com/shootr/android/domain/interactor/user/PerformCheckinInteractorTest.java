package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.service.user.ShootrUserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PerformCheckinInteractorTest {

    private static final String ANY_STREAM_ID = "any_stream_id";

    @Mock ShootrUserService shootrUserService;
    @Mock Interactor.CompletedCallback completedCallback;
    @Mock Interactor.ErrorCallback errorCallback;

    private PerformCheckinInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new PerformCheckinInteractor(interactorHandler, postExecutionThread, shootrUserService);
    }

    @Test public void shouldCallbackCompleteIfServiceDoesntFail() throws Exception {
        doNothing().when(shootrUserService).checkInStream(anyString());

        interactor.performCheckin(ANY_STREAM_ID, completedCallback, errorCallback);

        verify(completedCallback).onCompleted();
    }

    @Test public void shouldCallbackErrorIfServiceFailsWithServerCommunicationException() throws Exception {
        doThrow(new ServerCommunicationException(null)).when(shootrUserService).checkInStream(anyString());

        interactor.performCheckin(ANY_STREAM_ID, completedCallback, errorCallback);

        verify(errorCallback).onError(any(ShootrException.class));
    }

    @Test public void shouldNotCallbackCompletedIfServiceFailsWithServerCommunicationException() throws Exception {
        doThrow(new ServerCommunicationException(null)).when(shootrUserService).checkInStream(anyString());

        interactor.performCheckin(ANY_STREAM_ID, completedCallback, errorCallback);

        verify(completedCallback, never()).onCompleted();
    }
}