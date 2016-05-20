package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamInfo;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetStreamInfoInteractorTest {

  private static final String STREAM_ID = "streamId";
  private static final String USER_ID = "userId";
  private static final String OTHER_USER_ID = "otherUserId";
  @Mock UserRepository localUserRepository;
  @Mock UserRepository remoteUserRepository;
  @Mock StreamRepository remoteStreamRepository;
  @Mock StreamRepository localStreamRepository;
  @Mock SessionRepository sessionRepository;
  @Mock GetStreamInfoInteractor.Callback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private GetStreamInfoInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor =
        new GetStreamInfoInteractor(interactorHandler, postExecutionThread, localUserRepository,
            remoteUserRepository, remoteStreamRepository, localStreamRepository, sessionRepository);
  }

  @Test public void shouldNotifyStreamInfoWhenObtainInfoFromRepository() throws Exception {
    setupLocalRepositories();
    setupRemoteRepositories();
    when(sessionRepository.getCurrentUserId()).thenReturn(OTHER_USER_ID);

    interactor.obtainStreamInfo(STREAM_ID, callback, errorCallback);

    verify(callback, atLeastOnce()).onLoaded(any(StreamInfo.class));
  }

  private void setupRemoteRepositories() {
    when(remoteUserRepository.getUserById(anyString())).thenReturn(user());
    when(remoteStreamRepository.getStreamById(anyString())).thenReturn(stream());
    when(remoteUserRepository.getPeople()).thenReturn(users());
  }

  private void setupLocalRepositories() {
    when(localUserRepository.getUserById(anyString())).thenReturn(user());
    when(localStreamRepository.getStreamById(anyString())).thenReturn(stream());
    when(localUserRepository.getPeople()).thenReturn(users());
  }

  private User user() {
    User user = new User();
    user.setIdUser(USER_ID);
    user.setIdWatchingStream(STREAM_ID);
    return user;
  }

  private Stream stream() {
    Stream stream = new Stream();
    stream.setId(STREAM_ID);
    return stream;
  }

  private List<User> users() {
    return Collections.singletonList(user());
  }
}