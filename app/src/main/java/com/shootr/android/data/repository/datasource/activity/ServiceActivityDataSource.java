package com.shootr.android.data.repository.datasource.activity;

import com.shootr.android.data.api.entity.ActivityApiEntity;
import com.shootr.android.data.api.entity.mapper.ActivityApiEntityMapper;
import com.shootr.android.data.api.service.ActivityApiService;
import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceActivityDataSource implements ActivityDataSource{

    private final ActivityApiService activityApiService;
    private final ActivityApiEntityMapper activityApiEntityMapper;

    @Inject public ServiceActivityDataSource(ActivityApiService activityApiService,
      ActivityApiEntityMapper activityApiEntityMapper) {
        this.activityApiService = activityApiService;
        this.activityApiEntityMapper = activityApiEntityMapper;
    }

    @Override public List<ActivityEntity> getActivityTimeline(ActivityTimelineParameters parameters,
      String currentUserId) {
        try {
            List<ActivityApiEntity> activities = activityApiService.getActivityTimeline(currentUserId, parameters.getIncludedTypes(), parameters.getLimit(), parameters.getSinceDate(), parameters.getMaxDate());
            return activityApiEntityMapper.transform(activities);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void putActivities(List<ActivityEntity> activityEntities) {
        throw new IllegalArgumentException("method not implemented");
    }
}
