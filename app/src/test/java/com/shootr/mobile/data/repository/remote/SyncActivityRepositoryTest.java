package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.data.mapper.ActivityEntityMapper;
import com.shootr.mobile.data.repository.datasource.activity.ActivityDataSource;
import com.shootr.mobile.domain.model.activity.ActivityTimelineParameters;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.repository.ShotRepository;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SyncActivityRepositoryTest {

  private static final ActivityTimelineParameters PARAMETERS = new ActivityTimelineParameters();
  private static final String LOCALE = "locale";
  private static final String SHOT_ID = "shotId";
  @Mock ActivityDataSource localActivityDataSource;
  @Mock ActivityDataSource remoteActivityDataSource;
  @Mock ActivityEntityMapper activityEntityMapper;
  @Mock ShotRepository remoteShotRepository;
  @Mock ShotRepository localShotRepository;

  private SyncActivityRepository repository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    repository = new SyncActivityRepository(localActivityDataSource, remoteActivityDataSource,
        activityEntityMapper, remoteShotRepository, localShotRepository);
  }

  @Test public void shouldPutActivitiesInLocalWhenGetActivityTimeline() throws Exception {
    when(remoteActivityDataSource.getActivityTimeline(any(ActivityTimelineParameters.class),
        anyString())).thenReturn(activityEntities());
    when(remoteShotRepository.getShot(anyString(), anyArray(), anyArray())).thenReturn(shot());

    repository.getActivityTimeline(PARAMETERS, LOCALE);

    verify(localActivityDataSource).putActivities(anyList());
  }

  private Shot shot() {
    Shot shot = new Shot();
    shot.setIdShot(SHOT_ID);
    return shot;
  }

  private String[] anyArray() {
    return any(String[].class);
  }

  private List<ActivityEntity> activityEntities() {
    ActivityEntity activityEntity = new ActivityEntity();
    activityEntity.setIdShot(SHOT_ID);

    return Collections.singletonList(activityEntity);
  }
}
