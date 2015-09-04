package com.shootr.android.domain.service.shot;

import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimeline;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.ActivityType;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.StreamTimelineParameters;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.TimelineSynchronizationRepository;
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

import static com.shootr.android.domain.asserts.StreamTimelineParametersAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShootrTimelineServiceTest {

    private static final Long DATE_OLDER = 1L;
    private static final Long DATE_MIDDLE = 2L;
    private static final Long DATE_NEWER = 3L;

    private static final String WATCHING_STREAM_ID = "idStream";
    private static final Long WATCHING_STREAM_REFRESH_DATE = 1000L;

    private static final String STREAM_SHOT_ID = "stream_shot";
    private static final String CURRENT_USER_ID = "current_user";
    private static final Date DATE_STUB = new Date();
    public static final String USER_ID = "user_id";
    public static final String ID_STREAM = "idStream";

    @Mock SessionRepository sessionRepository;
    @Mock ShotRepository remoteShotRepository;
    @Mock ActivityRepository remoteActivityRepository;
    @Mock ActivityRepository localActivityRepository;
    @Mock TimelineSynchronizationRepository timelineSynchronizationRepository;
    @Mock ActivityTimelineParameters activityTimelineParameters;

    private ShootrTimelineService shootrTimelineService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shootrTimelineService = new ShootrTimelineService(sessionRepository,
          remoteShotRepository,
          localActivityRepository,
          remoteActivityRepository,
          timelineSynchronizationRepository);
    }

    @Test
    public void shouldReturnStreamTimelineWhenRefreshStreamTimeline() throws Exception {
        setupWatchingStream();
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters())).thenReturn(
          streamShotListWithMultipleShots());

        Timeline resultTimeline = shootrTimelineService.refreshTimelinesForStream(ID_STREAM);

        assertThat(resultTimeline.getShots()).isEqualTo(streamShotListWithMultipleShots());
    }

    @Test
    public void shouldRefreshActivityShotsWhenRefreshStreamTimeline() throws Exception {
        setupWatchingStream();
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters())).thenReturn(new ArrayList<Shot>());

        shootrTimelineService.refreshTimelinesForStream(ID_STREAM);

        verify(remoteActivityRepository).getActivityTimeline(anyActivityParameters());
    }

    @Test
    public void shouldReturnActivityTimelineWhenRefreshActivityTimeline() throws Exception {
        setupWatchingStream();
        List<Activity> activities = activitiesList();
        when(remoteActivityRepository.getActivityTimeline(anyActivityParameters())).thenReturn(activities);
        when(sessionRepository.getCurrentUser()).thenReturn(userWithWatchingStreamId());
        ActivityTimeline resultTimeline = shootrTimelineService.refreshTimelinesForActivity();

        assertThat(resultTimeline.getActivities()).isEqualTo(activities);
    }

    @Test
    public void shouldReturnActivityTimelineWhenRefreshActivityTimelineAndNotWatchingAnyStream() throws Exception {
        List<Activity> activities = activitiesList();
        when(remoteActivityRepository.getActivityTimeline(anyActivityParameters())).thenReturn(activities);
        when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutWatchingStreamId());

        ActivityTimeline resultTimeline = shootrTimelineService.refreshTimelinesForActivity();

        assertThat(resultTimeline.getActivities()).isEqualTo(activities);
    }

    @Test
    public void shouldRefreshStreamShotsWhenRefreshActivityTimeline() throws Exception {
        setupWatchingStream();
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters())).thenReturn(streamShotList());
        when(sessionRepository.getCurrentUser()).thenReturn(userWithWatchingStreamId());

        shootrTimelineService.refreshTimelinesForActivity();

        verify(remoteShotRepository).getShotsForStreamTimeline(anyStreamParameters());
    }

    @Test
    public void shouldNotRefreshStreamShotsWhenRefreshActivityTimelineAndNotWatchingAnyStream() throws Exception {
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters())).thenReturn(streamShotList());
        when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutWatchingStreamId());

        shootrTimelineService.refreshTimelinesForActivity();

        verify(remoteShotRepository, never()).getShotsForStreamTimeline(anyStreamParameters());
    }

    @Test
    public void shouldRequestTimelineWithStreamIdWhenWatchingStream() throws Exception {
        setupWatchingStream();
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters())).thenReturn(new ArrayList<Shot>());

        shootrTimelineService.refreshTimelinesForStream(ID_STREAM);

        assertThat(captureTimelineParameters()).hasStreamId(ID_STREAM);
    }

    @Test
    public void shouldRequestTimelinehWithStreamRefreshDateWhenWatchingStream() throws Exception {
        setupWatchingStream();
        when(timelineSynchronizationRepository.getStreamTimelineRefreshDate(WATCHING_STREAM_ID)).thenReturn(
          WATCHING_STREAM_REFRESH_DATE);

        shootrTimelineService.refreshTimelinesForStream(ID_STREAM);

        assertThat(captureTimelineParameters()).hasSinceDate(WATCHING_STREAM_REFRESH_DATE);
    }

    @Test
    public void shouldSetTimelineRefreshDateWhenRemoteShotsReturned() throws Exception {
        setupWatchingStream();
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters())).thenReturn(Arrays.asList(
          shotWithDate(2L),
          shotWithDate(1L)));

        shootrTimelineService.refreshTimelinesForStream(ID_STREAM);

        verify(timelineSynchronizationRepository).setStreamTimelineRefreshDate(WATCHING_STREAM_ID, 2L);
    }

    @Test
    public void shouldNotSetTimelineRefreshDateWhenEmptyRemoteShotsReturned() throws Exception {
        setupWatchingStream();
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters())).thenReturn(Collections.<Shot>emptyList());

        shootrTimelineService.refreshTimelinesForStream(ID_STREAM);

        verify(timelineSynchronizationRepository, never()).setStreamTimelineRefreshDate(anyString(), anyLong());
    }

    @Test
    public void shouldReturnTimelineShotsOrderedByNewerAboveComparatorWhenWatchingStream() throws Exception {
        setupWatchingStream();
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters())).thenReturn(unorderedShots());

        Timeline resultTimeline = shootrTimelineService.refreshTimelinesForStream(ID_STREAM);

        assertThat(resultTimeline.getShots()).isSortedAccordingTo(new Shot.NewerAboveComparator());
    }

    @Test
    public void shouldReturnAllActivityTypesIfIsThereWasLocalActivity(){
        setupWatchingStream();
        when(localActivityRepository.getActivityTimeline(anyActivityParameters())).thenReturn(activitiesList());
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutWatchingStreamId());

        ActivityTimeline activityTimeline = shootrTimelineService.refreshTimelinesForActivity();

        ArgumentCaptor<ActivityTimelineParameters> argumentCaptor = ArgumentCaptor.forClass(ActivityTimelineParameters.class);
        verify(remoteActivityRepository).getActivityTimeline(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getIncludedTypes()).containsExactly(allActivityTypes());
    }

    @Test
    public void shouldReturnVisibleActivityTypesIfThereWasNoLocalActivity(){
        setupWatchingStream();
        when(localActivityRepository.getActivityTimeline(anyActivityParameters())).thenReturn(emptyActivityList());
        when(sessionRepository.getCurrentUser()).thenReturn(userWithWatchingStreamId());
        ActivityTimeline activityTimeline = shootrTimelineService.refreshTimelinesForActivity();

        ArgumentCaptor<ActivityTimelineParameters> argumentCaptor = ArgumentCaptor.forClass(ActivityTimelineParameters.class);
        verify(remoteActivityRepository).getActivityTimeline(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getIncludedTypes()).containsExactly(visibleActivityTypes());
    }

    private User userWithWatchingStreamId() {
        User user = new User();
        user.setIdWatchingStream("idStream");
        return user;
    }

    private User userWithoutWatchingStreamId() {
        return new User();
    }

    private String[] visibleActivityTypes() {
        return ActivityType.TYPES_ACTIVITY_SHOWN;
    }

    private List<Activity> emptyActivityList() {
        return Collections.EMPTY_LIST;
    }

    private String[] allActivityTypes() {
        return ActivityType.TYPES_ACTIVITY;
    }

    //region Setups and stubs
    private void setupWatchingStream() {
        when(sessionRepository.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
    }

    private List<Shot> unorderedShots() {
        return Arrays.asList(shotWithDate(DATE_MIDDLE), shotWithDate(DATE_OLDER), shotWithDate(DATE_NEWER));
    }

    private Shot shotWithDate(Long date) {
        Shot shot = new Shot();
        shot.setPublishDate(new Date(date));
        return shot;
    }

    private StreamTimelineParameters captureTimelineParameters() {
        ArgumentCaptor<StreamTimelineParameters> parametersCaptor =
          ArgumentCaptor.forClass(StreamTimelineParameters.class);
        verify(remoteShotRepository).getShotsForStreamTimeline(parametersCaptor.capture());
        return parametersCaptor.getValue();
    }

    private List<Shot> streamShotList() {
        return Collections.singletonList(streamShot());
    }

    private List<Shot> streamShotListWithMultipleShots(){
        List<Shot> shots = new ArrayList<>();
        shots.add(streamShot());
        shots.add(streamShot());
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

    private Shot streamShot() {
        Shot shot = new Shot();
        shot.setIdShot(STREAM_SHOT_ID);
        shot.setPublishDate(DATE_STUB);
        return shot;
    }

    private StreamTimelineParameters anyStreamParameters() {
        return any(StreamTimelineParameters.class);
    }

    private ActivityTimelineParameters anyActivityParameters() {
        return any(ActivityTimelineParameters.class);
    }

    //endregion
}