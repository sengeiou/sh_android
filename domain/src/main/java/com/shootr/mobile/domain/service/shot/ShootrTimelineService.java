package com.shootr.mobile.domain.service.shot;

import com.shootr.mobile.domain.Activity;
import com.shootr.mobile.domain.ActivityTimeline;
import com.shootr.mobile.domain.ActivityTimelineParameters;
import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.StreamTimelineParameters;
import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.domain.repository.TimelineSynchronizationRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class ShootrTimelineService {

    private final ShotRepository remoteShotRepository;
    private final ActivityRepository localActivityRepository;
    private final ActivityRepository remoteActivityRepository;
    private final TimelineSynchronizationRepository timelineSynchronizationRepository;

    @Inject
    public ShootrTimelineService(@Remote ShotRepository remoteShotRepository,
      @Local ActivityRepository localActivityRepository,
      @Remote ActivityRepository remoteActivityRepository,
      TimelineSynchronizationRepository timelineSynchronizationRepository) {
        this.remoteShotRepository = remoteShotRepository;
        this.localActivityRepository = localActivityRepository;
        this.remoteActivityRepository = remoteActivityRepository;
        this.timelineSynchronizationRepository = timelineSynchronizationRepository;
    }

    public ActivityTimeline refreshTimelinesForActivity(String language) {
        List<Activity> activities = refreshActivityShots(language);
        return buildSortedActivityTimeline(activities);
    }

    private List<Activity> refreshActivityShots(String language) {
        Long activityRefreshDateSince = timelineSynchronizationRepository.getActivityTimelineRefreshDate();

        ActivityTimelineParameters activityTimelineParameters = ActivityTimelineParameters.builder() //
          .since(activityRefreshDateSince) //
          .build();

        if (localActivityRepository.getActivityTimeline(activityTimelineParameters, language).isEmpty()) {
            activityTimelineParameters.excludeHiddenTypes();
        }

        return remoteActivityRepository.getActivityTimeline(activityTimelineParameters, language);
    }

    public Timeline refreshTimelinesForStream(String idStream) {
        List<Shot> shotsForStream = refreshStreamShots(idStream);
        return buildSortedTimeline(shotsForStream);
    }

    private List<Shot> refreshStreamShots(String idStream) {
        Long streamRefreshDateSince = timelineSynchronizationRepository.getStreamTimelineRefreshDate(idStream);

        StreamTimelineParameters streamTimelineParameters = StreamTimelineParameters.builder() //
          .forStream(idStream) //
          .since(streamRefreshDateSince) //
          .build();

        List<Shot> newShots = remoteShotRepository.getShotsForStreamTimeline(streamTimelineParameters);
        if (!newShots.isEmpty()) {
            long lastShotDate = newShots.get(0).getPublishDate().getTime();
            timelineSynchronizationRepository.setStreamTimelineRefreshDate(idStream, lastShotDate);
        }
        return newShots;
    }

    private Timeline buildSortedTimeline(List<Shot> shots) {
        Timeline timeline = new Timeline();
        timeline.setShots(sortShotsByPublishDate(shots));
        return timeline;
    }

    private ActivityTimeline buildSortedActivityTimeline(List<Activity> activities) {
        ActivityTimeline timeline = new ActivityTimeline();
        timeline.setActivities(sortActivitiesByPublishDate(activities));
        return timeline;
    }

    private List<Shot> sortShotsByPublishDate(List<Shot> remoteShots) {
        Collections.sort(remoteShots, new Shot.NewerAboveComparator());
        return remoteShots;
    }

    private List<Activity> sortActivitiesByPublishDate(List<Activity> remoteActivities) {
        Collections.sort(remoteActivities, new Activity.NewerAboveComparator());
        return remoteActivities;
    }

    public Timeline refreshHoldingTimelineForStream(String idStream, String idUser) {
        Long streamRefreshDateSince = timelineSynchronizationRepository.getStreamTimelineRefreshDate(idStream);

        StreamTimelineParameters streamTimelineParameters = StreamTimelineParameters.builder() //
          .forStream(idStream) //
          .forUser(idUser) //
          .since(streamRefreshDateSince) //
          .build();

        List<Shot> newShots = remoteShotRepository.getUserShotsForStreamTimeline(streamTimelineParameters);
        if (!newShots.isEmpty()) {
            long lastShotDate = newShots.get(0).getPublishDate().getTime();
            timelineSynchronizationRepository.setStreamTimelineRefreshDate(idStream, lastShotDate);
        }
        return buildSortedTimeline(newShots);
    }
}
