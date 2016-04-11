package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.FollowingBlockedUserException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.FollowRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FollowInteractorTest {

    public static final String ID_USER = "idUser";
    private FollowInteractor interactor;
    @Mock FollowRepository localFollowRepository;
    @Mock FollowRepository remoteFollowRepository;
    @Mock UserRepository remoteUserRepository;
    @Mock UserRepository localUserRepository;
    @Mock Interactor.CompletedCallback callback;
    @Mock Interactor.ErrorCallback errorCallback;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new FollowInteractor(interactorHandler,
          postExecutionThread,
          localFollowRepository,
          remoteFollowRepository,
          remoteUserRepository,
          localUserRepository);
    }

    @Test public void shouldFollowUserInLocalWhenFollow() throws Exception {
        interactor.follow(ID_USER, callback, errorCallback);

        verify(localFollowRepository).follow(ID_USER);
    }

    @Test public void shouldFollowUserInRemoteWhenFollow() throws Exception {
        interactor.follow(ID_USER, callback, errorCallback);

        verify(remoteFollowRepository).follow(ID_USER);
    }

    @Test public void shouldNotifyErrorIfTryingToFollowTheSameUserTwice() throws Exception {
        doThrow(FollowingBlockedUserException.class).when(remoteFollowRepository).follow(anyString());

        interactor.follow(ID_USER, callback, errorCallback);

        verify(errorCallback).onError(any(ShootrException.class));
    }

    @Test public void shouldSaveUserInLocalIfIsNotSaved() throws Exception {
        when(localUserRepository.getUserById(anyString())).thenReturn(null);
        when(remoteUserRepository.getUserById(anyString())).thenReturn(any(User.class));

        interactor.follow(ID_USER, callback, errorCallback);

        verify(localUserRepository).putUser(any(User.class));
    }

    @Test public void shouldNotSaveUserInLocalIfIsAlreadySaved() throws Exception {
        when(localUserRepository.getUserById(ID_USER)).thenReturn(new User());

        interactor.follow(ID_USER, callback, errorCallback);

        verify(localUserRepository, never()).putUser(any(User.class));
    }
}
