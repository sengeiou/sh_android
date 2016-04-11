package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.interactor.user.PerformEmailLoginInteractor;
import com.shootr.mobile.domain.service.user.ShootrUserService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.shootr.mobile.domain.interactor.Interactor.CompletedCallback;
import static com.shootr.mobile.domain.interactor.Interactor.ErrorCallback;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class EmailLoginInteractorTest {

    public static final String FAKE_USER_STUB = "fake_user";
    public static final String FAKE_PASSWORD_STUB = "fake_password";
    public static final String USERNAME_STUB = "username";
    public static final String PASSWORD_STUB = "password";
    @Mock ShootrUserService shootrUserService;
    @Mock ErrorCallback errorCallback;
    @Mock CompletedCallback completedCallback;
    private PerformEmailLoginInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        interactor = new PerformEmailLoginInteractor(interactorHandler, postExecutionThread, shootrUserService);
    }

    @Test public void shouldHadleServerErrorWhenAttempLoginWithInvalidCredentials() throws Exception {
        doThrow(new ShootrException() {
        }).when(shootrUserService).performLogin(anyString(), anyString());
        interactor.attempLogin(FAKE_USER_STUB, FAKE_PASSWORD_STUB, completedCallback, errorCallback);
        verify(errorCallback).onError(any(ShootrException.class));
    }

    @Test public void shouldCallbackCompletedWhenLoginReturnsCorrectResult() throws Exception {
        doNothing().when(shootrUserService).performLogin(anyString(), anyString());
        interactor.attempLogin(USERNAME_STUB, PASSWORD_STUB, completedCallback, errorCallback);
        verify(completedCallback).onCompleted();
    }
}
