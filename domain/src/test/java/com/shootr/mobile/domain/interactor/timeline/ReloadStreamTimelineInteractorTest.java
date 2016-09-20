package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.repository.ShotRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReloadStreamTimelineInteractorTest {
  public static final String ID_STREAM = "id_stream";
  private ReloadStreamTimelineInteractor reloadStreamTimelineInteractor;
  @Mock ShotRepository remoteShotRepository;
  @Mock Interactor.Callback<Timeline> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    reloadStreamTimelineInteractor =
        new ReloadStreamTimelineInteractor(interactorHandler, postExecutionThread,
            remoteShotRepository);
  }

  @Test public void shouldLoadShotsFromRemoteRepository() throws Exception {
    reloadStreamTimelineInteractor.loadStreamTimeline(ID_STREAM, callback, errorCallback);

    verify(remoteShotRepository).getShotsForStreamTimeline(any(StreamTimelineParameters.class));
  }

  @Test public void shouldNotifyLoadedAfterLoadShot() throws Exception {
    reloadStreamTimelineInteractor.loadStreamTimeline(ID_STREAM, callback, errorCallback);

    verify(callback).onLoaded(any(Timeline.class));
  }

  @Test public void shouldNotifyErrorWhenRemoteRepositoryThrowsServerCommunicationException()
      throws Exception {
    when(remoteShotRepository.getShotsForStreamTimeline(
        any(StreamTimelineParameters.class))).thenThrow(
        new ServerCommunicationException(new Throwable()));

    reloadStreamTimelineInteractor.loadStreamTimeline(ID_STREAM, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }
}
