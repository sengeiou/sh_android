package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetOlderStreamTimelineInteractorTest {

  public static final long CURRENT_OLDEST_DATE = 0L;
  public static final String VISIBLE_STREAM_ID = "visible_stream_id";
  public static final String USER_ID = "user_id";
  private static final String STREAM_ID = "stream_id";
  private static final boolean FILTER_ACTIVATED = false;
  String[] TYPES_STREAM = StreamMode.TYPES_STREAM;
  @Mock ExternalShotRepository remoteShotRepository;
  @Mock StreamRepository localStreamRepository;
  @Mock Interactor.Callback<Timeline> callback;
  @Mock Interactor.ErrorCallback errorCallback;
  private GetOlderStreamTimelineInteractor getOlderStreamTimelineInteractor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    getOlderStreamTimelineInteractor =
        new GetOlderStreamTimelineInteractor(interactorHandler, postExecutionThread,
            remoteShotRepository, localStreamRepository);
    when(localStreamRepository.getStreamById(STREAM_ID, TYPES_STREAM)).thenReturn(stream());
  }

  @Test public void shouldGetShotsForStreamTimeline() throws Exception {
    getOlderStreamTimelineInteractor.loadOlderStreamTimeline(STREAM_ID, FILTER_ACTIVATED, CURRENT_OLDEST_DATE, callback,
        errorCallback);

    verify(remoteShotRepository).getShotsForStreamTimeline(any(StreamTimelineParameters.class));
  }

  @Test public void shouldNotifyErrorWhenRemoteShotRepositoryThrowsShootrException()
      throws Exception {
    when(remoteShotRepository.getShotsForStreamTimeline(
        any(StreamTimelineParameters.class))).thenThrow(new ShootrException() {
    });

    getOlderStreamTimelineInteractor.loadOlderStreamTimeline(STREAM_ID, FILTER_ACTIVATED, CURRENT_OLDEST_DATE, callback,
        errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyLoadedWhenRemoteRepositoryReturnsShots() throws Exception {
    when(remoteShotRepository.getShotsForStreamTimeline(
        any(StreamTimelineParameters.class))).thenReturn(shots());

    getOlderStreamTimelineInteractor.loadOlderStreamTimeline(STREAM_ID, FILTER_ACTIVATED, CURRENT_OLDEST_DATE, callback,
        errorCallback);

    verify(callback).onLoaded(any(Timeline.class));
  }

  private List<Shot> shots() {
    Shot shot = new Shot();
    shot.setPublishDate(new Date());
    Shot newerShot = new Shot();
    newerShot.setPublishDate(new Date());
    return Arrays.asList(shot, newerShot);
  }

  private User user() {
    User user = new User();
    user.setIdWatchingStream(VISIBLE_STREAM_ID);
    return user;
  }

  private Stream stream() {
    Stream stream = new Stream();
    stream.setId(STREAM_ID);
    return stream;
  }
}
