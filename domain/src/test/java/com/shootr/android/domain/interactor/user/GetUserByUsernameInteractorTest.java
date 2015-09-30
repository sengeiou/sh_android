package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.UserNotFoundException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.UserRepository;
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
        when(remoteUserRepository.getUserByUsername(anyString())).thenThrow(new ShootrException() {});

        interactor.searchUserByUsername(USERNAME_STUB, callback, errorCallback);

        verify(errorCallback).onError(any(ShootrException.class));
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
