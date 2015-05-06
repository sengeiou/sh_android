package com.shootr.android.domain.service.shot;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.TimelineParameters;
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

    private final SessionRepository sessionRepository;
    private final EventRepository localEventRepository;
    private final UserRepository localUserRepository;
    private final ShotRepository remoteShotRepository;
    private final TimelineSynchronizationRepository timelineSynchronizationRepository;

    @Inject public ShootrTimelineService(SessionRepository sessionRepository, @Local EventRepository localEventRepository, @Local UserRepository localUserRepository, @Remote ShotRepository remoteShotRepository, TimelineSynchronizationRepository timelineSynchronizationRepository) {
        this.sessionRepository = sessionRepository;
        this.localEventRepository = localEventRepository;
        this.localUserRepository = localUserRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.timelineSynchronizationRepository = timelineSynchronizationRepository;
    }

    public List<Timeline> refreshTimelines() {
        List<Timeline> resultTimelines = new ArrayList<>();

        Event visibleEvent = getVisibleEvent();
        if (visibleEvent != null) {
            Long eventRefreshDateSince = timelineSynchronizationRepository.getEventTimelineRefreshDate(visibleEvent.getId());

            TimelineParameters eventTimelineParameters = TimelineParameters.builder() //
                    .forUsers(getPeopleIds(), sessionRepository.getCurrentUserId()) //
                    .forEvent(visibleEvent) //
                    .since(eventRefreshDateSince) //
                    .includeSyncTriggers(true)
                    .build();
            List<Shot> shotsForEvent = remoteShotRepository.getShotsForTimeline(eventTimelineParameters);
            resultTimelines.add(buildSortedTimeline(eventTimelineParameters, shotsForEvent));
        }

        Long activityRefreshDateSince = timelineSynchronizationRepository.getActivityTimelineRefreshDate();
        TimelineParameters activityTimelineParameters = TimelineParameters.builder() //
                .forUsers(getPeopleIds(), sessionRepository.getCurrentUserId()) //
                .forActivity() //
                .since(activityRefreshDateSince) //
                .includeSyncTriggers(false)
                .build();
        List<Shot> shotsForActivity = remoteShotRepository.getShotsForTimeline(activityTimelineParameters);
        resultTimelines.add(buildSortedTimeline(activityTimelineParameters, shotsForActivity));

        return resultTimelines;
    }

    private Timeline buildSortedTimeline(TimelineParameters timelineParameters, List<Shot> shots) {
        Timeline timeline = new Timeline();
        timeline.setShots(sortByPublishDate(shots));
        timeline.setParameters(timelineParameters);
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

    private Event getVisibleEvent() {
        String visibleEventId = sessionRepository.getCurrentUser().getVisibleEventId();
        if (visibleEventId != null) {
            return localEventRepository.getEventById(visibleEventId);
        }
        return null;
    }

}
