package com.shootr.mobile.data.repository.datasource.activity;

import com.shootr.mobile.data.api.entity.ActivityApiEntity;
import com.shootr.mobile.data.api.entity.mapper.ActivityApiEntityMapper;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.ActivityApiService;
import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.repository.datasource.event.StreamDataSource;
import com.shootr.mobile.domain.ActivityTimelineParameters;
import com.shootr.mobile.domain.ActivityType;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.Local;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class ServiceActivityDataSource implements ActivityDataSource{

    private final ActivityApiService activityApiService;
    private final ActivityApiEntityMapper activityApiEntityMapper;
    private final StreamDataSource localStreamDataSource;

    @Inject
    public ServiceActivityDataSource(ActivityApiService activityApiService,
      ActivityApiEntityMapper activityApiEntityMapper,
      @Local StreamDataSource localStreamDataSource) {
        this.activityApiService = activityApiService;
        this.activityApiEntityMapper = activityApiEntityMapper;
        this.localStreamDataSource = localStreamDataSource;
    }

    @Override public List<ActivityEntity> getActivityTimeline(ActivityTimelineParameters parameters, String locale) {
        try {
            List<ActivityApiEntity> activities = activityApiService.getActivityTimeline(parameters.getIncludedTypes(),
              parameters.getLimit(),
              parameters.getSinceDate(),
              parameters.getMaxDate(),
              locale);
            storeEmbedStreams(activities);
            return filterSyncActivities(activityApiEntityMapper.transform(activities));
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public ActivityEntity getActivity(String activityId) {
        try {
            ActivityApiEntity activityApiEntity = activityApiService.getActivity(activityId);
            return activityApiEntityMapper.transform(activityApiEntity);
        } catch (ApiException | IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public void putActivities(List<ActivityEntity> activityEntities) {
        throw new IllegalArgumentException("method not implemented");
    }

    @Override public void deleteActivitiesWithShot(String idShot) {
        throw new IllegalArgumentException("No remote implementation");
    }

    private void storeEmbedStreams(List<ActivityApiEntity> activities) {
        for (ActivityApiEntity activity : activities) {
            StreamEntity stream = activity.getStream();
            boolean hasAssociatedStream = stream != null;
            if (hasAssociatedStream) {
                localStreamDataSource.putStream(stream);
            }
        }
    }

    private List<ActivityEntity> filterSyncActivities(List<ActivityEntity> activityEntities) {
        List<ActivityEntity> filtered = new ArrayList<>();
        for (ActivityEntity activityEntity : activityEntities) {
            if (!isHiddenActivity(activityEntity)) {
                filtered.add(activityEntity);
            }
        }
        return filtered;
    }

    private boolean isHiddenActivity(ActivityEntity activityEntity) {
        String type = activityEntity.getType();
        return !Arrays.asList(ActivityType.TYPES_ACTIVITY_SHOWN).contains(type);
    }

}
