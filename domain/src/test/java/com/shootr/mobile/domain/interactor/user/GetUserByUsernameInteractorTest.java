package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetUserByUsernameInteractorTest {

    private static final String USERNAME_STUB = "username";
    private static final String LOCAL_USER_ID = "local_user";
    private static final String REMOTE_USER_ID = "remote_user";

    @Mock Interactor.ErrorCallback errorCallback;
    @Mock PostExecutionThread postExecutionThread;
    @Mock Interactor.Callback<User> callback;
    @Mock UserRepository localUserRepository;
    @Mock UserRepository remoteUserRepository;

    private GetUserByUsernameInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new GetUserByUsernameInteractor(interactorHandler,
          remoteUserRepository,
          localUserRepository,
          postExecutionThread);
    }

    @Test public void shouldCallbackLocalUserIfLocalRepositoryReturnsAUser() throws Exception {
        when(localUserRepository.getUserByUsername(USERNAME_STUB)).thenReturn(localUser());

        interactor.searchUserByUsername(USERNAME_STUB, callback, errorCallback);

        verify(callback).onLoaded(localUser());
    }

    @Test public void shouldCallbackRemoteUserIfRemoteUserReturnsAUser() throws Exception {
        when(remoteUserRepository.getUserByUsername(USERNAME_STUB)).thenReturn(remoteUser());

        interactor.searchUserByUsername(USERNAME_STUB, callback, errorCallback);

        verify(callback).onLoaded(remoteUser());
    }

    @Test public void shouldPutRemoteUserInLocalIfLocalRepositoryReturnsAUser() throws Exception {
        when(localUserRepository.getUserByUsername(USERNAME_STUB)).thenReturn(localUser());
        when(remoteUserRepository.getUserByUsername(USERNAME_STUB)).thenReturn(remoteUser());

        interactor.searchUserByUsername(USERNAME_STUB, callback, errorCallback);

        verify(localUserRepository).putUser(remoteUser());
    }

    @Test public void shouldNotPutRemoteUserInLocalIfLocalRepositoryDoesntReturnAUser() throws Exception {
        when(localUserRepository.getUserByUsername(USERNAME_STUB)).thenReturn(null);
        when(remoteUserRepository.getUserByUsername(USERNAME_STUB)).thenReturn(remoteUser());

        interactor.searchUserByUsername(USERNAME_STUB, callback, errorCallback);

        verify(localUserRepository, never()).putUser(any(User.class));
    }

    @Test public void shouldCallbackErrorIfRemoteRepositoryFails() throws Exception {
        when(remoteUserRepository.getUserByUsername(anyString())).thenThrow(new com.shootr.mobile.domain.exception.ShootrException() {});

        interactor.searchUserByUsername(USERNAME_STUB, callback, errorCallback);

        verify(errorCallback).onError(any(com.shootr.mobile.domain.exception.ShootrException.class));
    }

    private User remoteUser() {
        User user = new User();
        user.setIdUser(REMOTE_USER_ID);
        return user;
    }

    private User localUser() {
        User user = new User();
        user.setIdUser(LOCAL_USER_ID);
        return user;
    }
}
