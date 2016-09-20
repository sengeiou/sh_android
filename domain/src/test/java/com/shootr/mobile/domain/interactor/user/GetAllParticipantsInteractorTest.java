package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.user.User;
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
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetAllParticipantsInteractorTest {

  private static final String STREAM_ID = "streamId";
  private static final Long TIMESTAMP = 1L;
  private static final boolean IS_PAGINATING = true;
  private static final boolean IS_NOT_PAGINATING = false;
  @Mock UserRepository remoteUserRepository;
  @Mock Interactor.Callback<List<User>> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private GetAllParticipantsInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor = new GetAllParticipantsInteractor(interactorHandler, remoteUserRepository,
        postExecutionThread);
  }

  @Test public void shouldNotifyLoadedWHenObtainAllParticipants() throws Exception {
    when(remoteUserRepository.getPeople()).thenReturn(users());
    when(remoteUserRepository.getAllParticipants(anyString(), anyLong())).thenReturn(
        new ArrayList<User>());

    interactor.obtainAllParticipants(STREAM_ID, TIMESTAMP, IS_PAGINATING, callback, errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyLoadedWHenObtainAllParticipantsAndIsNotPaginating()
      throws Exception {
    when(remoteUserRepository.getPeople()).thenReturn(users());
    when(remoteUserRepository.getAllParticipants(anyString(), anyLong())).thenReturn(
        new ArrayList<User>());

    interactor.obtainAllParticipants(STREAM_ID, TIMESTAMP, IS_NOT_PAGINATING, callback,
        errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyErrorWhenRemoteRepositoryThrowsServerComunicationException()
      throws Exception {
    when(remoteUserRepository.getPeople()).thenReturn(users());
    doThrow(ServerCommunicationException.class).when(remoteUserRepository)
        .getAllParticipants(anyString(), anyLong());

    interactor.obtainAllParticipants(STREAM_ID, TIMESTAMP, IS_NOT_PAGINATING, callback,
        errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }

  private List<User> users() {
    User user = new User();
    user.setIdWatchingStream(STREAM_ID);
    return Collections.singletonList(user);
  }
}
