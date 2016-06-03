package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetStreamMediaInteractorTest {

  private static final String ID_STREAM = "idStream";
  @Mock ShotRepository remoteShotRepository;
  @Mock ShotRepository localShotRepository;
  @Mock UserRepository remoteUserRepository;
  @Mock UserRepository localUserRepository;
  @Mock SessionRepository sessionRepository;
  @Mock Interactor.Callback<List<Shot>> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private com.shootr.mobile.domain.interactor.shot.GetStreamMediaInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor =
        new com.shootr.mobile.domain.interactor.shot.GetStreamMediaInteractor(interactorHandler,
            postExecutionThread, remoteShotRepository, localShotRepository, remoteUserRepository,
            localUserRepository, sessionRepository);
  }

  @Test public void shouldNotifyMediaWhenGetShotsFromRepository() throws Exception {
    when(localUserRepository.getPeople()).thenReturn(users());
    when(remoteUserRepository.getPeople()).thenReturn(users());

    interactor.getStreamMedia(ID_STREAM, callback, errorCallback);

    verify(callback, atLeastOnce()).onLoaded(anyList());
  }

  @Test public void shouldNotifyErrorWhenUserRemoteRepositoryThrowsServerComunicationException()
      throws Exception {
    when(localUserRepository.getPeople()).thenReturn(users());
    doThrow(ServerCommunicationException.class).
        when(remoteUserRepository).getPeople();

    interactor.getStreamMedia(ID_STREAM, callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }

  private List<User> users() {
    return Collections.singletonList(new User());
  }
}
