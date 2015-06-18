package com.shootr.android.domain.service.shot;

import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimeline;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventTimelineParameters;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.TimelineSynchronizationRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.shootr.android.domain.asserts.EventTimelineParametersAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShootrTimelineServiceTest {

    private static final Long DATE_OLDER = 1L;
    private static final Long DATE_MIDDLE = 2L;
    private static final Long DATE_NEWER = 3L;

    private static final String WATCHING_EVENT_ID = "watching_event";
    private static final Long WATCHING_EVENT_REFRESH_DATE = 1000L;

    private static final String EVENT_SHOT_ID = "event_shot";
    private static final String ACTIVITY_SHOT_ID = "activity_shot";
    private static final String CURRENT_USER_ID = "current_user";
    private static final Date DATE_STUB = new Date();

    @Mock SessionRepository sessionRepository;
    @Mock EventRepository localEventRepository;
    @Mock UserRepository localUserRepository;
    @Mock ShotRepository remoteShotRepository;
    @Mock ActivityRepository remoteActivityRepository;
    @Mock ActivityRepository localActivityRepository;
    @Mock TimelineSynchronizationRepository timelineSynchronizationRepository;
    @Mock ShotRepository localShotRepository;

    private ShootrTimelineService shootrTimelineService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shootrTimelineService = new ShootrTimelineService(sessionRepository,
          localEventRepository,
          localUserRepository,
          remoteShotRepository, localActivityRepository, remoteActivityRepository, localShotRepository,
          timelineSynchronizationRepository);
    }

    @Test
    public void shouldReturnEventTimelineWhenRefreshEventTimeline() throws Exception {
        setupWatchingEvent();
        when(remoteShotRepository.getShotsForEventTimeline(anyEventParameters())).thenReturn(
          eventShotListWithMultipleShots());

        Timeline resultTimeline = shootrTimelineService.refreshTimelinesForWatchingEvent();

        assertThat(resultTimeline.getShots()).isEqualTo(eventShotListWithMultipleShots());
    }

    @Test
    public void shouldRefreshActivityShotsWhenRefreshEventTimeline() throws Exception {
        setupWatchingEvent();
        when(remoteShotRepository.getShotsForEventTimeline(anyEventParameters())).thenReturn(new ArrayList<Shot>());

        shootrTimelineService.refreshTimelinesForWatchingEvent();

        verify(remoteActivityRepository).getActivityTimeline(anyActivityParameters());
    }

    @Test
    public void shouldReturnActivityTimelineWhenRefreshActivityTimeline() throws Exception {
        setupWatchingEvent();
        List<Activity> activities = activitiesList();
        when(remoteActivityRepository.getActivityTimeline(anyActivityParameters())).thenReturn(activities);

        ActivityTimeline resultTimeline = shootrTimelineService.refreshTimelinesForActivity();

        assertThat(resultTimeline.getActivities()).isEqualTo(activities);
    }

    @Test
    public void shouldReturnActivityTimelineWhenRefreshActivityTimelineAndNotWatchingAnyEvent() throws Exception {
        List<Activity> activities = activitiesList();
        when(remoteShotRepository.getShotsForActivityTimeline(anyActivityParameters())).thenReturn(
          eventShotListWithMultipleShots());
        when(localEventRepository.getEventById(anyString())).thenReturn(watchingEvent());
        when(localUserRepository.getUserById(anyString())).thenReturn(new User());
        when(remoteActivityRepository.getActivityTimeline(anyActivityParameters())).thenReturn(activities);

        ActivityTimeline resultTimeline = shootrTimelineService.refreshTimelinesForActivity();

        assertThat(resultTimeline.getActivities()).isEqualTo(activities);
    }

    @Test
    public void shouldRefreshEventShotsWhenRefreshActivityTimeline() throws Exception {
        setupWatchingEvent();
        when(remoteShotRepository.getShotsForEventTimeline(anyEventParameters())).thenReturn(eventShotList());

        shootrTimelineService.refreshTimelinesForActivity();

        verify(remoteShotRepository).getShotsForEventTimeline(anyEventParameters());
    }

    @Test
    public void shouldNotRefreshEventShotsWhenRefreshActivityTimelineAndNotWatchingAnyEvent() throws Exception {
        when(remoteShotRepository.getShotsForEventTimeline(anyEventParameters())).thenReturn(eventShotList());
        when(localEventRepository.getEventById(anyString())).thenReturn(watchingEvent());
        when(localUserRepository.getUserById(anyString())).thenReturn(new User());

        shootrTimelineService.refreshTimelinesForActivity();

        verify(remoteShotRepository, never()).getShotsForEventTimeline(anyEventParameters());
    }

    @Test
    public void shouldRequestFewerNiceShotsWhenWatchingEventHasShotsInLocalRepository() throws Exception {
        setupWatchingEvent();
        when(localShotRepository.getShotsForEventTimeline(anyEventParameters())).thenReturn(unorderedShots());

        shootrTimelineService.refreshTimelinesForWatchingEvent();

        EventTimelineParameters parameters = captureTimelineParameters();
        assertThat(parameters.getMaxNiceShotsIncluded()).isEqualTo(ShootrTimelineService.MAXIMUM_NICE_SHOTS_WHEN_TIMELINE_EMPTY);
    }

    @Test
    public void shouldRequestFullNiceShotsWhenWatchingEventDoesntHaveShotsInLocalRepository() throws Exception {
        setupWatchingEvent();
        when(localShotRepository.getShotsForEventTimeline(anyEventParameters())).thenReturn(Collections.<Shot>emptyList());

        shootrTimelineService.refreshTimelinesForWatchingEvent();

        EventTimelineParameters parameters = captureTimelineParameters();
        assertThat(parameters.getMaxNiceShotsIncluded()).isEqualTo(ShootrTimelineService.MAXIMUM_NICE_SHOTS_WHEN_TIMELINE_HAS_SHOTS_ALREADY);
    }

    @Test
    public void shouldRequestTimelineWithEventIdWhenWatchingEvent() throws Exception {
        setupWatchingEvent();
        when(remoteShotRepository.getShotsForEventTimeline(anyEventParameters())).thenReturn(new ArrayList<Shot>());

        shootrTimelineService.refreshTimelinesForWatchingEvent();

        assertThat(captureTimelineParameters()).hasEventId(WATCHING_EVENT_ID);
    }

    @Test
    public void shouldRequestTimelinehWithEventRefreshDateWhenWatchingEvent() throws Exception {
        setupWatchingEvent();
        when(timelineSynchronizationRepository.getEventTimelineRefreshDate(WATCHING_EVENT_ID)).thenReturn(
          WATCHING_EVENT_REFRESH_DATE);

        shootrTimelineService.refreshTimelinesForWatchingEvent();

        assertThat(captureTimelineParameters()).hasSinceDate(WATCHING_EVENT_REFRESH_DATE);
    }

    @Test
    public void shouldReturnTimelineShotsOrderedByNewerAboveComparatorWhenWatchingEvent() throws Exception {
        setupWatchingEvent();
        when(remoteShotRepository.getShotsForEventTimeline(anyEventParameters())).thenReturn(unorderedShots());

        Timeline resultTimeline = shootrTimelineService.refreshTimelinesForWatchingEvent();

        assertThat(resultTimeline.getShots()).isSortedAccordingTo(new Shot.NewerAboveComparator());
    }

    //region Setups and stubs
    private void setupWatchingEvent() {
        when(sessionRepository.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(localUserRepository.getUserById(CURRENT_USER_ID)).thenReturn(currentUserWatching());
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

    private EventTimelineParameters captureTimelineParameters() {
        ArgumentCaptor<EventTimelineParameters> parametersCaptor =
          ArgumentCaptor.forClass(EventTimelineParameters.class);
        verify(remoteShotRepository).getShotsForEventTimeline(parametersCaptor.capture());
        return parametersCaptor.getValue();
    }

    private List<Shot> eventShotList() {
        return Collections.singletonList(eventShot());
    }

    private List<Shot> eventShotListWithMultipleShots(){
        List<Shot> shots = new ArrayList<>();
        shots.add(eventShot());
        shots.add(eventShot());
        return shots;
    }

    private List<Activity> activitiesList(){
        List<Activity> shots = new ArrayList<>();
        shots.add(activity());
        shots.add(activity());
        return shots;
    }

    private Activity activity() {
        Activity activity = new Activity();
        activity.setPublishDate(new Date());
        return activity;
    }

    private Shot eventShot() {
        Shot shot = new Shot();
        shot.setIdShot(EVENT_SHOT_ID);
        shot.setPublishDate(DATE_STUB);
        return shot;
    }

    private EventTimelineParameters anyEventParameters() {
        return any(EventTimelineParameters.class);
    }

    private ActivityTimelineParameters anyActivityParameters() {
        return any(ActivityTimelineParameters.class);
    }

    private Shot activityShot() {
        Shot shot = new Shot();
        shot.setIdShot(ACTIVITY_SHOT_ID);
        shot.setPublishDate(DATE_STUB);
        return shot;
    }

    //endregion
}