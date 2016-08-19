package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.shootr.mobile.domain.interactor.Interactor.CompletedCallback;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnwatchStreamInteractorTest {

  public static final String USER_ID = "user_id";
  public static final String WATCHING_STREAM_ID = "watching_stream_id";

  @Mock SessionRepository sessionRepository;
  @Mock UserRepository localUserRepository;
  @Mock UserRepository remoteUserRepository;
  @Mock CompletedCallback completedCallback;
  @Mock BusPublisher busPublisher;

  private UnwatchStreamInteractor unwatchStreamInteractor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    this.unwatchStreamInteractor =
        new UnwatchStreamInteractor(interactorHandler, postExecutionThread, sessionRepository,
            localUserRepository, remoteUserRepository, busPublisher);
  }

  //region tests
  @Test public void shouldUpdateUserInfoInSessionRepositoryWhenUnwatch() throws Throwable {
    setupUserWithWatchingStream();

    unwatchStreamInteractor.unwatchStream(completedCallback);

    verify(sessionRepository).setCurrentUser(user());
  }

  @Test public void shouldUpdateUserInfoInLocalRepositoryWhenUnwatch() {
    setupUserWithWatchingStream();

    unwatchStreamInteractor.unwatchStream(completedCallback);

    verify(localUserRepository).updateWatch(user());
  }

  @Test public void shouldUpdateUserInfoInRemoteRepositoryWhenUnwatch() {
    setupUserWithWatchingStream();

    unwatchStreamInteractor.unwatchStream(completedCallback);

    verify(remoteUserRepository).updateWatch(user());
  }

  @Test public void shouldupdateWatchWithNoWatchingStreamInSessionRepositoryWhenUnwatch() {
    setupUserWithWatchingStream();

    unwatchStreamInteractor.unwatchStream(completedCallback);

    ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
    verify(sessionRepository).setCurrentUser(argument.capture());
    assertThat(argument.getValue().getIdWatchingStream()).isNull();
  }

  @Test public void shouldNotifyCompletedAfterLocalUserInfoUpdated() {
    setupUserWithWatchingStream();

    unwatchStreamInteractor.unwatchStream(completedCallback);

    InOrder inOrder = inOrder(localUserRepository, completedCallback);
    inOrder.verify(localUserRepository).updateWatch(user());
    inOrder.verify(completedCallback).onCompleted();
  }

  @Test public void shouldNotifyCompletedBeforeRemoteUserInfoUpdated() {
    setupUserWithWatchingStream();

    unwatchStreamInteractor.unwatchStream(completedCallback);

    InOrder inOrder = inOrder(completedCallback, remoteUserRepository);
    inOrder.verify(completedCallback).onCompleted();
    inOrder.verify(remoteUserRepository).updateWatch(user());
  }
  //endregion

  //region stubbers
  private User user() {
    User user = new User();
    user.setIdUser(USER_ID);
    return user;
  }

  private User userWithWatchingStream() {
    User user = new User();
    user.setIdUser(USER_ID);
    user.setIdWatchingStream(WATCHING_STREAM_ID);
    return user;
  }

  //endregion

  private void setupUserWithWatchingStream() {
    when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
    when(localUserRepository.getUserById(USER_ID)).thenReturn(userWithWatchingStream());
  }
}
