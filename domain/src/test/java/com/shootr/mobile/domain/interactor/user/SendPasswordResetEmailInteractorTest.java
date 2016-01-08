package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.service.user.ShootrUserService;
import com.shootr.mobile.domain.utils.LocaleProvider;
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
    @Mock ShootrUserService shootrUserService;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock Interactor.CompletedCallback completedCallback;
    @Mock LocaleProvider localeProvider;
    private SendPasswordResetEmailInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        interactor = new SendPasswordResetEmailInteractor(interactorHandler, postExecutionThread, shootrUserService,
          localeProvider);
    }

    @Test public void shouldCallbackCompletedWhenSendResetPasswordEmailWorksCorrectly() throws Exception {
        doNothing().when(shootrUserService).sendPasswordResetEmail(anyString(), anyString());
        interactor.sendPasswordResetEmail(FAKE_USER_ID, completedCallback, errorCallback);
        verify(completedCallback).onCompleted();
    }

    @Test public void shouldHadleServerErrorWhenSendResetPasswordEmailHasConnectionProblems() throws Exception {
        doThrow(new ShootrException() {
        }).when(shootrUserService).sendPasswordResetEmail(anyString(), anyString());
        interactor.sendPasswordResetEmail(FAKE_USER_ID, completedCallback, errorCallback);
        verify(errorCallback).onError(any(ShootrException.class));
    }
}
