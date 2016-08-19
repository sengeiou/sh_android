package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetFollowingInteractorTest {

  @Mock UserRepository remoteUserRepository;
  @Mock UserRepository localUserRepository;
  @Mock Interactor.Callback<List<User>> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private GetFollowingInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    InteractorHandler interactorHandler = new TestInteractorHandler();

    interactor =
        new GetFollowingInteractor(interactorHandler, postExecutionThread, remoteUserRepository,
            localUserRepository);
  }

  @Test public void shouldNotifyLocalPeopleWhenLocalUserRepositoryHavePeople() throws Exception {
    when(localUserRepository.getPeople()).thenReturn(users());

    interactor.obtainPeople(callback, errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyRemotePeopleWhenLocalUserRepositoryDoNotHavePeople()
      throws Exception {
    when(remoteUserRepository.getPeople()).thenReturn(users());
    when(localUserRepository.getPeople()).thenReturn(new ArrayList<User>());

    interactor.obtainPeople(callback, errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyEmptyListWhenRemoteUserRepositoryDoNotHavePeople()
      throws Exception {
    when(remoteUserRepository.getPeople()).thenReturn(new ArrayList<User>());
    when(localUserRepository.getPeople()).thenReturn(new ArrayList<User>());

    interactor.obtainPeople(callback, errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyErrorWhenRemoteRepositoryThrowsServerComunicationException()
      throws Exception {
    doThrow(ServerCommunicationException.class).
        when(remoteUserRepository).getPeople();
    when(localUserRepository.getPeople()).thenReturn(new ArrayList<User>());

    interactor.obtainPeople(callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }

  private List<User> users() {
    User user = new User();
    return Collections.singletonList(user);
  }
}
