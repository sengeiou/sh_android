package com.shootr.android.data.repository.datasource.activity;

import com.shootr.android.data.api.entity.ActivityApiEntity;
import com.shootr.android.data.api.entity.mapper.ActivityApiEntityMapper;
import com.shootr.android.data.api.service.ActivityApiService;
import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.ActivityType;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.WatchUpdateRequest;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

public class ServiceActivityDataSource implements ActivityDataSource{

    private final ActivityApiService activityApiService;
    private final ActivityApiEntityMapper activityApiEntityMapper;
    private final BusPublisher busPublisher;
    private final SessionRepository sessionRepository;
    private final StreamDataSource localStreamDataSource;
    private long lastTriggerDate;

    @Inject
    public ServiceActivityDataSource(ActivityApiService activityApiService,
      ActivityApiEntityMapper activityApiEntityMapper,
      BusPublisher busPublisher,
      SessionRepository sessionRepository,
      @Local StreamDataSource localStreamDataSource) {
        this.activityApiService = activityApiService;
        this.activityApiEntityMapper = activityApiEntityMapper;
        this.busPublisher = busPublisher;
        this.sessionRepository = sessionRepository;
        this.localStreamDataSource = localStreamDataSource;
    }

    @Override public List<ActivityEntity> getActivityTimeline(ActivityTimelineParameters parameters) {
        try {
            List<ActivityApiEntity> activities = activityApiService.getActivityTimeline(parameters.getIncludedTypes(), parameters.getLimit(), parameters.getSinceDate(), parameters.getMaxDate());
            storeEmbedEvents(activities);
            return filterSyncActivities(activityApiEntityMapper.transform(activities));
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public ActivityEntity getActivity(String activityId) {
        try {
            ActivityApiEntity activityApiEntity = activityApiService.getActivity(activityId);
            return activityApiEntityMapper.transform(activityApiEntity);
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public void putActivities(List<ActivityEntity> activityEntities) {
        throw new IllegalArgumentException("method not implemented");
    }

    private void storeEmbedEvents(List<ActivityApiEntity> activities) {
        for (ActivityApiEntity activity : activities) {
            StreamEntity event = activity.getStream();
            boolean hasAssociatedEvent = event != null;
            if (hasAssociatedEvent) {
                localStreamDataSource.putStream(event);
            }
        }
    }

    private List<ActivityEntity> filterSyncActivities(List<ActivityEntity> activityEntities) {
        notifySyncTrigger(activityEntities);
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

    private void notifySyncTrigger(List<ActivityEntity> newActivities) {
        String currentUserId = sessionRepository.getCurrentUserId();
        for (ActivityEntity newActivity : newActivities) {
            if (isSyncTriggerActivity(newActivity)
              && isNewerThanLastTrigger(newActivity)
              && !newActivity.getIdUser().equals(currentUserId)) {
                busPublisher.post(new WatchUpdateRequest.Stream());
                lastTriggerDate = newActivity.getBirth().getTime();
                break;
            }
        }
    }

    private boolean isSyncTriggerActivity(ActivityEntity newActivity) {
        String type = newActivity.getType();
        return Arrays.asList(ActivityType.TYPES_SYNC_TRIGGER).contains(type);
    }

    private boolean isNewerThanLastTrigger(ActivityEntity newActivity) {
        return newActivity.getBirth().getTime() > lastTriggerDate;
    }
}
