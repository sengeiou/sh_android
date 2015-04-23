package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.interactor.user.PerformEmailLoginInteractor;
import com.shootr.android.domain.service.user.ShootrUserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.shootr.android.domain.interactor.Interactor.CompletedCallback;
import static com.shootr.android.domain.interactor.Interactor.ErrorCallback;
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
    private PerformEmailLoginInteractor interactor;
    @Mock ShootrUserService shootrUserService;
    @Mock ErrorCallback errorCallback;
    @Mock CompletedCallback completedCallback;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        interactor = new PerformEmailLoginInteractor(interactorHandler, postExecutionThread, shootrUserService);
    }

    @Test
    public void shouldHadleServerErrorWhenAttempLoginWithInvalidCredentials(){
        //TODO: Se llama al error callback
        doThrow(new ShootrException(){}).when(shootrUserService).performLogin(anyString(), anyString());
        interactor.attempLogin(FAKE_USER_STUB, FAKE_PASSWORD_STUB, completedCallback, errorCallback);
        verify(errorCallback).onError(any(ShootrException.class));
    }

    @Test
    public void shouldCallbackCompletedWhenLoginReturnsCorrectResult(){
        //TODO: Se llama al error callback
        doNothing().when(shootrUserService).performLogin(anyString(),anyString());
        interactor.attempLogin(USERNAME_STUB, PASSWORD_STUB, completedCallback, errorCallback);
        verify(completedCallback).onCompleted();
    }

}
