package com.shootr.mobile.domain.service.shot;

import com.shootr.mobile.domain.Activity;
import com.shootr.mobile.domain.ActivityTimeline;
import com.shootr.mobile.domain.repository.Local;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class ShootrTimelineService {

    private final com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository;
    private final com.shootr.mobile.domain.repository.ActivityRepository localActivityRepository;
    private final com.shootr.mobile.domain.repository.ActivityRepository remoteActivityRepository;
    private final com.shootr.mobile.domain.repository.TimelineSynchronizationRepository
      timelineSynchronizationRepository;

    @Inject
    public ShootrTimelineService(@com.shootr.mobile.domain.repository.Remote
    com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository,
      @Local com.shootr.mobile.domain.repository.ActivityRepository localActivityRepository, @com.shootr.mobile.domain.repository.Remote
    com.shootr.mobile.domain.repository.ActivityRepository remoteActivityRepository,
      com.shootr.mobile.domain.repository.TimelineSynchronizationRepository timelineSynchronizationRepository) {
        this.remoteShotRepository = remoteShotRepository;
        this.localActivityRepository = localActivityRepository;
        this.remoteActivityRepository = remoteActivityRepository;
        this.timelineSynchronizationRepository = timelineSynchronizationRepository;
    }

    public ActivityTimeline refreshTimelinesForActivity() {
        List<Activity> activities = refreshActivityShots();
        return buildSortedActivityTimeline(activities);
    }

    private List<Activity> refreshActivityShots() {
        Long activityRefreshDateSince = timelineSynchronizationRepository.getActivityTimelineRefreshDate();

        com.shootr.mobile.domain.ActivityTimelineParameters
          activityTimelineParameters = com.shootr.mobile.domain.ActivityTimelineParameters.builder() //
          .since(activityRefreshDateSince) //
          .build();

        if(localActivityRepository.getActivityTimeline(activityTimelineParameters).isEmpty()){
            activityTimelineParameters.excludeHiddenTypes();
        }

        return remoteActivityRepository.getActivityTimeline(activityTimelineParameters);
    }

    public com.shootr.mobile.domain.Timeline refreshTimelinesForStream(String idStream) {
        List<com.shootr.mobile.domain.Shot> shotsForStream = refreshStreamShots(idStream);
        return buildSortedTimeline(shotsForStream);
    }

    private List<com.shootr.mobile.domain.Shot> refreshStreamShots(String idStream) {
        Long streamRefreshDateSince = timelineSynchronizationRepository.getStreamTimelineRefreshDate(idStream);

        com.shootr.mobile.domain.StreamTimelineParameters
          streamTimelineParameters = com.shootr.mobile.domain.StreamTimelineParameters.builder() //
          .forStream(idStream) //
          .since(streamRefreshDateSince) //
          .build();

        List<com.shootr.mobile.domain.Shot> newShots = remoteShotRepository.getShotsForStreamTimeline(streamTimelineParameters);
        if (!newShots.isEmpty()) {
            long lastShotDate = newShots.get(0).getPublishDate().getTime();
            timelineSynchronizationRepository.setStreamTimelineRefreshDate(idStream, lastShotDate);
        }
        return newShots;
    }

    private com.shootr.mobile.domain.Timeline buildSortedTimeline(List<com.shootr.mobile.domain.Shot> shots) {
        com.shootr.mobile.domain.Timeline timeline = new com.shootr.mobile.domain.Timeline();
        timeline.setShots(sortShotsByPublishDate(shots));
        return timeline;
    }

    private ActivityTimeline buildSortedActivityTimeline(List<Activity> activities) {
        ActivityTimeline timeline = new ActivityTimeline();
        timeline.setActivities(sortActivitiesByPublishDate(activities));
        return timeline;
    }

    private List<com.shootr.mobile.domain.Shot> sortShotsByPublishDate(List<com.shootr.mobile.domain.Shot> remoteShots) {
        Collections.sort(remoteShots, new com.shootr.mobile.domain.Shot.NewerAboveComparator());
        return remoteShots;
    }

    private List<Activity> sortActivitiesByPublishDate(List<Activity> remoteActivities) {
        Collections.sort(remoteActivities, new Activity.NewerAboveComparator());
        return remoteActivities;
    }

    public com.shootr.mobile.domain.Timeline refreshHoldingTimelineForStream(String idStream, String idUser) {
        Long streamRefreshDateSince = timelineSynchronizationRepository.getStreamTimelineRefreshDate(idStream);

        com.shootr.mobile.domain.StreamTimelineParameters
          streamTimelineParameters = com.shootr.mobile.domain.StreamTimelineParameters.builder() //
          .forStream(idStream) //
          .forUser(idUser) //
          .since(streamRefreshDateSince) //
          .build();

        List<com.shootr.mobile.domain.Shot> newShots = remoteShotRepository.getUserShotsForStreamTimeline(streamTimelineParameters);
        if (!newShots.isEmpty()) {
            long lastShotDate = newShots.get(0).getPublishDate().getTime();
            timelineSynchronizationRepository.setStreamTimelineRefreshDate(idStream, lastShotDate);
        }
        return buildSortedTimeline(newShots);
    }
}
