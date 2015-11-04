package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class ResetPasswordInteractorTest {

    public static final String FAKE_USERNAME_STUB = "fake_username";
    private ResetPasswordInteractor interactor;
    @Mock com.shootr.mobile.domain.service.user.ShootrUserService shootrUserService;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock Interactor.Callback<com.shootr.mobile.domain.ForgotPasswordResult> completedCallback;
    @Mock com.shootr.mobile.domain.ForgotPasswordResult forgotPasswordResult;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        interactor = new ResetPasswordInteractor(interactorHandler, postExecutionThread, shootrUserService);
    }

    @Test
    public void shouldCallbackCompletedWhenResetPasswordReturnsCorrectResult() throws Exception {
        doReturn(forgotPasswordResult).when(shootrUserService).performResetPassword(anyString());
        interactor.attempResetPassword(FAKE_USERNAME_STUB, completedCallback, errorCallback);
        verify(completedCallback).onLoaded(any(com.shootr.mobile.domain.ForgotPasswordResult.class));
    }

    @Test
    public void shouldHadleServerErrorWhenAttempResetPasswordWithInvalidCredentials() throws Exception {
        doThrow(new com.shootr.mobile.domain.exception.ShootrException(){}).when(shootrUserService).performResetPassword(anyString());
        interactor.attempResetPassword(FAKE_USERNAME_STUB, completedCallback, errorCallback);
        verify(errorCallback).onError(any(com.shootr.mobile.domain.exception.ShootrException.class));
    }
}
