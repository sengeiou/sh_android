package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.MuteRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetMutedStreamInteractorTest {

  private static final String STREAM_ID = "idStream";
  @Mock PostExecutionThread postExecutionThread;
  @Mock MuteRepository localMuteRepository;
  @Mock MuteRepository remoteMuteRepository;
  @Mock Interactor.Callback<List<String>> callback;

  private GetMutedStreamsInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    InteractorHandler interactorHandler = new TestInteractorHandler();
    interactor =
        new GetMutedStreamsInteractor(interactorHandler, postExecutionThread, localMuteRepository,
            remoteMuteRepository);
  }

  @Test public void shouldNotifyLocalResultsWhenLoadLocalMutedStreams() throws Exception {
    when(localMuteRepository.getMutedIdStreams()).thenReturn(mutedStreams());

    interactor.loadMutedStreamIds(callback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyRemoteResultsWhenLocalMutedStreamsAreEmpty() throws Exception {
    when(localMuteRepository.getMutedIdStreams()).thenReturn(new ArrayList<String>());
    when(remoteMuteRepository.getMutedIdStreams()).thenReturn(mutedStreams());

    interactor.loadMutedStreamIds(callback);

    verify(callback).onLoaded(anyList());
  }

  private List<String> mutedStreams() {
    return Arrays.asList(STREAM_ID);
  }
}
