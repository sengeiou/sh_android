package com.shootr.android.domain.service.shot;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.TimelineSynchronizationRepository;
import com.shootr.android.domain.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.shootr.android.domain.asserts.TimelineParametersAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ShootrTimelineServiceTest {

    private static final Long DATE_OLDER = 1L;
    private static final Long DATE_MIDDLE = 2L;
    private static final Long DATE_NEWER = 3L;

    private static final String WATCHING_EVENT_ID = "watching_event";
    private static final String WATCHING_EVENT_AUTHOR = "event_author";
    private static final Long WATCHING_EVENT_REFRESH_DATE = 1000L;

    @Mock SessionRepository sessionRepository;
    @Mock EventRepository localEventRepository;
    @Mock UserRepository localUserRepository;
    @Mock ShotRepository remoteShotRepository;
    @Mock TimelineSynchronizationRepository timelineSynchronizationRepository;

    @Captor ArgumentCaptor<List<TimelineParameters>> timelineParametersCaptor;

    private ShootrTimelineService shootrTimelineService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shootrTimelineService = new ShootrTimelineService(sessionRepository, localEventRepository, localUserRepository, remoteShotRepository,
          timelineSynchronizationRepository);
    }

    @Test
    public void shouldReturnFirstTimelineWithEventIdAndAuthorWhenEventWatching() throws Exception {
        setupWatchingEvent();

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();
        TimelineParameters timelineParameters = timelinesResult.get(0).getParameters();

        assertThat(timelineParameters).hasEventId(WATCHING_EVENT_ID).hasEventAuthorId(WATCHING_EVENT_AUTHOR);
    }

    @Test
    public void shouldReturnFirstTimelinehWithEventRefreshDateWhenEventWatching() throws Exception {
        setupWatchingEvent();
        when(timelineSynchronizationRepository.getEventTimelineRefreshDate(WATCHING_EVENT_ID)).thenReturn(
          WATCHING_EVENT_REFRESH_DATE);

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();
        TimelineParameters timelineParameters = timelinesResult.get(0).getParameters();

        assertThat(timelineParameters).hasSinceDate(WATCHING_EVENT_REFRESH_DATE);
    }

    @Test
    public void shoudReturnTwoTimelinesWhenEventWatching() throws Exception {
        setupWatchingEvent();

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();

        assertThat(timelinesResult).hasSize(2);
    }

    @Test
    public void shoudReturnOnlyOneTimelineWhenNoEventWatching() throws Exception {
        setupNoWatchingEvent();

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();

        assertThat(timelinesResult).hasSize(1);
    }

    @Test
    public void shouldReturnSecondTimelineWithoutEventIdAndAuthorWhenEventWatching() throws Exception {
        setupWatchingEvent();

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();
        TimelineParameters timelineParameters = timelinesResult.get(1).getParameters();

        assertThat(timelineParameters).hasNoEventId().hasNoEventAuthorId();
    }

    @Test
    public void shouldReturnFirstTimelineWithoutEventIdAndAuthorWhenNoEventWatching() throws Exception {
        setupNoWatchingEvent();

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();
        TimelineParameters timelineParameters = timelinesResult.get(0).getParameters();

        assertThat(timelineParameters).hasNoEventId().hasNoEventAuthorId();
    }

    @Test
    public void shouldReturnTwoTimelinesShotsInOrderWithPublishDateComparatorWhenEventWatching() throws Exception {
        setupWatchingEvent();
        when(remoteShotRepository.getShotsForTimeline(any(TimelineParameters.class))).thenReturn(unorderedShots());

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();
        List<Shot> firstTimelineShots = timelinesResult.get(0).getShots();
        List<Shot> secondTimelineShots = timelinesResult.get(1).getShots();

        assertThat(firstTimelineShots).isSortedAccordingTo(new Shot.NewerAboveComparator());
        assertThat(secondTimelineShots).isSortedAccordingTo(new Shot.NewerAboveComparator());
    }

    //region Setups and stubs
    private void setupWatchingEvent() {
        when(sessionRepository.getCurrentUser()).thenReturn(currentUserWatching());
        when(localEventRepository.getEventById(WATCHING_EVENT_ID)).thenReturn(watchingEvent());
    }

    private void setupNoWatchingEvent() {
        when(sessionRepository.getCurrentUser()).thenReturn(currentUserNotWatching());
    }

    private User currentUserWatching() {
        User user = new User();
        user.setIdWatchingEvent(WATCHING_EVENT_ID);
        return user;
    }

    private User currentUserNotWatching() {
        return new User();
    }

    private Event watchingEvent() {
        Event event = new Event();
        event.setId(WATCHING_EVENT_ID);
        event.setAuthorId(WATCHING_EVENT_AUTHOR);
        return event;
    }

    private List<Shot> unorderedShots() {
        return Arrays.asList(shotWithDate(DATE_MIDDLE), shotWithDate(DATE_OLDER), shotWithDate(DATE_NEWER));
    }

    private Shot shotWithDate(Long date) {
        Shot shot = new Shot();
        shot.setPublishDate(new Date(date));
        return shot;
    }
    //endregion
}