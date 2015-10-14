package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetUserByIdInteractorTest {

    private GetUserByIdInteractor interactor;

    @Mock UserRepository localUserRepository;
    @Mock UserRepository remoteUserRepository;
    @Mock Interactor.Callback<User> callback;
    @Mock Interactor.ErrorCallback errorCallback;

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

    @Test public void shouldNotLoadUserFromLocalRepositoryIfRepositoryReturnsNull() throws Exception {
        when(localUserRepository.getUserById(anyString())).thenReturn(null);

        interactor.loadUserById(anyString(), callback, errorCallback);

        verify(callback, times(1)).onLoaded(any(User.class));
    }
}
