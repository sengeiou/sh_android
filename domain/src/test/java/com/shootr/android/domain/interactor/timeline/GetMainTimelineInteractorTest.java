package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static com.shootr.android.domain.asserts.TimelineParametersAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
    @Mock UserRepository localUserRepository;
    @Mock SessionRepository sessionRepository;

    private GetMainTimelineInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        when(localUserRepository.getPeople()).thenReturn(people());

        interactor = new GetMainTimelineInteractor(interactorHandler,
          postExecutionThread, sessionRepository, localShotRepository,
          remoteShotRepository,
          remoteWatchRepository,
          eventRepository, localUserRepository);
    }

    @Test
    public void shouldRetrieveTimelineWithoutEventIdAndAuthorWhenNoEventVisible() throws Exception {
        when(remoteWatchRepository.getCurrentVisibleWatch()).thenReturn(NO_EVENT_WATCH);

        interactor.loadMainTimeline(spyCallback);

        TimelineParameters localParameters = captureTimelineParametersFromRepositoryCall(localShotRepository);
        assertThat(localParameters).hasNoEventId().hasNoEventAuthorId();

        TimelineParameters remoteParameters = captureTimelineParametersFromRepositoryCall(remoteShotRepository);
        assertThat(remoteParameters).hasNoEventId().hasNoEventAuthorId();
    }

    @Test
    public void shouldRetrieveTimelineWithEventIdAndAuthorWhenEventVisible() throws Exception {
        setupVisibleEvent();

        interactor.loadMainTimeline(spyCallback);

        TimelineParameters localParameters = captureTimelineParametersFromRepositoryCall(localShotRepository);
        assertThat(localParameters).hasEventId(VISIBLE_EVENT_ID).hasEventAuthorId(EVENT_AUTHOR_ID);
        TimelineParameters remoteParameters = captureTimelineParametersFromRepositoryCall(localShotRepository);
        assertThat(remoteParameters).hasEventId(VISIBLE_EVENT_ID).hasEventAuthorId(EVENT_AUTHOR_ID);
    }

    private void setupVisibleEvent() {
        when(remoteWatchRepository.getCurrentVisibleWatch()).thenReturn(eventVisibleWatch());
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
    private TimelineParameters captureTimelineParametersFromRepositoryCall(ShotRepository shotRepository) {
        ArgumentCaptor<TimelineParameters> captor =
          ArgumentCaptor.forClass(TimelineParameters.class);
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