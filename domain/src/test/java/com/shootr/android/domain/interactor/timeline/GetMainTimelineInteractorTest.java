package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.RepositoryException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.SynchronizationRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static com.shootr.android.domain.asserts.TimelineParametersAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetMainTimelineInteractorTest {

    private static final Long ID_SHOT_WITHOUT_EVENT = 1L;
    private static final Long VISIBLE_EVENT_ID = 2L;
    private static final Long ID_SHOT_WITH_EVENT = 3L;
    private static final Long EVENT_AUTHOR_ID = 4L;
    private static final Long ID_SHOT_FROM_AUTHOR = 5L;
    private static final Long ID_CURRENT_USER = 6L;

    private static final Long DATE_OLDER = 1000L;
    private static final Long DATE_MIDDLE = 2000L;
    private static final Long DATE_NEWER = 3000L;

    @Mock ShotRepository localShotRepository;
    @Mock ShotRepository remoteShotRepository;
    @Mock UserRepository localUserRepository;
    @Mock UserRepository remoteUserRepository;
    @Spy SpyCallback spyCallback = new SpyCallback();
    @Mock EventRepository eventRepository;
    @Mock SessionRepository sessionRepository;
    @Mock SynchronizationRepository synchronizationRepository;
    @Mock Interactor.ErrorCallback errorCallback;

    private GetMainTimelineInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        when(localUserRepository.getPeople()).thenReturn(people());
        when(remoteUserRepository.getPeople()).thenReturn(people());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_CURRENT_USER);

        interactor = new GetMainTimelineInteractor(interactorHandler,
          postExecutionThread,
          sessionRepository,
          localShotRepository,
          remoteShotRepository,
          remoteUserRepository,
          eventRepository,
          localUserRepository,
          synchronizationRepository);
    }

    @Test
    public void shouldRetrieveTimelineWithoutEventIdAndAuthorWhenNoEventVisible() throws Exception {
        when(localUserRepository.getUserById(ID_CURRENT_USER)).thenReturn(currentUserNotWatching());
        when(remoteUserRepository.getUserById(ID_CURRENT_USER)).thenReturn(currentUserNotWatching());

        interactor.loadMainTimeline(spyCallback, errorCallback);

        TimelineParameters localParameters = captureTimelineParametersFromRepositoryCall(localShotRepository);
        assertThat(localParameters).hasNoEventId().hasNoEventAuthorId();

        TimelineParameters remoteParameters = captureTimelineParametersFromRepositoryCall(remoteShotRepository);
        assertThat(remoteParameters).hasNoEventId().hasNoEventAuthorId();
    }

    private User currentUserNotWatching() {
        User user = new User();
        user.setIdUser(ID_CURRENT_USER);
        user.setVisibleEventId(null);
        return user;
    }

    private User currentUserWatching() {
        User user = new User();
        user.setIdUser(ID_CURRENT_USER);
        user.setVisibleEventId(VISIBLE_EVENT_ID);
        return user;
    }

    @Test
    public void shouldRetrieveTimelineWithEventIdAndAuthorWhenEventVisible() throws Exception {
        setupVisibleEvent();

        interactor.loadMainTimeline(spyCallback, errorCallback);

        TimelineParameters localParameters = captureTimelineParametersFromRepositoryCall(localShotRepository);
        assertThat(localParameters).hasEventId(VISIBLE_EVENT_ID).hasEventAuthorId(EVENT_AUTHOR_ID);
        TimelineParameters remoteParameters = captureTimelineParametersFromRepositoryCall(localShotRepository);
        assertThat(remoteParameters).hasEventId(VISIBLE_EVENT_ID).hasEventAuthorId(EVENT_AUTHOR_ID);
    }

    @Test
    public void shouldCallbackShotsInOrderWithPublishDateComparator() throws Exception {
        setupVisibleEvent();
        when(localShotRepository.getShotsForTimeline(any(TimelineParameters.class))).thenReturn(unorderedShots());
        when(remoteShotRepository.getShotsForTimeline(any(TimelineParameters.class))).thenReturn(unorderedShots());

        interactor.loadMainTimeline(spyCallback, errorCallback);
        List<Shot> localShotsReturned = spyCallback.timelinesReturned.get(0).getShots();
        List<Shot> remoteShotsReturned = spyCallback.timelinesReturned.get(1).getShots();

        assertThat(localShotsReturned).isSortedAccordingTo(new Shot.PublishDateComparator());
        assertThat(remoteShotsReturned).isSortedAccordingTo(new Shot.PublishDateComparator());
    }

    @Test
    public void shouldUpdateLastRefreshDateWithNewestShotPublishDateFromRemoteRepo() throws Exception {
        setupVisibleEvent();
        when(remoteShotRepository.getShotsForTimeline(any(TimelineParameters.class))).thenReturn(unorderedShots());

        interactor.loadMainTimeline(spyCallback, errorCallback);

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(synchronizationRepository).putTimelineLastRefresh(captor.capture());
        Long updatedRefreshDate = captor.getValue();

        assertThat(updatedRefreshDate).isEqualTo(DATE_NEWER);
    }

    @Test
    public void shouldCallbackShotsOnceIfRemoteUserRepositoryFails() throws Exception {
        setupVisibleEvent();
        when(remoteUserRepository.getUserById(anyLong())).thenThrow(new RepositoryException("Test exception"));
        when(localShotRepository.getShotsForTimeline(any(TimelineParameters.class))).thenReturn(unorderedShots());
        when(remoteShotRepository.getShotsForTimeline(any(TimelineParameters.class))).thenReturn(unorderedShots());

        interactor.loadMainTimeline(spyCallback, errorCallback);

        verify(spyCallback, times(1)).onLoaded(any(Timeline.class));
    }

    private List<Shot> unorderedShots() {
        return Arrays.asList(shotWithDate(DATE_MIDDLE), shotWithDate(DATE_OLDER), shotWithDate(DATE_NEWER));
    }

    private Shot shotWithDate(Long date) {
        Shot shot = new Shot();
        shot.setPublishDate(new Date(date));
        return shot;
    }

    private void setupVisibleEvent() {
        when(remoteUserRepository.getUserById(ID_CURRENT_USER)).thenReturn(currentUserWatching());
        when(localUserRepository.getUserById(ID_CURRENT_USER)).thenReturn(currentUserWatching());
        when(eventRepository.getEventById(eq(VISIBLE_EVENT_ID))).thenReturn(visibleEvent());
    }

    //region Stubs
    private List<Shot> shotsWithVisibleEventAndAuthor() {
        List<Shot> shots = shotsWithVisibleEvent();
        shots.addAll(shotsFromAuthor());
        return shots;
    }

    private List<Shot> shotsFromAuthor() {
        return Arrays.asList(shotFromAuthor());
    }

    private List<User> people() {
        return Arrays.asList(new User());
    }

    private Shot shotFromAuthor() {
        Shot shot = new Shot();
        shot.setIdShot(ID_SHOT_FROM_AUTHOR);
        return shot;
    }

    private List<Shot> shotsWithVisibleEvent() {
        List<Shot> shots = new ArrayList<>();
        shots.add(shotWithEvent());
        shots.add(shotWithEvent());
        shots.add(shotWithEvent());
        return shots;
    }

    private Shot shotWithEvent() {
        Shot shot = new Shot();
        shot.setIdShot(ID_SHOT_WITH_EVENT);
        shot.setEventInfo(visibleEventInfo());
        return shot;
    }

    private Shot.ShotEventInfo visibleEventInfo() {
        Shot.ShotEventInfo eventInfo = new Shot.ShotEventInfo();
        eventInfo.setIdEvent(VISIBLE_EVENT_ID);
        return eventInfo;
    }

    private Event visibleEvent() {
        Event event = new Event();
        event.setId(VISIBLE_EVENT_ID);
        event.setAuthorId(EVENT_AUTHOR_ID);
        return event;
    }

    //endregion

    //region Spies
    private TimelineParameters captureTimelineParametersFromRepositoryCall(ShotRepository shotRepository) {
        ArgumentCaptor<TimelineParameters> captor = ArgumentCaptor.forClass(TimelineParameters.class);
        verify(shotRepository).getShotsForTimeline(captor.capture());
        return captor.getValue();
    }

    static class SpyCallback implements GetMainTimelineInteractor.Callback {

        public List<Timeline> timelinesReturned = new ArrayList<>();

        @Override public void onLoaded(Timeline timeline) {
            timelinesReturned.add(timeline);
        }
    }
    //endregion
}