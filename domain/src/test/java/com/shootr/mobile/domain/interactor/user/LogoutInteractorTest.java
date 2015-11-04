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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class LogoutInteractorTest {

    private LogoutInteractor interactor;
    @Mock com.shootr.mobile.domain.service.user.ShootrUserService shootrUserService;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock Interactor.CompletedCallback completedCallback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler = new TestInteractorHandler();
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
        doThrow(new com.shootr.mobile.domain.exception.ShootrException() {
        }).when(shootrUserService).performLogout();
        interactor.attempLogout(completedCallback, errorCallback);
        verify(errorCallback).onError(any(com.shootr.mobile.domain.exception.ShootrException.class));
    }

    @Test
    public void shouldCallbackUserServiceWhenAttempLogout() {
        doNothing().when(shootrUserService).performLogout();
        interactor.attempLogout(completedCallback, errorCallback);
        verify(shootrUserService).performLogout();
    }

}
