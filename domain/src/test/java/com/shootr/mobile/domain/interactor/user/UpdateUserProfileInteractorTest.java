package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.DomainValidationException;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateUserProfileInteractorTest {

    public static final String ID_USER = "idUser";

    @Mock UserRepository localUserRepository;
    @Mock UserRepository remoteUserRepository;
    @Mock SessionRepository sessionRepository;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock Interactor.CompletedCallback completedCallback;

    private UpdateUserProfileInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new UpdateUserProfileInteractor(interactorHandler,
          postExecutionThread,
          localUserRepository,
          remoteUserRepository,
          sessionRepository);
    }

    @Test public void shouldUpdateUserProfileInRemoteUserRepository() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(localUserRepository.getUserById(ID_USER)).thenReturn(user());

        interactor.updateProfile(user(), completedCallback, errorCallback);

        verify(remoteUserRepository).updateUserProfile(any(User.class));
    }

    @Test public void shouldSetCurrentUserInSessionRepository() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(localUserRepository.getUserById(ID_USER)).thenReturn(user());

        interactor.updateProfile(user(), completedCallback, errorCallback);

        verify(sessionRepository).setCurrentUser(any(User.class));
    }

    @Test public void shouldNotifyCommunicationErrorIfRemoteThrowsServerCommunicationException() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(localUserRepository.getUserById(ID_USER)).thenReturn(user());
        doThrow(ServerCommunicationException.class).when(remoteUserRepository).updateUserProfile(any(User.class));

        interactor.updateProfile(user(), completedCallback, errorCallback);

        verify(errorCallback).onError(any(ServerCommunicationException.class));
    }

    @Test public void shouldNotifyDomainValidationExceptionIfRemoteThrowsEmailAlreadyExistsException()
      throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(localUserRepository.getUserById(ID_USER)).thenReturn(user());
        doThrow(EmailAlreadyExistsException.class).when(remoteUserRepository).updateUserProfile(any(User.class));

        interactor.updateProfile(user(), completedCallback, errorCallback);

        verify(errorCallback).onError(any(DomainValidationException.class));
    }

    @Test public void shouldNotifyDomainValidationExceptionIfRemoteThrowsUsernameAlreadyExistsException()
      throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(localUserRepository.getUserById(ID_USER)).thenReturn(user());
        doThrow(UsernameAlreadyExistsException.class).when(remoteUserRepository)
          .updateUserProfile(any(User.class));

        interactor.updateProfile(user(), completedCallback, errorCallback);

        verify(errorCallback).onError(any(DomainValidationException.class));
    }

    @Test public void shouldNotifyLoadedWhenUserUpdatedSucessfully() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(localUserRepository.getUserById(ID_USER)).thenReturn(user());

        interactor.updateProfile(user(), completedCallback, errorCallback);

        verify(completedCallback).onCompleted();
    }

    private User user() {
        User user = new User();
        user.setIdUser(ID_USER);
        return user;
    }
}
