package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.StreamTimelineParameters;
import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ShotRepository;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetStreamHoldingTimelineInteractorTest {
  public static final String ID_STREAM = "id_stream";
  public static final String ID_USER = "id_user";
  public static final boolean GONE_BACKGROUND = false;
  private GetStreamHoldingTimelineInteractor getStreamHoldingTimelineInteractor;
  @Mock ShotRepository localShotRepository;
  @Mock ShotRepository remoteShotRepository;
  @Mock Interactor.Callback<Timeline> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    getStreamHoldingTimelineInteractor =
        new GetStreamHoldingTimelineInteractor(interactorHandler, postExecutionThread,
            localShotRepository, remoteShotRepository);
  }

  @Test public void shouldLoadLocalTimeline() throws Exception {
    getStreamHoldingTimelineInteractor.loadStreamHoldingTimeline(ID_STREAM, ID_USER,
        GONE_BACKGROUND, callback, errorCallback);

    verify(localShotRepository).getUserShotsForStreamTimeline(any(StreamTimelineParameters.class));
  }

  @Test public void shouldLoadRemoteTimelineIfLocalIsEmpty() throws Exception {
    when(localShotRepository.getUserShotsForStreamTimeline(
        any(StreamTimelineParameters.class))).thenReturn(Collections.<Shot>emptyList());

    getStreamHoldingTimelineInteractor.loadStreamHoldingTimeline(ID_STREAM, ID_USER,
        GONE_BACKGROUND, callback, errorCallback);

    verify(remoteShotRepository).getUserShotsForStreamTimeline(any(StreamTimelineParameters.class));
  }

  @Test public void shouldNotifyErrorWhenRemoteShotRepositoryThrowsException() throws Exception {
    when(remoteShotRepository.getUserShotsForStreamTimeline(
        any(StreamTimelineParameters.class))).thenThrow(
        new ServerCommunicationException(new Throwable()));

    getStreamHoldingTimelineInteractor.loadStreamHoldingTimeline(ID_STREAM, ID_USER,
        GONE_BACKGROUND, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyTimelineLoaded() throws Exception {
    getStreamHoldingTimelineInteractor.loadStreamHoldingTimeline(ID_STREAM, ID_USER,
        GONE_BACKGROUND, callback, errorCallback);

    verify(callback).onLoaded(any(Timeline.class));
  }
}
