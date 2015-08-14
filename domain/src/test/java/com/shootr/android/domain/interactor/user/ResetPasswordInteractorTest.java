package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.ForgotPasswordResult;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class ResetPasswordInteractorTest {

    public static final String FAKE_USERNAME_STUB = "fake_username";
    private ResetPasswordInteractor interactor;
    @Mock ShootrUserService shootrUserService;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock Interactor.Callback<ForgotPasswordResult> completedCallback;
    @Mock ForgotPasswordResult forgotPasswordResult;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        interactor = new ResetPasswordInteractor(interactorHandler, postExecutionThread, shootrUserService);
    }

    @Test
    public void shouldCallbackCompletedWhenResetPasswordReturnsCorrectResult() throws Exception {
        doReturn(forgotPasswordResult).when(shootrUserService).performResetPassword(anyString());
        interactor.attempResetPassword(FAKE_USERNAME_STUB, completedCallback, errorCallback);
        verify(completedCallback).onLoaded(any(ForgotPasswordResult.class));
    }

    @Test
    public void shouldHadleServerErrorWhenAttempResetPasswordWithInvalidCredentials() throws Exception {
        doThrow(new ShootrException(){}).when(shootrUserService).performResetPassword(anyString());
        interactor.attempResetPassword(FAKE_USERNAME_STUB, completedCallback, errorCallback);
        verify(errorCallback).onError(any(ShootrException.class));
    }
}
