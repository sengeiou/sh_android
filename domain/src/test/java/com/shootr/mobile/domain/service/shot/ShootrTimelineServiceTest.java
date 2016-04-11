package com.shootr.mobile.domain.service.shot;

import com.shootr.mobile.domain.Activity;
import com.shootr.mobile.domain.ActivityTimeline;
import com.shootr.mobile.domain.ActivityTimelineParameters;
import com.shootr.mobile.domain.ActivityType;
import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.StreamTimelineParameters;
import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.domain.repository.TimelineSynchronizationRepository;
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

import static com.shootr.mobile.domain.asserts.StreamTimelineParametersAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShootrTimelineServiceTest {

    public static final String USER_ID = "user_id";
    public static final String ID_STREAM = "idStream";
    private static final Long DATE_OLDER = 1L;
    private static final Long DATE_MIDDLE = 2L;
    private static final Long DATE_NEWER = 3L;
    private static final String WATCHING_STREAM_ID = "idStream";
    private static final Long WATCHING_STREAM_REFRESH_DATE = 1000L;
    private static final String STREAM_SHOT_ID = "stream_shot";
    private static final String CURRENT_USER_ID = "current_user";
    private static final Date DATE_STUB = new Date();
    public static final String LANGUAGE = "LANGUAGE";
    private static final Boolean NOT_PAUSED = false;
    @Mock ShotRepository remoteShotRepository;
    @Mock ActivityRepository remoteActivityRepository;
    @Mock ActivityRepository localActivityRepository;
    @Mock TimelineSynchronizationRepository timelineSynchronizationRepository;
    @Mock ActivityTimelineParameters activityTimelineParameters;

    private ShootrTimelineService shootrTimelineService;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shootrTimelineService = new ShootrTimelineService(remoteShotRepository,
          localActivityRepository,
          remoteActivityRepository,
          timelineSynchronizationRepository);
    }

    @Test public void shouldReturnStreamTimelineWhenRefreshStreamTimeline() throws Exception {
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters())).thenReturn(
          streamShotListWithMultipleShots());

        Timeline resultTimeline = shootrTimelineService.refreshTimelinesForStream(ID_STREAM, NOT_PAUSED);

        assertThat(resultTimeline.getShots()).isEqualTo(streamShotListWithMultipleShots());
    }

    @Test public void shouldReturnActivityTimelineWhenRefreshActivityTimeline() throws Exception {
        List<Activity> activities = activitiesList();
        when(remoteActivityRepository.getActivityTimeline(anyActivityParameters(), anyString())).thenReturn(activities);
        ActivityTimeline resultTimeline = shootrTimelineService.refreshTimelinesForActivity(LANGUAGE);

        assertThat(resultTimeline.getActivities()).isEqualTo(activities);
    }

    @Test public void shouldReturnActivityTimelineWhenRefreshActivityTimelineAndNotWatchingAnyStream()
      throws Exception {
        List<Activity> activities = activitiesList();
        when(remoteActivityRepository.getActivityTimeline(anyActivityParameters(), anyString())).thenReturn(activities);

        ActivityTimeline resultTimeline = shootrTimelineService.refreshTimelinesForActivity(LANGUAGE);

        assertThat(resultTimeline.getActivities()).isEqualTo(activities);
    }

    @Test public void shouldNotRefreshStreamShotsWhenRefreshActivityTimelineAndNotWatchingAnyStream() throws Exception {
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters())).thenReturn(streamShotList());

        shootrTimelineService.refreshTimelinesForActivity(LANGUAGE);

        verify(remoteShotRepository, never()).getShotsForStreamTimeline(anyStreamParameters());
    }

    @Test public void shouldRequestTimelineWithStreamIdWhenWatchingStream() throws Exception {
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters())).thenReturn(new ArrayList<Shot>());

        shootrTimelineService.refreshTimelinesForStream(ID_STREAM, NOT_PAUSED);

        assertThat(captureTimelineParameters()).hasStreamId(ID_STREAM);
    }

    @Test public void shouldRequestTimelinehWithStreamRefreshDateWhenWatchingStream() throws Exception {
        when(timelineSynchronizationRepository.getStreamTimelineRefreshDate(WATCHING_STREAM_ID)).thenReturn(
          WATCHING_STREAM_REFRESH_DATE);

        shootrTimelineService.refreshTimelinesForStream(ID_STREAM, NOT_PAUSED);

        assertThat(captureTimelineParameters()).hasSinceDate(WATCHING_STREAM_REFRESH_DATE);
    }

    @Test public void shouldSetTimelineRefreshDateWhenRemoteShotsReturned() throws Exception {
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters())).thenReturn(Arrays.asList(
          shotWithDate(2L),
          shotWithDate(1L)));

        shootrTimelineService.refreshTimelinesForStream(ID_STREAM, NOT_PAUSED);

        verify(timelineSynchronizationRepository).setStreamTimelineRefreshDate(WATCHING_STREAM_ID, 2L);
    }

    @Test public void shouldNotSetTimelineRefreshDateWhenEmptyRemoteShotsReturned() throws Exception {
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters()))
          .thenReturn(Collections.<Shot>emptyList());

        shootrTimelineService.refreshTimelinesForStream(ID_STREAM, NOT_PAUSED);

        verify(timelineSynchronizationRepository, never()).setStreamTimelineRefreshDate(anyString(), anyLong());
    }

    @Test public void shouldReturnTimelineShotsOrderedByNewerAboveComparatorWhenWatchingStream() throws Exception {
        when(remoteShotRepository.getShotsForStreamTimeline(anyStreamParameters())).thenReturn(unorderedShots());

        Timeline resultTimeline = shootrTimelineService.refreshTimelinesForStream(ID_STREAM, NOT_PAUSED);

        assertThat(resultTimeline.getShots()).isSortedAccordingTo(new Shot.NewerAboveComparator());
    }

    @Test public void shouldReturnVisibleActivityTypesIfIsThereWasLocalActivity() {
        when(localActivityRepository.getActivityTimeline(anyActivityParameters(), anyString())).thenReturn(
          activitiesList());

        shootrTimelineService.refreshTimelinesForActivity(LANGUAGE);

        ArgumentCaptor<ActivityTimelineParameters> argumentCaptor =
          ArgumentCaptor.forClass(ActivityTimelineParameters.class);
        verify(remoteActivityRepository).getActivityTimeline(argumentCaptor.capture(), anyString());
        assertThat(argumentCaptor.getValue().getIncludedTypes()).containsExactly(visibleActivityTypes());
    }

    @Test public void shouldReturnVisibleActivityTypesIfThereWasNoLocalActivity() {
        when(localActivityRepository.getActivityTimeline(anyActivityParameters(), anyString())).thenReturn(
          emptyActivityList());

        ActivityTimeline activityTimeline = shootrTimelineService.refreshTimelinesForActivity(LANGUAGE);

        ArgumentCaptor<ActivityTimelineParameters> argumentCaptor =
          ArgumentCaptor.forClass(ActivityTimelineParameters.class);
        verify(remoteActivityRepository).getActivityTimeline(argumentCaptor.capture(), anyString());
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

    private List<Shot> streamShotListWithMultipleShots() {
        List<Shot> shots = new ArrayList<>();
        shots.add(streamShot());
        shots.add(streamShot());
        return shots;
    }

    private List<Activity> activitiesList() {
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