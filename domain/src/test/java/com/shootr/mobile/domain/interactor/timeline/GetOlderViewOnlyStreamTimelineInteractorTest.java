package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetOlderViewOnlyStreamTimelineInteractorTest {

  private static final String CONTRIBUTOR_ID = "contributorId";
  private static final String USER_ID = "idUser";
  private static final String HOLDER_ID = "holderId";
  private static final String FOLLOWING_ID = "followingId";
  private static final String ID_SHOT = "idShot";
  private static final String WATCHING_STREAM = "watchingStream";
  private static final long CURRENT_OLDEST_DATE = new Date().getTime();

  @Mock Interactor.Callback<Timeline> callback;
  @Mock Interactor.ErrorCallback errorCallback;
  @Mock ShootrTimelineService shootrTimelineService;
  @Mock LocaleProvider localeProvider;
  @Mock ContributorRepository contributorRepository;
  @Mock SessionRepository sessionRepository;
  @Mock UserRepository userRepository;
  @Mock StreamRepository streamRepository;
  @Mock ExternalShotRepository shotRepository;
  private GetOlderViewOnlyStreamTimelineInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    this.interactor =
        new GetOlderViewOnlyStreamTimelineInteractor(interactorHandler, postExecutionThread,
            sessionRepository, shotRepository, streamRepository, contributorRepository,
            userRepository);
  }

  @Test public void shouldLoadOlderStreamTimelineTimelineForStreamWhenIsContributorShot()
      throws Exception {
    when(shotRepository.getShotsForStreamTimeline(any(StreamTimelineParameters.class))).thenReturn(
        contributorShots());
    when(contributorRepository.getContributors(anyString())).thenReturn(contributors());
    setupSessionAndStreamRepositories();

    interactor.loadOlderStreamTimeline(CURRENT_OLDEST_DATE, callback, errorCallback);

    verify(callback).onLoaded(timelineWithContributorShot());
  }

  @Test public void shouldLoadOlderStreamTimelineForStreamWhenIsHolderShot() throws Exception {
    when(shotRepository.getShotsForStreamTimeline(any(StreamTimelineParameters.class))).thenReturn(
        holderShots());
    when(contributorRepository.getContributors(anyString())).thenReturn(contributors());
    when(userRepository.isFollowing(anyString())).thenReturn(true);
    setupSessionAndStreamRepositories();

    interactor.loadOlderStreamTimeline(CURRENT_OLDEST_DATE, callback, errorCallback);

    verify(callback).onLoaded(timelineWithHolderShot());
  }

  @Test public void shouldLoadOlderStreamTimelineForStreamWhenIsFollowingShot() throws Exception {
    when(shotRepository.getShotsForStreamTimeline(any(StreamTimelineParameters.class))).thenReturn(
        followingShots());
    when(contributorRepository.getContributors(anyString())).thenReturn(contributors());
    when(userRepository.isFollowing(anyString())).thenReturn(true);
    setupSessionAndStreamRepositories();

    interactor.loadOlderStreamTimeline(CURRENT_OLDEST_DATE, callback, errorCallback);

    verify(callback).onLoaded(timelineWithFollowingShot());
  }

  @Test public void shouldLoadOlderStreamTimelineForStreamWhenIsMyShot() throws Exception {
    when(shotRepository.getShotsForStreamTimeline(any(StreamTimelineParameters.class))).thenReturn(
        myShot());
    when(contributorRepository.getContributors(anyString())).thenReturn(contributors());
    when(userRepository.isFollowing(anyString())).thenReturn(true);
    setupSessionAndStreamRepositories();

    interactor.loadOlderStreamTimeline(CURRENT_OLDEST_DATE, callback, errorCallback);

    verify(callback).onLoaded(timelineWithMyShot());
  }

  @Test public void shouldNotifyErrorWhenRemoteShotRepositoryThrowsShootrException()
      throws Exception {
    setupSessionAndStreamRepositories();
    when(shotRepository.getShotsForStreamTimeline(any(StreamTimelineParameters.class))).thenThrow(
        new ShootrException() {
        });

    interactor.loadOlderStreamTimeline(CURRENT_OLDEST_DATE, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  private void setupSessionAndStreamRepositories() {
    when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
    when(userRepository.getUserById(USER_ID)).thenReturn(user());
    when(streamRepository.getStreamById(anyString(), anyArray())).thenReturn(stream());
  }

  private List<Shot> contributorShots() {
    Shot.ShotUserInfo shotUserInfo = new Shot.ShotUserInfo();
    shotUserInfo.setIdUser(CONTRIBUTOR_ID);
    Shot shot = new Shot();
    shot.setIdShot(ID_SHOT);
    shot.setUserInfo(shotUserInfo);

    return Collections.singletonList(shot);
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

  private List<Shot> holderShots() {
    Shot.ShotUserInfo shotUserInfo = new Shot.ShotUserInfo();
    shotUserInfo.setIdUser(HOLDER_ID);
    Shot shot = new Shot();
    shot.setIdShot(ID_SHOT);
    shot.setUserInfo(shotUserInfo);

    return Collections.singletonList(shot);
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

  private List<Shot> followingShots() {
    Shot.ShotUserInfo shotUserInfo = new Shot.ShotUserInfo();
    shotUserInfo.setIdUser(FOLLOWING_ID);
    Shot shot = new Shot();
    shot.setIdShot(ID_SHOT);
    shot.setUserInfo(shotUserInfo);

    return Collections.singletonList(shot);
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

  private List<Shot> myShot() {
    Shot.ShotUserInfo shotUserInfo = new Shot.ShotUserInfo();
    shotUserInfo.setIdUser(USER_ID);
    Shot shot = new Shot();
    shot.setIdShot(ID_SHOT);
    shot.setUserInfo(shotUserInfo);

    return Collections.singletonList(shot);
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

  private List<Contributor> contributors() {
    Contributor contributor = new Contributor();
    contributor.setIdUser(CONTRIBUTOR_ID);
    return Collections.singletonList(contributor);
  }

  private User user() {
    User user = new User();
    user.setIdUser(USER_ID);
    user.setIdWatchingStream(WATCHING_STREAM);
    return user;
  }

  private Stream stream() {
    Stream stream = new Stream();
    stream.setAuthorId(HOLDER_ID);
    stream.setId("streamId");
    return stream;
  }

  private String[] anyArray() {
    return any(String[].class);
  }
}
