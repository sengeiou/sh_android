package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.MuteRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class MuteInteractorTest {

  private static final String STREAM_ID = "streamId";
  @Mock MuteRepository localMuteRepository;
  @Mock MuteRepository remoteMuteRepository;
  @Mock BusPublisher busPublisher;
  @Mock Interactor.CompletedCallback callback;

  private MuteInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();
    TestInteractorHandler interactorHandler = new TestInteractorHandler();

    interactor = new MuteInteractor(interactorHandler, postExecutionThread, localMuteRepository,
        remoteMuteRepository, busPublisher);
  }

  @Test public void shouldNotifiyMuteWhenComplete() throws Exception {
    interactor.mute(STREAM_ID, callback);

    verify(callback, atLeastOnce()).onCompleted();
  }

  @Test public void shouldCallMuteInLocalRepository() throws Exception {
    interactor.mute(STREAM_ID, callback);

    verify(localMuteRepository).mute(STREAM_ID);
  }
}
