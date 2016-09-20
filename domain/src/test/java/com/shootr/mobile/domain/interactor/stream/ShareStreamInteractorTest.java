package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class ShareStreamInteractorTest {

  private static final String ID_STREAM = "idStream";
  @Mock ExternalStreamRepository remoteStreamRepository;
  @Mock Interactor.CompletedCallback completedCallback;
  @Mock Interactor.ErrorCallback errorCallback;

  private ShareStreamInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor =
        new ShareStreamInteractor(remoteStreamRepository, interactorHandler, postExecutionThread);
  }

  @Test public void shouldNotifyCompletedWhenShareStream() throws Exception {
    interactor.shareStream(ID_STREAM, completedCallback, errorCallback);

    verify(completedCallback).onCompleted();
  }

  @Test public void shouldNotifyErrorWhenRemoteStreamRepositoryThrowsException() throws Exception {
    doThrow(ServerCommunicationException.class).
        when(remoteStreamRepository).shareStream(ID_STREAM);

    interactor.shareStream(ID_STREAM, completedCallback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }
}
