package com.shootr.android.domain.service.shot;

import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimeline;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.StreamTimelineParameters;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.TimelineSynchronizationRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class ShootrTimelineService {

    private final SessionRepository sessionRepository;
    private final ShotRepository remoteShotRepository;
    private final ActivityRepository localActivityRepository;
    private final ActivityRepository remoteActivityRepository;
    private final TimelineSynchronizationRepository timelineSynchronizationRepository;

    @Inject
    public ShootrTimelineService(SessionRepository sessionRepository, @Remote ShotRepository remoteShotRepository,
      @Local ActivityRepository localActivityRepository, @Remote ActivityRepository remoteActivityRepository,
      TimelineSynchronizationRepository timelineSynchronizationRepository) {
        this.sessionRepository = sessionRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.localActivityRepository = localActivityRepository;
        this.remoteActivityRepository = remoteActivityRepository;
        this.timelineSynchronizationRepository = timelineSynchronizationRepository;
    }

    public ActivityTimeline refreshTimelinesForActivity() {
        List<Activity> activities = refreshActivityShots();

        refreshStreamShots(sessionRepository.getCurrentUser().getIdWatchingStream());

        return buildSortedActivityTimeline(activities);
    }

    private List<Activity> refreshActivityShots() {
        Long activityRefreshDateSince = timelineSynchronizationRepository.getActivityTimelineRefreshDate();

        ActivityTimelineParameters activityTimelineParameters = ActivityTimelineParameters.builder() //
          .since(activityRefreshDateSince) //
          .build();

        if(localActivityRepository.getActivityTimeline(activityTimelineParameters).isEmpty()){
            activityTimelineParameters.excludeHiddenTypes();
        }

        return remoteActivityRepository.getActivityTimeline(activityTimelineParameters);
    }

    public Timeline refreshTimelinesForStream(String idStream) {
        List<Shot> shotsForStream = refreshStreamShots(idStream);

        refreshActivityShots();

        return buildSortedTimeline(shotsForStream);
    }

    private List<Shot> refreshStreamShots(String idStream) {
        List<Shot> newShots = new ArrayList<>();
        if (idStream != null) {
            Long streamRefreshDateSince = timelineSynchronizationRepository.getStreamTimelineRefreshDate(idStream);

            StreamTimelineParameters streamTimelineParameters = StreamTimelineParameters.builder() //
              .forStream(idStream) //
              .since(streamRefreshDateSince) //
              .build();

            newShots = remoteShotRepository.getShotsForStreamTimeline(streamTimelineParameters);
            if (!newShots.isEmpty()) {
                long lastShotDate = newShots.get(0).getPublishDate().getTime();
                timelineSynchronizationRepository.setStreamTimelineRefreshDate(idStream, lastShotDate);
            }
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

}
