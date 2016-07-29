package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetUserByIdInteractorTest {

    public static final String ID_USER = "idUser";
    @Mock UserRepository localUserRepository;
    @Mock UserRepository remoteUserRepository;
    @Mock Interactor.Callback<User> callback;
    @Mock Interactor.ErrorCallback errorCallback;
    private GetUserByIdInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new GetUserByIdInteractor(interactorHandler,
          postExecutionThread,
          localUserRepository,
          remoteUserRepository);
    }

    @Test public void shouldLoadUserFromLocalRepository() throws Exception {
        interactor.loadUserById(anyString(), callback, errorCallback);

        verify(localUserRepository).getUserById(anyString());
    }

    @Test public void shouldLoadUserFromRemoteRepository() throws Exception {
        interactor.loadUserById(anyString(), callback, errorCallback);

        verify(remoteUserRepository).getUserById(anyString());
    }

    @Test public void shouldLoadUserFromLocalRepositoryAndThenRemoteRepository() throws Exception {
        interactor.loadUserById(anyString(), callback, errorCallback);

        InOrder inOrder = inOrder(localUserRepository, remoteUserRepository);
        inOrder.verify(localUserRepository).getUserById(anyString());
        inOrder.verify(remoteUserRepository).getUserById(anyString());
    }

    @Test public void shouldCallbackUserOnceIfLocalRepositoryReturnsNull() throws Exception {
        when(localUserRepository.getUserById(anyString())).thenReturn(null);

        interactor.loadUserById(anyString(), callback, errorCallback);

        verify(callback, times(1)).onLoaded(any(User.class));
    }

    @Test public void shouldCallbackUserTwiceIfLocalRepositoryReturnsUser() throws Exception {
        when(localUserRepository.getUserById(anyString())).thenReturn(user());
        when(remoteUserRepository.getUserById(anyString())).thenReturn(user());

        interactor.loadUserById(anyString(), callback, errorCallback);

        verify(callback, times(2)).onLoaded(any(User.class));
    }

    @Test public void shouldCallbackErrorIfRemoteRepositoryFailsWithException() throws Exception {
        doThrow(new ServerCommunicationException(null)).when(remoteUserRepository).getUserById(anyString());

        interactor.loadUserById(anyString(), callback, errorCallback);

        verify(errorCallback).onError(any(ShootrException.class));
    }

    private User user() {
        User user = new User();
        user.setIdUser(ID_USER);
        return user;
    }
}
