package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.TimelineSynchronizationRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.Arrays;
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

public class GetViewOnlyStreamTimelineInteractorTest {

  private static final String ID_CURRENT_USER = "current_user";
  public static final String ID_STREAM = "ID_STREAM";
  public static final String ID_USER = "ID_USER";
  public static final String ANTOHER_ID_USER = "antoher_id_user";
  String[] TYPES_STREAM = StreamMode.TYPES_STREAM;

  @Mock ShotRepository localShotRepository;
  @Mock UserRepository localUserRepository;
  @Mock SessionRepository sessionRepository;
  @Mock TimelineSynchronizationRepository timelineSynchronizationRepository;
  @Mock ContributorRepository localContributorRepository;
  @Mock Interactor.Callback<Timeline> callback;
  @Mock StreamRepository streamRepository;
  private GetViewOnlyStreamTimelineInteractor getViewOnlyStreamTimelineInteractor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    when(localUserRepository.getPeople()).thenReturn(people());
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_CURRENT_USER);
    when(localContributorRepository.getContributors(ID_STREAM)).thenReturn(contributors());
    when(streamRepository.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(stream());

    getViewOnlyStreamTimelineInteractor =
        new GetViewOnlyStreamTimelineInteractor(interactorHandler, postExecutionThread,
            localShotRepository, localUserRepository, localContributorRepository, streamRepository,
            sessionRepository);
  }

  @Test public void shouldLoadShotsFromRemoteRepository() throws Exception {
    getViewOnlyStreamTimelineInteractor.loadStreamTimeline(ID_STREAM, true, callback);

    verify(localShotRepository).getShotsForStreamTimeline(any(StreamTimelineParameters.class));
  }

  @Test public void shouldGetPeopleFromLocalRepository() throws Exception {
    when(localShotRepository.getShotsForStreamTimeline(parameters())).thenReturn(shots());

    getViewOnlyStreamTimelineInteractor.loadStreamTimeline(ID_STREAM, true, callback);

    verify(localUserRepository).getPeople();
  }

  @Test public void shouldGetContributorsFromLocalRepository() throws Exception {
    when(localShotRepository.getShotsForStreamTimeline(parameters())).thenReturn(shots());

    getViewOnlyStreamTimelineInteractor.loadStreamTimeline(ID_STREAM, true, callback);

    verify(localContributorRepository).getContributors(anyString());
  }

  @Test public void shouldReturnShotsFromVisibleUsersOnly() throws Exception {
    List<Shot> shots = threeShots();
    when(localShotRepository.getShotsForStreamTimeline(
        any(StreamTimelineParameters.class))).thenReturn(shots);

    getViewOnlyStreamTimelineInteractor.loadStreamTimeline(ID_STREAM, true, callback);

    Timeline timeline = new Timeline();
    timeline.setShots(Arrays.asList(shots.get(2), shots.get(0)));
    verify(callback).onLoaded(timeline);
  }

  private List<Shot> threeShots() {
    Shot visibleShot = new Shot();
    visibleShot.setPublishDate(new Date(0L));
    Shot.ShotUserInfo visibleUserInfo = new Shot.ShotUserInfo();
    visibleUserInfo.setIdUser(ID_USER);
    visibleShot.setUserInfo(visibleUserInfo);

    Shot shot = new Shot();
    shot.setPublishDate(new Date(1L));
    Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();
    userInfo.setIdUser(ANTOHER_ID_USER);
    shot.setUserInfo(userInfo);

    Shot currentUserShot = new Shot();
    currentUserShot.setPublishDate(new Date(2L));
    Shot.ShotUserInfo currentUserInfo = new Shot.ShotUserInfo();
    currentUserInfo.setIdUser(ID_CURRENT_USER);
    currentUserShot.setUserInfo(currentUserInfo);

    return Arrays.asList(visibleShot, shot, currentUserShot);
  }

  private List<Contributor> contributors() {
    Contributor contributor = new Contributor();
    contributor.setIdUser(ID_USER);
    return Collections.singletonList(contributor);
  }

  private List<Shot> shots() {
    return Collections.singletonList(shotWithDate(0L));
  }

  private Shot shotWithDate(Long date) {
    Shot shot = new Shot();
    shot.setPublishDate(new Date(date));
    return shot;
  }

  private StreamTimelineParameters parameters() {
    return StreamTimelineParameters.builder().forStream(ID_STREAM).realTime(true).build();
  }

  private List<User> people() {
    User user = new User();
    user.setIdUser(ID_USER);
    return Arrays.asList(user);
  }

  private Stream stream() {
    Stream stream = new Stream();
    stream.setId(ID_STREAM);
    stream.setAuthorId(ID_USER);
    return stream;
  }
}
