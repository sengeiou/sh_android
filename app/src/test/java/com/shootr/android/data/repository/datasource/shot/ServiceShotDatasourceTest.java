package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.WatchUpdateRequest;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.service.ShootrService;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceShotDatasourceTest {

    private static final TimelineParameters TIMELINE_PARAMETERS_STUB = null;
    private static final Long ID_USER_STUB = 1L;
    private static final Date DATE_NEWER = new Date(2000);
    private static final Date DATE_OLDER = new Date(1000);

    @Mock ShootrService shootrService;
    @Mock BusPublisher busPublisher;
    @Mock SessionRepository sessionRepository;

    private ServiceShotDatasource datasource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        datasource = new ServiceShotDatasource(shootrService, busPublisher, sessionRepository);
    }

    //TODO tests with hidden sync

    @Test
    public void shouldPostEventToBusWhenSyncTriggerShotReceived() throws Exception {
        when(shootrService.getShotsByParameters(any(TimelineParameters.class))).thenReturn(oneTriggerShot());

        datasource.getShotsForTimeline(TIMELINE_PARAMETERS_STUB);

        ArgumentCaptor<WatchUpdateRequest.Event> captor = ArgumentCaptor.forClass(WatchUpdateRequest.Event.class);
        verify(busPublisher).post(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(WatchUpdateRequest.Event.class);
    }

    @Test
    public void shouldPostOnlyOneEventWhenTwhoSyncTriggerShotsReceived() throws Exception {
        when(shootrService.getShotsByParameters(any(TimelineParameters.class))).thenReturn(twoTriggerShots());


        datasource.getShotsForTimeline(TIMELINE_PARAMETERS_STUB);

        ArgumentCaptor<WatchUpdateRequest.Event> captor = ArgumentCaptor.forClass(WatchUpdateRequest.Event.class);
        verify(busPublisher, times(1)).post(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(WatchUpdateRequest.Event.class);
    }

    @Test
    public void shouldPostOnlyOneEventWhenReceivedShotWithSameDateThanPreviousTime() throws Exception {
        when(shootrService.getShotsByParameters(any(TimelineParameters.class))).thenReturn(twoTriggerShots());

        datasource.getShotsForTimeline(TIMELINE_PARAMETERS_STUB);
        datasource.getShotsForTimeline(TIMELINE_PARAMETERS_STUB);

        ArgumentCaptor<WatchUpdateRequest.Event> captor = ArgumentCaptor.forClass(WatchUpdateRequest.Event.class);
        verify(busPublisher, times(1)).post(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(WatchUpdateRequest.Event.class);
    }

    @Test
    public void shouldPostOnlyOneEventWhenReceivedShotWithOlderDateThanPreviousTime() throws Exception {
        when(shootrService.getShotsByParameters(any(TimelineParameters.class))).thenReturn(Arrays.asList(
          syncShotWithDate(DATE_NEWER), syncShotWithDate(DATE_OLDER)));

        datasource.getShotsForTimeline(TIMELINE_PARAMETERS_STUB);
        datasource.getShotsForTimeline(TIMELINE_PARAMETERS_STUB);

        ArgumentCaptor<WatchUpdateRequest.Event> captor = ArgumentCaptor.forClass(WatchUpdateRequest.Event.class);
        verify(busPublisher).post(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(WatchUpdateRequest.Event.class);
    }

    @Test
    public void shouldNotPostEventWhenTriggerShotFromCurrentUser() throws Exception {
        when(shootrService.getShotsByParameters(any(TimelineParameters.class))).thenReturn(oneTriggerShot());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER_STUB);

        datasource.getShotsForTimeline(TIMELINE_PARAMETERS_STUB);

        verify(busPublisher, never()).post(any(WatchUpdateRequest.Event.class));
    }

    private List<ShotEntity> oneTriggerShot() {
        return Arrays.asList(syncShot());
    }

    private List<ShotEntity> twoTriggerShots() {
        return Arrays.asList(syncShot(), syncShot());
    }

    private ShotEntity syncShot() {
        ShotEntity shotEntity = new ShotEntity();
        shotEntity.setType(ShotEntity.TYPE_TRIGGER_SYNC);
        shotEntity.setIdUser(ID_USER_STUB);
        shotEntity.setCsysBirth(DATE_NEWER);
        return shotEntity;
    }

    private ShotEntity syncShotWithDate(Date date) {
        ShotEntity shot = syncShot();
        shot.setCsysBirth(date);
        return shot;
    }
}