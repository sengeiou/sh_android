package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.activity.Activity;
import com.shootr.mobile.domain.model.activity.ActivityTimeline;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RefreshActivityTimelineInteractorTest {

  public static final String ID_WATCHING_STREAM = "idWatchingStream";
  public static final String ID_USER = "idUser";
  private static final Boolean NOT_PAUSED = false;
  public static final boolean NOT_USER_ACTIVITY_TIMELINE = false;
  public static final boolean IS_USER_ACTIVITY_TIMELINE = true;
  public static final String FOLLOWING = "FOLLOWING";
  public static final String ME = "ME";
  @Spy SpyCallback spyCallback = new SpyCallback();
  @Mock Interactor.ErrorCallback errorCallback;
  @Mock ShootrTimelineService shootrTimelineService;
  @Mock SessionRepository sessionRepository;
  @Mock LocaleProvider localeProvider;
  @Mock UserRepository localUserRepository;
  private com.shootr.mobile.domain.interactor.timeline.activity.RefreshActivityTimelineInteractor
      interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    this.interactor =
        new com.shootr.mobile.domain.interactor.timeline.activity.RefreshActivityTimelineInteractor(
            interactorHandler, postExecutionThread, shootrTimelineService);
  }

  @Test public void shouldCallbackActivityTimelineWhenServiceReturnsTimelineForActivity()
      throws Exception {
    when(shootrTimelineService.refreshTimelinesForActivity(anyString(),
        anyBoolean())).thenReturn(timelineForActivity());
    interactor.refreshActivityTimeline(NOT_USER_ACTIVITY_TIMELINE, FOLLOWING, spyCallback,
        errorCallback);

    verify(spyCallback).onLoaded(timelineForActivity());
  }

  @Test public void shouldNotRefreshTimelinesForStreamWhenLocalRepositoryReturnsNullUser()
      throws Exception {
    when(shootrTimelineService.refreshTimelinesForActivity(anyString(),
        anyBoolean())).thenReturn(timelineForActivity());
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    when(localUserRepository.getUserById(ID_USER)).thenReturn(null);

    interactor.refreshActivityTimeline(NOT_USER_ACTIVITY_TIMELINE, FOLLOWING, spyCallback,
        errorCallback);

    verify(shootrTimelineService, never()).refreshTimelinesForStream(anyString(), anyBoolean());
  }

  @Test
  public void shouldCallbackActivityTimelineWhenServiceReturnsTimelineForActivityAndIsUserTimeline()
      throws Exception {
    when(shootrTimelineService.refreshTimelinesForActivity(anyString(),
        anyBoolean())).thenReturn(timelineForActivity());

    interactor.refreshActivityTimeline(IS_USER_ACTIVITY_TIMELINE, ME, spyCallback,
        errorCallback);

    verify(spyCallback).onLoaded(timelineForActivity());
  }

  @Test
  public void shouldNotRefreshTimelinesForStreamWhenLocalRepositoryReturnsUserWithoutWatchingStreamAndIsUserTimeline()
      throws Exception {
    when(shootrTimelineService.refreshTimelinesForActivity(anyString(),
        anyBoolean())).thenReturn(timelineForActivity());
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    when(localUserRepository.getUserById(ID_USER)).thenReturn(userWithoutWatchingStream());

    interactor.refreshActivityTimeline(IS_USER_ACTIVITY_TIMELINE, ME, spyCallback,
        errorCallback);

    verify(shootrTimelineService, never()).refreshTimelinesForStream(anyString(), anyBoolean());
  }

  @Test
  public void shouldNotRefreshTimelinesForStreamWhenLocalRepositoryReturnsNullUserAndIsUserTimeline()
      throws Exception {
    when(shootrTimelineService.refreshTimelinesForActivity(anyString(),
        anyBoolean())).thenReturn(timelineForActivity());
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    when(localUserRepository.getUserById(ID_USER)).thenReturn(null);

    interactor.refreshActivityTimeline(IS_USER_ACTIVITY_TIMELINE, ME, spyCallback,
        errorCallback);

    verify(shootrTimelineService, never()).refreshTimelinesForStream(anyString(), anyBoolean());
  }

  @Test public void shouldNotifyErrorWhenShootrTimelineServiceThrowsShootrException()
      throws Exception {
    when(shootrTimelineService.refreshTimelinesForActivity(anyString(),
        anyBoolean())).thenThrow(new ShootrException() {
    });

    interactor.refreshActivityTimeline(IS_USER_ACTIVITY_TIMELINE, ME, spyCallback,
        errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  private User user() {
    User user = new User();
    user.setIdWatchingStream(ID_WATCHING_STREAM);
    return user;
  }

  private User userWithoutWatchingStream() {
    User user = new User();
    return user;
  }

  private ActivityTimeline timelineForActivity() {
    ActivityTimeline timeline = new ActivityTimeline();
    timeline.setActivities(new ArrayList<Activity>());
    return timeline;
  }

  static class SpyCallback implements Interactor.Callback<ActivityTimeline> {

    public List<ActivityTimeline> timelinesReturned = new ArrayList<>();

    @Override public void onLoaded(ActivityTimeline timeline) {
      timelinesReturned.add(timeline);
    }
  }
}