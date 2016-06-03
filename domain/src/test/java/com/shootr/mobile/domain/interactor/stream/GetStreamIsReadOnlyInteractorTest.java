package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamMode;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.service.NetworkNotAvailableException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetStreamIsReadOnlyInteractorTest {

  public static final String ID_STREAM = "idStream";
  String[] TYPES_STREAM = StreamMode.TYPES_STREAM;
  private GetStreamIsReadOnlyInteractor interactor;
  @Mock StreamRepository localStreamRepository;
  @Mock StreamRepository remoteStreamRepository;
  @Mock Interactor.Callback<Boolean> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    interactor = new GetStreamIsReadOnlyInteractor(interactorHandler, postExecutionThread,
        localStreamRepository, remoteStreamRepository);
  }

  @Test public void shouldLoadStreamFromLocalRepository() throws Exception {
    interactor.isStreamReadOnly(ID_STREAM, callback, errorCallback);

    verify(localStreamRepository).getStreamById(ID_STREAM, TYPES_STREAM);
  }

  @Test public void shouldLoadStreamFromRemoteIfNotFoundInLocalRepository() throws Exception {
    when(localStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(null);

    interactor.isStreamReadOnly(ID_STREAM, callback, errorCallback);

    verify(remoteStreamRepository).getStreamById(ID_STREAM, TYPES_STREAM);
  }

  @Test public void shouldNotifyLoadedIfStreamIsInLocalRepository() throws Exception {
    when(localStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(stream());

    interactor.isStreamReadOnly(ID_STREAM, callback, errorCallback);

    verify(callback).onLoaded(anyBoolean());
  }

  @Test public void shouldNotifyLoadedIfStreamIsInRemoteRepository() throws Exception {
    when(localStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(null);
    when(remoteStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(stream());

    interactor.isStreamReadOnly(ID_STREAM, callback, errorCallback);

    verify(callback).onLoaded(anyBoolean());
  }

  @Test public void shouldNotifyErrorIfRemoteStreamIsNull() throws Exception {
    interactor.isStreamReadOnly(ID_STREAM, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyErrorIfRemoteRepositoryThrowsServerCommunicationException()
      throws Exception {
    when(localStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(null);
    when(remoteStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM)).thenThrow(
        new ServerCommunicationException(new Throwable()));

    interactor.isStreamReadOnly(ID_STREAM, callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }

  @Test public void shouldNotifyErrorIfRemoteRepositoryReturnNullStream() throws Exception {
    when(localStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(null);
    when(remoteStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(null);

    interactor.isStreamReadOnly(ID_STREAM, callback, errorCallback);

    verify(errorCallback).onError(any(NetworkNotAvailableException.class));
  }

  @Test public void shouldNotifyLoadedIfRemoteRepositoryReturnNotStream() throws Exception {
    when(localStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(null);
    when(remoteStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(stream());

    interactor.isStreamReadOnly(ID_STREAM, callback, errorCallback);

    verify(callback).onLoaded(stream().isRemoved());
  }

  private Stream stream() {
    return new Stream();
  }
}
