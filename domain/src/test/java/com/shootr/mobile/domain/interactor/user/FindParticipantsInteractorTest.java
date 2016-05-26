package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class FindParticipantsInteractorTest {

  private static final String STREAM_ID = "streamId";
  private static final String QUERY = "query";
  @Mock UserRepository remoteUserRepository;
  @Mock PostExecutionThread postExecutionThread;
  @Mock Interactor.Callback<List<User>> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private FindParticipantsInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor = new FindParticipantsInteractor(interactorHandler, remoteUserRepository,
        postExecutionThread);
  }

  @Test public void shouldNotifyLoadedWhenObtainALlParticipants() throws Exception {
    interactor.obtainAllParticipants(STREAM_ID, QUERY, callback, errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyErrorWhenObtainParticipantsAndThrowsServerComunicationException()
      throws Exception {
    doThrow(ServerCommunicationException.class).when(remoteUserRepository)
        .findParticipants(anyString(), anyString());

    interactor.obtainAllParticipants(STREAM_ID, QUERY, callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }
}
