package com.shootr.android.domain.service.shot;

import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.EventTimelineParameters;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.EventRepository;
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
    private final EventRepository localEventRepository;
    private final UserRepository localUserRepository;
    private final ShotRepository remoteShotRepository;
    private final ShotRepository localShotRepository;
    private final TimelineSynchronizationRepository timelineSynchronizationRepository;

    @Inject
    public ShootrTimelineService(SessionRepository sessionRepository,
      @Local EventRepository localEventRepository,
      @Local UserRepository localUserRepository,
      @Remote ShotRepository remoteShotRepository,
      @Local ShotRepository localShotRepository,
      TimelineSynchronizationRepository timelineSynchronizationRepository) {
        this.sessionRepository = sessionRepository;
        this.localEventRepository = localEventRepository;
        this.localUserRepository = localUserRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.localShotRepository = localShotRepository;
        this.timelineSynchronizationRepository = timelineSynchronizationRepository;
    }

    public Timeline refreshTimelinesForActivity() {
        List<Shot> shotsForActivity = refreshActivityShots();

        refreshWatchingEventShots();

        return buildSortedTimeline(shotsForActivity);
    }

    private List<Shot> refreshActivityShots() {
        Long activityRefreshDateSince = timelineSynchronizationRepository.getActivityTimelineRefreshDate();
        ActivityTimelineParameters activityTimelineParameters = ActivityTimelineParameters.builder() //
          .forUsers(getPeopleIds(), sessionRepository.getCurrentUserId()) //
          .since(activityRefreshDateSince) //
          .build();
        return remoteShotRepository.getShotsForActivityTimeline(activityTimelineParameters);
    }

    public Timeline refreshTimelinesForWatchingEvent() {
        List<Shot> shotsForEvent = refreshWatchingEventShots();

        refreshActivityShots();

        return buildSortedTimeline(shotsForEvent);
    }

    private List<Shot> refreshWatchingEventShots() {
        Event watchingEvent = getWatchingEvent();
        if (watchingEvent != null) {
            return refreshEventShots(watchingEvent);
        }
        return null;
    }

    private List<Shot> refreshEventShots(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Can't refresh null event");
        }

        Long eventRefreshDateSince = timelineSynchronizationRepository.getEventTimelineRefreshDate(event.getId());

        EventTimelineParameters eventTimelineParameters = EventTimelineParameters.builder() //
          .currentUser(sessionRepository.getCurrentUserId()) //
          .forEvent(event) //
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
        timeline.setShots(sortByPublishDate(shots));
        return timeline;
    }

    private List<Shot> sortByPublishDate(List<Shot> remoteShots) {
        Collections.sort(remoteShots, new Shot.NewerAboveComparator());
        return remoteShots;
    }

    private List<String> getPeopleIds() {
        List<String> ids = new ArrayList<>();
        for (User user : localUserRepository.getPeople()) {
            ids.add(user.getIdUser());
        }
        return ids;
    }

    private Event getWatchingEvent() {
        String currentUserId = sessionRepository.getCurrentUserId();
        User currentUser = localUserRepository.getUserById(currentUserId);
        String watchingEventId = currentUser.getIdWatchingEvent();
        if (watchingEventId != null) {
            return localEventRepository.getEventById(watchingEventId);
        }
        return null;
    }
}
