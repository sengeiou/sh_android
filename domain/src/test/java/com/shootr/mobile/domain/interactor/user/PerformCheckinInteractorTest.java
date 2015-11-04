package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
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

    @Mock com.shootr.mobile.domain.service.user.ShootrUserService shootrUserService;
    @Mock com.shootr.mobile.domain.interactor.Interactor.CompletedCallback completedCallback;
    @Mock com.shootr.mobile.domain.interactor.Interactor.ErrorCallback errorCallback;

    private PerformCheckinInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler = new TestInteractorHandler();
        com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new PerformCheckinInteractor(interactorHandler, postExecutionThread, shootrUserService);
    }

    @Test public void shouldCallbackCompleteIfServiceDoesntFail() throws Exception {
        doNothing().when(shootrUserService).checkInStream(anyString());

        interactor.performCheckin(ANY_STREAM_ID, completedCallback, errorCallback);

        verify(completedCallback).onCompleted();
    }

    @Test public void shouldCallbackErrorIfServiceFailsWithServerCommunicationException() throws Exception {
        doThrow(new com.shootr.mobile.domain.exception.ServerCommunicationException(null)).when(shootrUserService).checkInStream(anyString());

        interactor.performCheckin(ANY_STREAM_ID, completedCallback, errorCallback);

        verify(errorCallback).onError(any(com.shootr.mobile.domain.exception.ShootrException.class));
    }

    @Test public void shouldNotCallbackCompletedIfServiceFailsWithServerCommunicationException() throws Exception {
        doThrow(new com.shootr.mobile.domain.exception.ServerCommunicationException(null)).when(shootrUserService).checkInStream(anyString());

        interactor.performCheckin(ANY_STREAM_ID, completedCallback, errorCallback);

        verify(completedCallback, never()).onCompleted();
    }
}