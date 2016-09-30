package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class RestoreStreamInteractorTest {

  private static final String STREAM_ID = "idStream";
  @Mock StreamRepository localStreamRepository;
  @Mock ExternalStreamRepository remoteStreamRepository;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private RestoreStreamInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor =
        new RestoreStreamInteractor(interactorHandler, postExecutionThread, localStreamRepository,
            remoteStreamRepository);
  }

  @Test public void shouldNotifyWhenRestoreStream() throws Exception {
    interactor.restoreStream(STREAM_ID, callback, errorCallback);

    verify(callback).onCompleted();
  }

  @Test public void shouldNotifyErrorWhenRemoteStreamRepositoryThrowsException() throws Exception {
    doThrow(ServerCommunicationException.class).
        when(remoteStreamRepository).restoreStream(STREAM_ID);

    interactor.restoreStream(STREAM_ID, callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }
}
