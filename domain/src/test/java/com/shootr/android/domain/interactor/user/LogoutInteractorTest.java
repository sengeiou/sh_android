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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class LogoutInteractorTest {

    private LogoutInteractor interactor;
    @Mock ShootrUserService shootrUserService;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock Interactor.CompletedCallback completedCallback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        interactor = new LogoutInteractor(interactorHandler, postExecutionThread, shootrUserService);
    }

    @Test
    public void shouldCallbackCompletedWhenLogoutReturnsCorrectResult(){
        doNothing().when(shootrUserService).performLogout();
        interactor.attempLogout(completedCallback, errorCallback);
        verify(completedCallback).onCompleted();
    }

    @Test
    public void shouldHadleServerErrorWhenAttempLogoutWithoutConnection(){
        doThrow(new ShootrException(){}).when(shootrUserService).performLogout();
        interactor.attempLogout(completedCallback, errorCallback);
        verify(errorCallback).onError(any(ShootrException.class));
    }

    @Test
    public void shouldCallbackUserServiceWhenAttempLogout() {
        doNothing().when(shootrUserService).performLogout();
        interactor.attempLogout(completedCallback, errorCallback);
        verify(shootrUserService).performLogout();
    }

}
