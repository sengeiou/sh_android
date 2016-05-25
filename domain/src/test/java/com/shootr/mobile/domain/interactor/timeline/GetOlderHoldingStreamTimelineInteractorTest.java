package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamMode;
import com.shootr.mobile.domain.StreamTimelineParameters;
import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.UserRepository;
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

public class GetOlderHoldingStreamTimelineInteractorTest {

  public static final String VISIBLE_STREAM_ID = "visible_stream_id";
  public static final String USER_ID = "user_id";
  public static final long CURRENT_OLDEST_DATE = 0L;
  public static final String ID_USER = "id_user";
  private GetOlderHoldingStreamTimelineInteractor getOlderHoldingStreamTimelineInteractor;
  String[] TYPES_STREAM = StreamMode.TYPES_STREAM;
  @Mock SessionRepository sessionRepository;
  @Mock ShotRepository remoteShotRepository;
  @Mock StreamRepository localStreamRepository;
  @Mock Interactor.Callback<Timeline> callback;
  @Mock Interactor.ErrorCallback errorCallback;
  @Mock UserRepository localUserRepository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    getOlderHoldingStreamTimelineInteractor =
        new GetOlderHoldingStreamTimelineInteractor(interactorHandler, postExecutionThread,
            sessionRepository, remoteShotRepository, localStreamRepository, localUserRepository);
    when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
    when(localUserRepository.getUserById(USER_ID)).thenReturn(user());
    when(localStreamRepository.getStreamById(VISIBLE_STREAM_ID, TYPES_STREAM)).thenReturn(stream());
  }

  @Test public void shouldGetShotsForStreamTimeline() throws Exception {
    getOlderHoldingStreamTimelineInteractor.loadOlderHoldingStreamTimeline(ID_USER,
        CURRENT_OLDEST_DATE, callback, errorCallback);

    verify(remoteShotRepository).getUserShotsForStreamTimeline(any(StreamTimelineParameters.class));
  }

  @Test public void shouldNotifyErrorWhenRemoteShotRepositoryThrowsShootrException()
      throws Exception {
    when(remoteShotRepository.getUserShotsForStreamTimeline(
        any(StreamTimelineParameters.class))).thenThrow(new ShootrException() {
    });

    getOlderHoldingStreamTimelineInteractor.loadOlderHoldingStreamTimeline(ID_USER,
        CURRENT_OLDEST_DATE, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyLoadedWhenRemoteRepositoryReturnsShots() throws Exception {
    when(remoteShotRepository.getUserShotsForStreamTimeline(
        any(StreamTimelineParameters.class))).thenReturn(shots());

    getOlderHoldingStreamTimelineInteractor.loadOlderHoldingStreamTimeline(ID_USER,
        CURRENT_OLDEST_DATE, callback, errorCallback);

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
    return new Stream();
  }
}
