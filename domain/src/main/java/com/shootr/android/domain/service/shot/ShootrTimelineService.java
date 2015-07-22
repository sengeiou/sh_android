package com.shootr.android.domain.service.shot;

import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimeline;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.EventTimelineParameters;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.TimelineSynchronizationRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class ShootrTimelineService {

    public static final Integer MAXIMUM_NICE_SHOTS_WHEN_TIMELINE_EMPTY = 2;
    public static final Integer MAXIMUM_NICE_SHOTS_WHEN_TIMELINE_HAS_SHOTS_ALREADY = null;

    private final SessionRepository sessionRepository;
    private final StreamRepository localStreamRepository;
    private final UserRepository localUserRepository;
    private final ShotRepository remoteShotRepository;
    private final ActivityRepository localActivityRepository;
    private final ActivityRepository remoteActivityRepository;
    private final ShotRepository localShotRepository;
    private final TimelineSynchronizationRepository timelineSynchronizationRepository;

    @Inject
    public ShootrTimelineService(SessionRepository sessionRepository, @Local StreamRepository localStreamRepository,
      @Local UserRepository localUserRepository, @Remote ShotRepository remoteShotRepository,
      @Local ActivityRepository localActivityRepository, @Remote ActivityRepository remoteActivityRepository,
      @Local ShotRepository localShotRepository, TimelineSynchronizationRepository timelineSynchronizationRepository) {
        this.sessionRepository = sessionRepository;
        this.localStreamRepository = localStreamRepository;
        this.localUserRepository = localUserRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.localActivityRepository = localActivityRepository;
        this.remoteActivityRepository = remoteActivityRepository;
        this.localShotRepository = localShotRepository;
        this.timelineSynchronizationRepository = timelineSynchronizationRepository;
    }

    public ActivityTimeline refreshTimelinesForActivity() {
        List<Activity> activities = refreshActivityShots();

        refreshWatchingEventShots();

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

    public Timeline refreshTimelinesForWatchingEvent() {
        List<Shot> shotsForEvent = refreshWatchingEventShots();

        refreshActivityShots();

        return buildSortedTimeline(shotsForEvent);
    }

    private List<Shot> refreshWatchingEventShots() {
        Stream watchingStream = getWatchingEvent();
        if (watchingStream != null) {
            return refreshEventShots(watchingStream);
        }
        return null;
    }

    private List<Shot> refreshEventShots(Stream stream) {
        if (stream == null) {
            throw new IllegalArgumentException("Can't refresh null event");
        }

        Long eventRefreshDateSince = timelineSynchronizationRepository.getEventTimelineRefreshDate(stream.getId());

        EventTimelineParameters eventTimelineParameters = EventTimelineParameters.builder() //
          .forEvent(stream) //
          .niceShots(MAXIMUM_NICE_SHOTS_WHEN_TIMELINE_EMPTY) //
          .since(eventRefreshDateSince) //
          .build();

        List<Shot> localShots = localShotRepository.getShotsForEventTimeline(eventTimelineParameters);
        if (localShots.isEmpty()) {
            eventTimelineParameters.setMaxNiceShotsIncluded(MAXIMUM_NICE_SHOTS_WHEN_TIMELINE_HAS_SHOTS_ALREADY);
        }
        return remoteShotRepository.getShotsForEventTimeline(eventTimelineParameters);
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

    private List<String> getPeopleIds() {
        List<String> ids = new ArrayList<>();
        for (User user : localUserRepository.getPeople()) {
            ids.add(user.getIdUser());
        }
        return ids;
    }

    private Stream getWatchingEvent() {
        String currentUserId = sessionRepository.getCurrentUserId();
        User currentUser = localUserRepository.getUserById(currentUserId);
        String watchingEventId = currentUser.getIdWatchingEvent();
        if (watchingEventId != null) {
            return localStreamRepository.getStreamById(watchingEventId);
        }
        return null;
    }
}
