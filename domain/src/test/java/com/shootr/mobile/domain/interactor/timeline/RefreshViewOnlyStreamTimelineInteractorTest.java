package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Contributor;
import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RefreshViewOnlyStreamTimelineInteractorTest {

  private static final String ID_STREAM = "idStream";
  private static final Boolean NOT_PAUSED = false;
  private static final Boolean PAUSED = true;
  private static final String CONTRIBUTOR_ID = "contributorId";
  private static final String USER_ID = "idUser";
  private static final String HOLDER_ID = "holderId";
  private static final String FOLLOWING_ID = "followingId";
  private static final String OTHER_USER_ID = "otherUserId";
  private static final String ID_SHOT = "idShot";
  private static final long LAST_REFRESH_DATE = new Date().getTime();
  @Mock Interactor.Callback<Timeline> callback;
  @Mock Interactor.ErrorCallback errorCallback;
  @Mock ShootrTimelineService shootrTimelineService;
  @Mock LocaleProvider localeProvider;
  @Mock ContributorRepository contributorRepository;
  @Mock SessionRepository sessionRepository;
  @Mock UserRepository userRepository;
  @Mock StreamRepository streamRepository;
  private RefreshViewOnlyStreamTimelineInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    this.interactor =
        new RefreshViewOnlyStreamTimelineInteractor(interactorHandler, postExecutionThread,
            shootrTimelineService, localeProvider, userRepository, contributorRepository,
            sessionRepository, streamRepository);
  }

  @Test public void shouldRefreshStreamTimelineForStreamWhenIsContributorShot() throws Exception {
    when(shootrTimelineService.refreshTimelinesForStream(ID_STREAM, NOT_PAUSED)).thenReturn(
        timelineWithContributorShot());
    when(contributorRepository.getContributors(anyString())).thenReturn(contributors());
    setupSessionAndStreamRepository();

    interactor.refreshStreamTimeline(ID_STREAM, LAST_REFRESH_DATE, PAUSED, callback, errorCallback);

    verify(callback).onLoaded(timelineWithContributorShot());
  }

  @Test public void shouldRefreshStreamTimelineForStreamWhenIsHolderShot() throws Exception {
    when(shootrTimelineService.refreshTimelinesForStream(ID_STREAM, NOT_PAUSED)).thenReturn(
        timelineWithHolderShot());
    when(contributorRepository.getContributors(anyString())).thenReturn(contributors());
    when(userRepository.isFollowing(anyString())).thenReturn(true);
    setupSessionAndStreamRepository();

    interactor.refreshStreamTimeline(ID_STREAM, LAST_REFRESH_DATE, PAUSED, callback, errorCallback);

    verify(callback).onLoaded(timelineWithHolderShot());
  }

  @Test public void shouldRefreshStreamTimelineForStreamWhenIsFollowingShot() throws Exception {
    when(shootrTimelineService.refreshTimelinesForStream(ID_STREAM, NOT_PAUSED)).thenReturn(
        timelineWithFollowingShot());
    when(contributorRepository.getContributors(anyString())).thenReturn(contributors());
    when(userRepository.isFollowing(anyString())).thenReturn(true);
    setupSessionAndStreamRepository();

    interactor.refreshStreamTimeline(ID_STREAM, LAST_REFRESH_DATE, PAUSED, callback, errorCallback);

    verify(callback).onLoaded(timelineWithFollowingShot());
  }

  @Test public void shouldRefreshStreamTimelineForStreamWhenIsMyShot() throws Exception {
    when(shootrTimelineService.refreshTimelinesForStream(ID_STREAM, NOT_PAUSED)).thenReturn(
        timelineWithMyShot());
    when(contributorRepository.getContributors(anyString())).thenReturn(contributors());
    when(userRepository.isFollowing(anyString())).thenReturn(true);
    setupSessionAndStreamRepository();

    interactor.refreshStreamTimeline(ID_STREAM, LAST_REFRESH_DATE, PAUSED, callback, errorCallback);

    verify(callback).onLoaded(timelineWithMyShot());
  }

  @Test public void shouldRefreshStreamTimelineOnlyWithViewOnlyShots() throws Exception {
    when(shootrTimelineService.refreshTimelinesForStream(ID_STREAM, NOT_PAUSED)).thenReturn(
        timelineWithShots());
    when(contributorRepository.getContributors(anyString())).thenReturn(contributors());
    when(userRepository.isFollowing(anyString())).thenReturn(false);
    setupSessionAndStreamRepository();

    interactor.refreshStreamTimeline(ID_STREAM, LAST_REFRESH_DATE, PAUSED, callback, errorCallback);

    verify(callback).onLoaded(timelineWithViewOnlyShots());
  }

  private void setupSessionAndStreamRepository() {
    when(sessionRepository.getCurrentUser()).thenReturn(user());
    when(streamRepository.getStreamById(anyString())).thenReturn(stream());
  }

  private Timeline timelineWithContributorShot() {
    Timeline timeline = new Timeline();

    Shot.ShotUserInfo shotUserInfo = new Shot.ShotUserInfo();
    shotUserInfo.setIdUser(CONTRIBUTOR_ID);
    Shot shot = new Shot();
    shot.setIdShot(ID_SHOT);
    shot.setUserInfo(shotUserInfo);

    timeline.setShots(Collections.singletonList(shot));
    return timeline;
  }

  private Timeline timelineWithHolderShot() {
    Timeline timeline = new Timeline();

    Shot.ShotUserInfo shotUserInfo = new Shot.ShotUserInfo();
    shotUserInfo.setIdUser(HOLDER_ID);
    Shot shot = new Shot();
    shot.setIdShot(ID_SHOT);
    shot.setUserInfo(shotUserInfo);
    timeline.setShots(Collections.singletonList(shot));
    return timeline;
  }

  private Timeline timelineWithFollowingShot() {
    Timeline timeline = new Timeline();

    Shot.ShotUserInfo shotUserInfo = new Shot.ShotUserInfo();
    shotUserInfo.setIdUser(FOLLOWING_ID);
    Shot shot = new Shot();
    shot.setIdShot(ID_SHOT);
    shot.setUserInfo(shotUserInfo);
    timeline.setShots(Collections.singletonList(shot));
    return timeline;
  }

  private Timeline timelineWithMyShot() {
    Timeline timeline = new Timeline();

    Shot.ShotUserInfo shotUserInfo = new Shot.ShotUserInfo();
    shotUserInfo.setIdUser(USER_ID);
    Shot shot = new Shot();
    shot.setIdShot(ID_SHOT);
    shot.setUserInfo(shotUserInfo);
    timeline.setShots(Collections.singletonList(shot));
    return timeline;
  }

  private Timeline timelineWithShots() {
    Timeline timeline = new Timeline();
    ArrayList<Shot> shots = new ArrayList<>();
    Shot.ShotUserInfo shotUserInfo = new Shot.ShotUserInfo();
    shotUserInfo.setIdUser(USER_ID);

    Shot userShot = new Shot();
    userShot.setUserInfo(shotUserInfo);
    shots.add(userShot);

    Shot contributorShot = new Shot();
    shotUserInfo = new Shot.ShotUserInfo();
    shotUserInfo.setIdUser(CONTRIBUTOR_ID);
    contributorShot.setUserInfo(shotUserInfo);
    shots.add(contributorShot);

    Shot holderShot = new Shot();
    shotUserInfo = new Shot.ShotUserInfo();
    holderShot.setIdShot(ID_SHOT);
    shotUserInfo.setIdUser(HOLDER_ID);
    holderShot.setUserInfo(shotUserInfo);
    shots.add(holderShot);

    Shot otherShot = new Shot();
    shotUserInfo = new Shot.ShotUserInfo();
    otherShot.setIdShot(ID_SHOT);
    shotUserInfo.setIdUser(OTHER_USER_ID);
    otherShot.setUserInfo(shotUserInfo);
    shots.add(otherShot);

    timeline.setShots(shots);
    return timeline;
  }

  private Timeline timelineWithViewOnlyShots() {
    Timeline timeline = new Timeline();
    ArrayList<Shot> shots = new ArrayList<>();
    Shot.ShotUserInfo shotUserInfo = new Shot.ShotUserInfo();
    shotUserInfo.setIdUser(USER_ID);

    Shot userShot = new Shot();
    userShot.setUserInfo(shotUserInfo);
    shots.add(userShot);

    Shot contributorShot = new Shot();
    shotUserInfo = new Shot.ShotUserInfo();
    shotUserInfo.setIdUser(CONTRIBUTOR_ID);
    contributorShot.setUserInfo(shotUserInfo);
    shots.add(contributorShot);

    Shot holderShot = new Shot();
    shotUserInfo = new Shot.ShotUserInfo();
    holderShot.setIdShot(ID_SHOT);
    shotUserInfo.setIdUser(HOLDER_ID);
    holderShot.setUserInfo(shotUserInfo);
    shots.add(holderShot);

    timeline.setShots(shots);
    return timeline;
  }

  private List<Contributor> contributors() {
    Contributor contributor = new Contributor();
    contributor.setIdUser(CONTRIBUTOR_ID);
    return Collections.singletonList(contributor);
  }

  private User user() {
    User user = new User();
    user.setIdUser(USER_ID);
    return user;
  }

  private Stream stream() {
    Stream stream = new Stream();
    stream.setAuthorId(HOLDER_ID);
    stream.setId("streamId");
    return stream;
  }
}
