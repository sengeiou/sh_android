package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamMode;
import com.shootr.mobile.domain.StreamTimelineParameters;
import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.TimelineSynchronizationRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetStreamTimelineInteractorTest {

  private static final String WATCHING_STREAM_ID = "watching_stream";
  private static final String STREAM_AUTHOR_ID = "stream_author";
  private static final String ID_CURRENT_USER = "current_user";
  private static final String STREAM_ID = "stream";

  private static final Long DATE_OLDER = 1000L;
  private static final Long DATE_MIDDLE = 2000L;
  private static final Long DATE_NEWER = 3000L;
  private static final Boolean NOT_PAUSED = false;
  String[] TYPES_STREAM = StreamMode.TYPES_STREAM;

  @Mock ShotRepository localShotRepository;
  @Mock UserRepository localUserRepository;
  @Spy SpyCallback spyCallback = new SpyCallback();
  @Mock StreamRepository streamRepository;
  @Mock SessionRepository sessionRepository;
  @Mock TimelineSynchronizationRepository timelineSynchronizationRepository;

  private GetStreamTimelineInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    when(localUserRepository.getPeople()).thenReturn(people());
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_CURRENT_USER);

    interactor = new GetStreamTimelineInteractor(interactorHandler, postExecutionThread,
        localShotRepository);
  }

  @Test public void shouldLoadLocalShots() throws Exception {
    interactor.loadStreamTimeline(STREAM_ID, NOT_PAUSED, spyCallback);

    verify(localShotRepository).getShotsForStreamTimeline(any(StreamTimelineParameters.class));
  }

  @Test public void shouldCallbackShotsInOrderWithPublishDateComparator() throws Exception {
    setupWatchingStream();
    when(localShotRepository.getShotsForStreamTimeline(
        any(StreamTimelineParameters.class))).thenReturn(unorderedShots());

    interactor.loadStreamTimeline(STREAM_ID, NOT_PAUSED, spyCallback);
    List<Shot> localShotsReturned = spyCallback.timelinesReturned.get(0).getShots();

    assertThat(localShotsReturned).isSortedAccordingTo(new Shot.NewerAboveComparator());
  }

  @Test public void shouldNotifyLoadedShots() throws Exception {
    setupWatchingStream();
    when(localShotRepository.getShotsForStreamTimeline(
        any(StreamTimelineParameters.class))).thenReturn(unorderedShots());

    interactor.loadStreamTimeline(STREAM_ID, NOT_PAUSED, spyCallback);

    verify(spyCallback).onLoaded(any(Timeline.class));
  }

  private User currentUserWatching() {
    User user = new User();
    user.setIdUser(ID_CURRENT_USER);
    user.setIdWatchingStream(WATCHING_STREAM_ID);
    return user;
  }

  private List<Shot> unorderedShots() {
    return Arrays.asList(shotWithDate(DATE_MIDDLE), shotWithDate(DATE_OLDER),
        shotWithDate(DATE_NEWER));
  }

  private Shot shotWithDate(Long date) {
    Shot shot = new Shot();
    shot.setPublishDate(new Date(date));
    return shot;
  }

  private void setupWatchingStream() {
    when(localUserRepository.getUserById(ID_CURRENT_USER)).thenReturn(currentUserWatching());
    when(streamRepository.getStreamById(eq(WATCHING_STREAM_ID), eq(TYPES_STREAM))).thenReturn(
        watchingStream());
  }

  //region Stubs

  private List<User> people() {
    return Arrays.asList(new User());
  }

  private Stream watchingStream() {
    Stream stream = new Stream();
    stream.setId(WATCHING_STREAM_ID);
    stream.setAuthorId(STREAM_AUTHOR_ID);
    return stream;
  }

  //endregion

  //region Spies
  static class SpyCallback implements Interactor.Callback<Timeline> {

    public List<Timeline> timelinesReturned = new ArrayList<>();

    @Override public void onLoaded(Timeline timeline) {
      timelinesReturned.add(timeline);
    }
  }
  //endregion
}