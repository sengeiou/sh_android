package com.shootr.mobile.data.repository.datasource.activity;

import com.shootr.mobile.data.api.entity.ActivityApiEntity;
import com.shootr.mobile.data.api.entity.mapper.ActivityApiEntityMapper;
import com.shootr.mobile.data.api.entity.mapper.ShotApiEntityMapper;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.ActivityApiService;
import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.data.repository.datasource.shot.ShotDataSource;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.model.activity.ActivityTimelineParameters;
import com.shootr.mobile.domain.model.activity.ActivityType;
import com.shootr.mobile.domain.repository.Local;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

public class ServiceActivityDataSource implements ActivityDataSource {

    private final ActivityApiService activityApiService;
    private final ActivityApiEntityMapper activityApiEntityMapper;
    private final StreamDataSource localStreamDataSource;
    private final ShotDataSource localShotDatasource;
    private final ShotApiEntityMapper shotApiEntityMapper;

    @Inject public ServiceActivityDataSource(ActivityApiService activityApiService,
        ActivityApiEntityMapper activityApiEntityMapper,
        @Local StreamDataSource localStreamDataSource, @Local ShotDataSource localShotDatasource,
        ShotApiEntityMapper shotApiEntityMapper) {
        this.activityApiService = activityApiService;
        this.activityApiEntityMapper = activityApiEntityMapper;
        this.localStreamDataSource = localStreamDataSource;
        this.localShotDatasource = localShotDatasource;
        this.shotApiEntityMapper = shotApiEntityMapper;
    }

    @Override public List<ActivityEntity> getActivityTimeline(ActivityTimelineParameters parameters, String locale) {
        try {
            List<ActivityApiEntity> activities = activityApiService.getActivityTimeline(parameters.getIncludedTypes(),
              parameters.getStreamTypes(),
              parameters.getShotTypes(),
              parameters.getLimit(),
              parameters.getSinceDate(),
              parameters.getMaxDate(),
              locale,
              parameters.getActivityFilter());
            storeEmbedStreams(activities);
            for (ActivityApiEntity activity : activities) {
                ShotEntity shot = shotApiEntityMapper.transform(activity.getShot());
                boolean hasEmbedShot = shot != null;
                if (hasEmbedShot) {
                    localShotDatasource.putShot(shot);
                }
            }
            return filterSyncActivities(activityApiEntityMapper.transform(activities));
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public ActivityEntity getActivity(String activityId) {
        try {
            ActivityApiEntity activityApiEntity = activityApiService.getActivity(activityId);
            return activityApiEntityMapper.transform(activityApiEntity);
        } catch (ApiException | IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public void putActivities(ActivityTimelineParameters parameters, List<ActivityEntity> activityEntities) {
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
