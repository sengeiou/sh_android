package com.shootr.android.domain.service.shot;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.SynchronizationRepository;
import com.shootr.android.domain.repository.UserRepository;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.shootr.android.domain.asserts.TimelineParametersAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ShootrTimelineServiceTest {

    private static final Long DATE_OLDER = 1L;
    private static final Long DATE_MIDDLE = 2L;
    private static final Long DATE_NEWER = 3L;

    private static final Long VISIBLE_EVENT_ID = 1L;
    private static final Long VISIBLE_EVENT_AUTHOR = 2L;
    private static final Long VISIBLE_EVENT_REFRESH_DATE = 1000L;

    @Mock SessionRepository sessionRepository;
    @Mock EventRepository localEventRepository;
    @Mock UserRepository localUserRepository;
    @Mock ShotRepository remoteShotRepository;
    @Mock SynchronizationRepository synchronizationRepository;

    @Captor ArgumentCaptor<List<TimelineParameters>> timelineParametersCaptor;

    private ShootrTimelineService shootrTimelineService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shootrTimelineService = new ShootrTimelineService(sessionRepository, localEventRepository, localUserRepository, remoteShotRepository, synchronizationRepository);
    }

    @Test
    public void shouldReturnFirstTimelineWithEventIdAndAuthorWhenEventVisible() throws Exception {
        setupVisibleEvent();

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();
        TimelineParameters timelineParameters = timelinesResult.get(0).getParameters();

        assertThat(timelineParameters).hasEventId(VISIBLE_EVENT_ID).hasEventAuthorId(VISIBLE_EVENT_AUTHOR);
    }

    @Test
    public void shouldReturnFirstTimelinehWithEventRefreshDateWhenEventVisible() throws Exception {
        setupVisibleEvent();
        when(synchronizationRepository.getEventTimelineRefreshDate(VISIBLE_EVENT_ID)).thenReturn(VISIBLE_EVENT_REFRESH_DATE);

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();
        TimelineParameters timelineParameters = timelinesResult.get(0).getParameters();

        assertThat(timelineParameters).hasSinceDate(VISIBLE_EVENT_REFRESH_DATE);
    }

    @Test
    public void shoudReturnTwoTimelinesWhenEventVisible() throws Exception {
        setupVisibleEvent();

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();

        assertThat(timelinesResult).hasSize(2);
    }

    @Test
    public void shoudReturnOnlyOneTimelineWhenNoEventVisible() throws Exception {
        setupNoVisibleEvent();

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();

        assertThat(timelinesResult).hasSize(1);
    }

    @Test
    public void shouldReturnSecondTimelineWithoutEventIdAndAuthorWhenEventVisible() throws Exception {
        setupVisibleEvent();

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();
        TimelineParameters timelineParameters = timelinesResult.get(1).getParameters();

        assertThat(timelineParameters).hasNoEventId().hasNoEventAuthorId();
    }

    @Test
    public void shouldReturnFirstTimelineWithoutEventIdAndAuthorWhenNoEventVisible() throws Exception {
        setupNoVisibleEvent();

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();
        TimelineParameters timelineParameters = timelinesResult.get(0).getParameters();

        assertThat(timelineParameters).hasNoEventId().hasNoEventAuthorId();
    }

    @Test
    public void shouldReturnTwoTimelinesShotsInOrderWithPublishDateComparatorWhenEventVisible() throws Exception {
        setupVisibleEvent();
        when(remoteShotRepository.getShotsForTimeline(any(TimelineParameters.class))).thenReturn(unorderedShots());

        List<Timeline> timelinesResult = shootrTimelineService.refreshTimelines();
        List<Shot> firstTimelineShots = timelinesResult.get(0).getShots();
        List<Shot> secondTimelineShots = timelinesResult.get(1).getShots();

        assertThat(firstTimelineShots).isSortedAccordingTo(new Shot.NewerAboveComparator());
        assertThat(secondTimelineShots).isSortedAccordingTo(new Shot.NewerAboveComparator());
    }

    //region Setups and stubs
    private void setupVisibleEvent() {
        when(sessionRepository.getCurrentUser()).thenReturn(currentUserWatching());
        when(localEventRepository.getEventById(VISIBLE_EVENT_ID)).thenReturn(visibleEvent());
    }

    private void setupNoVisibleEvent() {
        when(sessionRepository.getCurrentUser()).thenReturn(currentUserNotWatching());
    }

    private User currentUserWatching() {
        User user = new User();
        user.setVisibleEventId(VISIBLE_EVENT_ID);
        return user;
    }

    private User currentUserNotWatching() {
        return new User();
    }

    private Event visibleEvent() {
        Event event = new Event();
        event.setId(VISIBLE_EVENT_ID);
        event.setAuthorId(VISIBLE_EVENT_AUTHOR);
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