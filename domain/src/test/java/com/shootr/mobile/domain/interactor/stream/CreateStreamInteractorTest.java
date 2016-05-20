package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateStreamInteractorTest {

  private static final String ID_STREAM = "idStream";
  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  private static final Integer PUBLIC_MODE = 0;
  private static final String TOPIC = "topic";
  private static final boolean NOTIFY = true;
  private static final boolean NOTIFY_TOPIC = true;
  private static final String SHORT_TITLE = "a";
  private static final String USER_ID = "userId";
  private static final String USER_NAME = "userName";
  @Mock PostExecutionThread postExecutionThread;
  @Mock SessionRepository sessionRepository;
  @Mock StreamRepository remoteStreamRepository;
  @Mock StreamRepository localStreamRepository;
  @Mock LocaleProvider localeProvider;
  @Mock CreateStreamInteractor.Callback callback;
  @Mock Interactor.ErrorCallback errorCallback;
  @Mock UserRepository localUserRepository;

  private CreateStreamInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
    when(localUserRepository.getUserById(USER_ID)).thenReturn(user());
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    interactor =
        new CreateStreamInteractor(interactorHandler, postExecutionThread, sessionRepository,
            remoteStreamRepository, localStreamRepository, localeProvider, localUserRepository);
  }

  @Test public void shouldThrowsDomainValidationExceptionWhenIsNotAValidStream() throws Exception {

    interactor.sendStream(null, SHORT_TITLE, DESCRIPTION, PUBLIC_MODE, TOPIC, NOTIFY, NOTIFY_TOPIC,
        callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyWhenSaveAValidStream() throws Exception {
    when(localStreamRepository.getStreamById(ID_STREAM)).thenReturn(new Stream());

    interactor.sendStream(ID_STREAM, TITLE, DESCRIPTION, PUBLIC_MODE, TOPIC, NOTIFY, NOTIFY_TOPIC,
        callback, errorCallback);

    verify(callback).onLoaded(any(Stream.class));
  }

  private User user() {
    User user = new User();

    user.setIdUser(USER_ID);
    user.setUsername(USER_NAME);

    return user;
  }
}