package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.FollowRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class BanUserInteractorTest {

    public static final String ID_USER = "idUser";

    @Mock FollowRepository localFollowRepository;
    @Mock FollowRepository remoteFollowRepository;
    @Mock Interactor.CompletedCallback callback;
    @Mock Interactor.ErrorCallback errorCallback;

    private BanUserInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new BanUserInteractor(interactorHandler,
          postExecutionThread,
          localFollowRepository,
          remoteFollowRepository);
    }

    @Test public void shouldBanUserInLocalWhenBanUser() throws Exception {
        interactor.ban(ID_USER, callback, errorCallback);

        verify(localFollowRepository).ban(ID_USER);
    }

    @Test public void shouldBanUserInRemoteWhenBanUser() throws Exception {
        interactor.ban(ID_USER, callback, errorCallback);

        verify(remoteFollowRepository).ban(ID_USER);
    }

    @Test public void shouldUnbanUserInLocalIfRemoteThrowsError() throws Exception {
        doThrow(ServerCommunicationException.class).when(remoteFollowRepository).ban(anyString());

        interactor.ban(ID_USER, callback, errorCallback);

        verify(localFollowRepository).unban(ID_USER);
    }

    @Test public void shouldNotifyErrorIfRemoteThrowsError() throws Exception {
        doThrow(ServerCommunicationException.class).when(remoteFollowRepository).ban(anyString());

        interactor.ban(ID_USER, callback, errorCallback);

        verify(errorCallback).onError(any(ServerCommunicationException.class));
    }
}
