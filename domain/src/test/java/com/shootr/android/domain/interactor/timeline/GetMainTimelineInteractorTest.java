package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static com.shootr.android.domain.asserts.TimelineAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetMainTimelineInteractorTest {

    public static final Watch NO_EVENT_WATCH = null;
    private static final Long ID_SHOT_WITHOUT_EVENT = 1L;
    private static final Long VISIBLE_EVENT_ID = 2L;
    private static final Long ID_SHOT_WITH_EVENT = 3L;
    private static final Long EVENT_AUTHOR_ID = 4L;
    private static final Long ID_SHOT_FROM_AUTHOR = 5L;

    @Mock ShotRepository localShotRepository;
    @Mock ShotRepository remoteShotRepository;
    @Mock WatchRepository remoteWatchRepository;
    @Spy SpyCallback spyCallback = new SpyCallback();
    @Mock EventRepository eventRepository;
    @Mock UserRepository remoteUserRepository;

    private GetMainTimelineInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        interactor = new GetMainTimelineInteractor(interactorHandler,
          postExecutionThread,
          localShotRepository,
          remoteShotRepository,
          remoteWatchRepository,
          eventRepository,
          remoteUserRepository);
    }

    @Test
    public void shouldRetrieveShotsWithoutEventWhenNoEventVisible() throws Exception {
        when(remoteWatchRepository.getCurrentVisibleWatch()).thenReturn(NO_EVENT_WATCH);

        interactor.loadMainTimeline(spyCallback);

        verify(localShotRepository, times(1)).getShotsWithoutEventFromUsers(anyListOf(Long.class));
        verify(remoteShotRepository, times(1)).getShotsWithoutEventFromUsers(anyListOf(Long.class));
    }

    @Test
    public void shouldCallbackShotsWithoutEventsWhenNoEventVisible() throws Exception {
        when(remoteWatchRepository.getCurrentVisibleWatch()).thenReturn(NO_EVENT_WATCH);
        when(localShotRepository.getShotsWithoutEventFromUsers(anyListOf(Long.class))).thenReturn(shotsWithoutEvent());
        when(remoteShotRepository.getShotsWithoutEventFromUsers(anyListOf(Long.class))).thenReturn(shotsWithoutEvent());

        interactor.loadMainTimeline(spyCallback);

        verify(spyCallback, times(2)).onLoaded(any(Timeline.class));
        Timeline localTimeline = spyCallback.timelinesReturned.get(0);
        assertThat(localTimeline).hasShots(shotsWithoutEvent());
        Timeline remoteTimeline = spyCallback.timelinesReturned.get(1);
        assertThat(remoteTimeline).hasShots(shotsWithoutEvent());
    }

    @Test
    public void shouldRetrieveShotsForEventIncludingAuthorShotsWhenEventVisible() throws Exception {
        setupVisibleEvent();

        interactor.loadMainTimeline(spyCallback);

        verify(localShotRepository).getShotsForEventAndUsersWithAuthor(eq(VISIBLE_EVENT_ID),
          eq(EVENT_AUTHOR_ID),
          anyListOf(Long.class));
        verify(remoteShotRepository).getShotsForEventAndUsersWithAuthor(eq(VISIBLE_EVENT_ID),
          eq(EVENT_AUTHOR_ID),
          anyListOf(Long.class));
    }

    @Test
    public void shouldCallbackShotsWithVisibleEventWhenEventVisible() throws Exception {
        setupVisibleEvent();

        interactor.loadMainTimeline(spyCallback);

        Timeline localTimeline = spyCallback.timelinesReturned.get(0);
        Timeline remoteTimeline = spyCallback.timelinesReturned.get(1);
        assertThat(localTimeline).hasShots(shotsWithVisibleEvent());
        assertThat(remoteTimeline).hasShots(shotsWithVisibleEvent());
    }

    @Test
    public void shouldCallbackShotsFromEventAuthorWhenEventVisible() throws Exception {
        setupVisibleEvent();

        interactor.loadMainTimeline(spyCallback);

        Timeline localTimeline = spyCallback.timelinesReturned.get(0);
        assertThat(localTimeline).hasShots(shotsFromAuthor());
        Timeline remoteTimeline = spyCallback.timelinesReturned.get(1);
        assertThat(remoteTimeline).hasShots(shotsFromAuthor());
    }

    private void setupVisibleEvent() {
        when(remoteWatchRepository.getCurrentVisibleWatch()).thenReturn(eventVisibleWatch());
        when(eventRepository.getEventById(eq(VISIBLE_EVENT_ID))).thenReturn(visibleEvent());
        when(localShotRepository.getShotsForEventAndUsersWithAuthor(eq(VISIBLE_EVENT_ID),
          eq(EVENT_AUTHOR_ID),
          anyListOf(Long.class))).thenReturn(shotsWithVisibleEventAndAuthor());
        when(remoteShotRepository.getShotsForEventAndUsersWithAuthor(eq(VISIBLE_EVENT_ID),
          eq(EVENT_AUTHOR_ID),
          anyListOf(Long.class))).thenReturn(shotsWithVisibleEventAndAuthor());
    }

    private List<Shot> shotsWithVisibleEventAndAuthor() {
        List<Shot> shots = shotsWithVisibleEvent();
        shots.addAll(shotsFromAuthor());
        return shots;
    }

    //region Stubs
    private List<Shot> shotsFromAuthor() {
        return Arrays.asList(shotFromAuthor());
    }

    private List<Long> peopleIds() {
        return new ArrayList<>();
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

    private Watch eventVisibleWatch() {
        Watch visibleWatch = new Watch();
        visibleWatch.setIdEvent(VISIBLE_EVENT_ID);
        return visibleWatch;
    }

    private List<Shot> shotsWithoutEvent() {
        List<Shot> shots = new ArrayList<>();
        shots.add(shotWithoutEvent());
        shots.add(shotWithoutEvent());
        shots.add(shotWithoutEvent());
        return shots;
    }

    private Shot shotWithoutEvent() {
        Shot shot = new Shot();
        shot.setIdShot(ID_SHOT_WITHOUT_EVENT);
        shot.setEventInfo(null);
        return shot;
    }
    //endregion

    //region Spies

    static class SpyCallback implements GetMainTimelineInteractor.Callback {

        public List<Timeline> timelinesReturned = new ArrayList<>();

        @Override public void onLoaded(Timeline timeline) {
            timelinesReturned.add(timeline);
        }
    }
    //endregion
}