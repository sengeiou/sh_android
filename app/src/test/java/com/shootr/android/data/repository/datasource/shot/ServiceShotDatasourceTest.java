package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.api.entity.mapper.ShotApiEntityMapper;
import com.shootr.android.data.api.service.ShotApiService;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.ShotType;
import com.shootr.android.service.ShootrService;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceShotDatasourceTest {

    private static final ActivityTimelineParameters TIMELINE_PARAMETERS_STUB = null;
    private static final String ID_USER_STUB = "1L";
    private static final Date DATE_NEWER = new Date(2000);
    private static final Date DATE_OLDER = new Date(1000);

    @Mock ShootrService shootrService;
    @Mock ShotApiService shotApiService;

    private ServiceShotDatasource datasource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ShotApiEntityMapper shotApiEntityMapper = new ShotApiEntityMapper();
        datasource = new ServiceShotDatasource(shootrService, shotApiService,
          shotApiEntityMapper);
    }

    //TODO tests with hidden sync

    @Test
    public void shouldPostEventToBusWhenShotReceived() throws Exception {
        when(shootrService.getActivityShotsByParameters(any(ActivityTimelineParameters.class))).thenReturn(oneTriggerShot());

        datasource.getShotsForActivityTimeline(TIMELINE_PARAMETERS_STUB);

        verify(shootrService).getActivityShotsByParameters(any(ActivityTimelineParameters.class));

    }

    private List<ShotEntity> oneTriggerShot() {
        return Arrays.asList(syncShot());
    }

    private ShotEntity syncShot() {
        ShotEntity shotEntity = new ShotEntity();
        shotEntity.setType(ShotType.COMMENT);
        shotEntity.setIdUser(ID_USER_STUB);
        shotEntity.setBirth(DATE_NEWER);
        return shotEntity;
    }

}