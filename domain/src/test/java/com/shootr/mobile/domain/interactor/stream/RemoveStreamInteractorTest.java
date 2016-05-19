package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RemoveStreamInteractorTest {

  private static final String USER_ID = "userId";
  private static final String STREAM_ID = "streamId";
  @Mock StreamRepository localStreamRepository;
  @Mock StreamRepository remoteStreamRepository;
  @Mock SessionRepository sessionRepository;
  @Mock UserRepository localUserRepository;
  @Mock UserRepository remoteUserRepository;
  @Mock Interactor.CompletedCallback completedCallback;
  @Mock Interactor.ErrorCallback errorCallback;

  private RemoveStreamInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);

    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor =
        new RemoveStreamInteractor(interactorHandler, postExecutionThread, localStreamRepository,
            remoteStreamRepository, sessionRepository, localUserRepository, remoteUserRepository);
  }

  @Test public void shouldNotifyCompletedWhenRemoveAStream() throws Exception {
    when(localUserRepository.getUserById(anyString())).thenReturn(user());

    interactor.removeStream(STREAM_ID, completedCallback, errorCallback);

    verify(completedCallback).onCompleted();
  }

  @Test public void shouldNotifyErrorWhenRemoteUserRepositoryThrowsServerComunicationException()
      throws Exception {
    when(localUserRepository.getUserById(anyString())).thenReturn(user());
    doThrow(ServerCommunicationException.class).
        when(remoteUserRepository).updateWatch(any(User.class));

    interactor.removeStream(STREAM_ID, completedCallback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));

  }

  private User user() {
    User user = new User();
    user.setIdUser(USER_ID);
    return user;
  }
}
