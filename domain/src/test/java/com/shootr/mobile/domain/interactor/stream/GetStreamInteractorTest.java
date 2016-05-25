package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamMode;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.StreamRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetStreamInteractorTest {

  private static final String ID_STREAM = "idStream";
  String[] TYPES_STREAM = StreamMode.TYPES_STREAM;
  @Mock PostExecutionThread postExecutionThread;
  @Mock StreamRepository localStreamRepository;
  @Mock StreamRepository remoteStreamRepository;
  @Mock GetStreamInteractor.Callback callback;

  private GetStreamInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    MockitoAnnotations.initMocks(this);
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    interactor =
        new GetStreamInteractor(interactorHandler, postExecutionThread, localStreamRepository,
            remoteStreamRepository);
  }

  @Test public void shouldNotifyResultsWhenLoadRemoteStream() throws Exception {
    when(remoteStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(stream());

    interactor.loadStream(ID_STREAM, callback);

    verify(callback).onLoaded(any(Stream.class));
  }

  @Test public void shouldNotifyLocalResultsWhenLoadRemoteStreamThrowsServerComunicationException()
      throws Exception {
    doThrow(ServerCommunicationException.class).
        when(remoteStreamRepository).getStreamById(ID_STREAM, TYPES_STREAM);
    when(localStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(stream());

    interactor.loadStream(ID_STREAM, callback);

    verify(callback).onLoaded(any(Stream.class));
  }

  private Stream stream() {
    return new Stream();
  }
}
