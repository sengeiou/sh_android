package com.shootr.android.data.repository.datasource.activity;

import com.shootr.android.data.api.entity.ActivityApiEntity;
import com.shootr.android.data.api.entity.EmbedUserApiEntity;
import com.shootr.android.data.api.entity.mapper.ActivityApiEntityMapper;
import com.shootr.android.data.api.service.ActivityApiService;
import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.ActivityType;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.WatchUpdateRequest;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceActivityDataSourceTest {

    private static final String ID_USER_STUB = "user";
    public static final String ANOTHER_ID_USER_STUB = "user_id";
    private static final Date DATE_NEWER = new Date(2000);
    private static final Date DATE_OLDER = new Date(1000);

    @Mock ActivityDataSource activityDataSource;
    @Mock BusPublisher busPublisher;
    @Mock SessionRepository sessionRepository;
    @Mock ActivityApiService activityApiService;
    @Mock EventDataSource localEventDataSource;
    @Mock ActivityTimelineParameters activityTimelineParameters;
    private ServiceActivityDataSource datasource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ActivityApiEntityMapper activityApiEntityMapper = new ActivityApiEntityMapper();
        datasource = new ServiceActivityDataSource(activityApiService,
          activityApiEntityMapper,
          busPublisher,
          sessionRepository,
          localEventDataSource);
    }

    @Test
    public void shouldPostEventToBusWhenSyncTriggerActivityReceived() throws Exception {
        when(activityApiService.getActivityTimeline(anyString(), anyList(), anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(syncActivityApi()));
        when(sessionRepository.getCurrentUserId()).thenReturn(ANOTHER_ID_USER_STUB);
        datasource.getActivityTimeline(activityTimelineParameters);

        ArgumentCaptor<WatchUpdateRequest.Event> captor = ArgumentCaptor.forClass(WatchUpdateRequest.Event.class);
        verify(busPublisher).post(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(WatchUpdateRequest.Event.class);
    }

    @Test
    public void shouldPostOnlyOneEventWhenTwhoSyncTriggerActivitiesReceived() throws Exception {
        when(activityApiService.getActivityTimeline(anyString(), anyList(), anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(
          syncActivityApi()));
        when(sessionRepository.getCurrentUserId()).thenReturn(ANOTHER_ID_USER_STUB);
        datasource.getActivityTimeline(activityTimelineParameters);

        ArgumentCaptor<WatchUpdateRequest.Event> captor = ArgumentCaptor.forClass(WatchUpdateRequest.Event.class);
        verify(busPublisher, times(1)).post(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(WatchUpdateRequest.Event.class);
    }

    @Test
    public void shouldPostOnlyOneEventWhenReceivedActivityWithSameDateThanPreviousTime() throws Exception {
        when(activityApiService.getActivityTimeline(anyString(), anyList(), anyInt(), anyLong(), anyLong())).thenReturn(
          twoTriggerActivity());
        when(sessionRepository.getCurrentUserId()).thenReturn(ANOTHER_ID_USER_STUB);

        datasource.getActivityTimeline(activityTimelineParameters);
        datasource.getActivityTimeline(activityTimelineParameters);

        ArgumentCaptor<WatchUpdateRequest.Event> captor = ArgumentCaptor.forClass(WatchUpdateRequest.Event.class);
        verify(busPublisher, times(1)).post(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(WatchUpdateRequest.Event.class);
    }

    @Test
    public void shouldPostOnlyOneEventWhenReceivedActivityWithOlderDateThanPreviousTime() throws Exception {
        when(activityApiService.getActivityTimeline(anyString(), anyList(), anyInt(), anyLong(), anyLong())).thenReturn(
          twoTriggerActivityWithDifferentDates());
        when(sessionRepository.getCurrentUserId()).thenReturn(ANOTHER_ID_USER_STUB);

        datasource.getActivityTimeline(activityTimelineParameters);
        datasource.getActivityTimeline(activityTimelineParameters);

        ArgumentCaptor<WatchUpdateRequest.Event> captor = ArgumentCaptor.forClass(WatchUpdateRequest.Event.class);
        verify(busPublisher).post(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(WatchUpdateRequest.Event.class);
    }

    @Test
    public void shouldNotPostEventWhenTriggerActivityFromCurrentUser() throws Exception {
        when(activityDataSource.getActivityTimeline(any(ActivityTimelineParameters.class))).thenReturn(
          oneTriggerActivity());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER_STUB);

        datasource.getActivityTimeline(activityTimelineParameters);

        verify(busPublisher, never()).post(any(WatchUpdateRequest.Event.class));
    }

    private List<ActivityEntity> oneTriggerActivity() {
        return Arrays.asList(syncActivity());
    }

    private List<ActivityApiEntity> twoTriggerActivity() {
        return Arrays.asList(syncActivityApi(), syncActivityApi());
    }

    private List<ActivityApiEntity> twoTriggerActivityWithDifferentDates() {
        return Arrays.asList(syncActivityApiWithDate(DATE_NEWER), syncActivityApiWithDate(DATE_OLDER));
    }

    private ActivityApiEntity syncActivityApiWithDate(Date dateOlder) {
        ActivityApiEntity activityApiEntity = syncActivityApi();
        activityApiEntity.setBirth(dateOlder.getTime());
        return activityApiEntity;
    }

    private ActivityApiEntity syncActivityApi() {
        ActivityApiEntity activityApiEntity = new ActivityApiEntity();
        activityApiEntity.setType(ActivityType.TYPES_SYNC_TRIGGER[0]);
        activityApiEntity.setIdUser(ID_USER_STUB);
        activityApiEntity.setBirth(new Date().getTime());
        activityApiEntity.setModified(new Date().getTime());
        EmbedUserApiEntity userApiEntity = new EmbedUserApiEntity();
        userApiEntity.setIdUser(ID_USER_STUB);
        activityApiEntity.setUser(userApiEntity);
        return activityApiEntity;
    }

    private ActivityEntity syncActivity() {
        ActivityEntity activityEntity = new ActivityEntity();
        activityEntity.setType(ActivityType.TYPES_SYNC_TRIGGER[0]);
        activityEntity.setIdUser(ID_USER_STUB);
        activityEntity.setBirth(DATE_NEWER);
        return activityEntity;
    }
}