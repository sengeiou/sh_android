package com.shootr.android.domain.interactor.user;

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
import static org.mockito.Mockito.verify;

public class SendPasswordResetEmailInteractorTest {

    public static final String FAKE_USER_ID = "fake_user_id";

    private SendPasswordResetEmailInteractor interactor;

    @Mock ShootrUserService shootrUserService;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock Interactor.CompletedCallback completedCallback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        interactor = new SendPasswordResetEmailInteractor(interactorHandler, postExecutionThread, shootrUserService);
    }

    @Test
    public void shouldCallbackCompletedWhenSendResetPasswordEmailWorksCorrectly(){
        doNothing().when(shootrUserService).sendPasswordResetEmail(anyString());
        interactor.sendPasswordResetEmail(FAKE_USER_ID, completedCallback, errorCallback);
        verify(completedCallback).onCompleted();
    }

    @Test
    public void shouldHadleServerErrorWhenSendResetPasswordEmailHasConnectionProblems(){
        doThrow(new ShootrException(){}).when(shootrUserService).sendPasswordResetEmail(anyString());
        interactor.sendPasswordResetEmail(FAKE_USER_ID, completedCallback, errorCallback);
        verify(errorCallback).onError(any(ShootrException.class));
    }

}
