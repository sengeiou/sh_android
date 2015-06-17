package com.shootr.android.data.repository.datasource.activity;

import com.shootr.android.data.api.entity.ActivityApiEntity;
import com.shootr.android.data.api.entity.mapper.ActivityApiEntityMapper;
import com.shootr.android.data.api.service.ActivityApiService;
import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceActivityDataSource implements ActivityDataSource{

    private final ShootrService shootrService;
    private final ActivityApiService activityApiService;
    private final ActivityApiEntityMapper activityApiEntityMapper;

    @Inject public ServiceActivityDataSource(ShootrService shootrService, ActivityApiService activityApiService,
      ActivityApiEntityMapper activityApiEntityMapper) {
        this.shootrService = shootrService;
        this.activityApiService = activityApiService;
        this.activityApiEntityMapper = activityApiEntityMapper;
    }

    @Override public List<ActivityEntity> getActivityTimeline(ActivityTimelineParameters parameters,
      String currentUserId, Integer maxActivities) {
        try {
            List<ActivityApiEntity> activities = activityApiService.getActivityTimeline(currentUserId, parameters.getIncludedTypes(),maxActivities);
            return activityApiEntityMapper.transform(activities);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void putActivities(List<ActivityEntity> activityEntities) {
        throw new IllegalArgumentException("method not implemented");
    }
}
