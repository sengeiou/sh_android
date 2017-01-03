package com.shootr.mobile.domain.service.shot;

import com.shootr.mobile.domain.model.activity.Activity;
import com.shootr.mobile.domain.model.activity.ActivityTimeline;
import com.shootr.mobile.domain.model.activity.ActivityTimelineParameters;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageTimeline;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.TimelineSynchronizationRepository;
import com.shootr.mobile.domain.repository.privateMessage.PrivateMessageRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class ShootrTimelineService {

  private final ExternalShotRepository remoteShotRepository;
  private final ActivityRepository localActivityRepository;
  private final ActivityRepository remoteActivityRepository;
  private final TimelineSynchronizationRepository timelineSynchronizationRepository;
  private final PrivateMessageRepository remotePrivateMessageRepository;


  @Inject public ShootrTimelineService(ExternalShotRepository remoteShotRepository,
      @Local ActivityRepository localActivityRepository,
      @Remote ActivityRepository remoteActivityRepository,
      TimelineSynchronizationRepository timelineSynchronizationRepository,
      @Remote PrivateMessageRepository remotePrivateMessageRepository) {
    this.remoteShotRepository = remoteShotRepository;
    this.localActivityRepository = localActivityRepository;
    this.remoteActivityRepository = remoteActivityRepository;
    this.timelineSynchronizationRepository = timelineSynchronizationRepository;
    this.remotePrivateMessageRepository = remotePrivateMessageRepository;
  }

  public ActivityTimeline refreshTimelinesForActivity(String language,
      Boolean isUserActivityTimeline) {
    List<Activity> activities = refreshActivityShots(language, isUserActivityTimeline);
    return buildSortedActivityTimeline(activities);
  }

  private List<Activity> refreshActivityShots(String language, Boolean isUserActivityTimeline) {
    Long activityRefreshDateSince;
    activityRefreshDateSince =
        timelineSynchronizationRepository.getActivityTimelineRefreshDate(isUserActivityTimeline);

    ActivityTimelineParameters activityTimelineParameters = ActivityTimelineParameters.builder() //
        .since(activityRefreshDateSince) //
        .isMeTimeline(isUserActivityTimeline) //
        .build();

    activityTimelineParameters.excludeHiddenTypes();

    if (localActivityRepository.getActivityTimeline(activityTimelineParameters, language)
        .isEmpty()) {
      activityTimelineParameters.excludeHiddenTypes();
    }

    return remoteActivityRepository.getActivityTimeline(activityTimelineParameters, language);
  }

  public Timeline refreshTimelinesForStream(String idStream, Boolean isRealTime) {
    List<Shot> shotsForStream = refreshStreamShots(idStream, isRealTime);
    return buildSortedTimeline(shotsForStream);
  }

  private List<Shot> refreshStreamShots(String idStream, Boolean isRealTime) {
    Long streamRefreshDateSince =
        timelineSynchronizationRepository.getStreamTimelineRefreshDate(idStream);

    StreamTimelineParameters streamTimelineParameters = StreamTimelineParameters.builder() //
        .forStream(idStream) //
        .since(streamRefreshDateSince) //
        .realTime(isRealTime) //
        .build();

    List<Shot> newShots = remoteShotRepository.getShotsForStreamTimeline(streamTimelineParameters);
    if (!newShots.isEmpty()) {
      long lastShotDate = newShots.get(0).getPublishDate().getTime();
      timelineSynchronizationRepository.setStreamTimelineRefreshDate(idStream, lastShotDate);
    }
    return newShots;
  }

  public PrivateMessageTimeline refreshTimelinesForChannel(String idChannel, String idTargetUser, Long lastRefresh) {
    List<PrivateMessage> shotsForStream = refreshChannelMessages(idChannel, idTargetUser, lastRefresh);
    return buildSortedPrivateMessageTimeline(shotsForStream);
  }

  private List<PrivateMessage> refreshChannelMessages(String idChannel, String idTargetUser,
      Long lastRefresh) {
    PrivateMessageTimeline timeline =
        remotePrivateMessageRepository.refreshPrivateMessageTimeline(idTargetUser, lastRefresh);
    return timeline.getPrivateMessages();
  }

  private PrivateMessageTimeline buildSortedPrivateMessageTimeline(List<PrivateMessage> privateMessages) {
    PrivateMessageTimeline timeline = new PrivateMessageTimeline();
    timeline.setPrivateMessages(sortPrivateMessagesByPublishDate(privateMessages));
    return timeline;
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

  private List<PrivateMessage> sortPrivateMessagesByPublishDate(List<PrivateMessage> privateMessages) {
    Collections.sort(privateMessages, new PrivateMessage.NewerAboveComparator());
    return privateMessages;
  }

  private List<Activity> sortActivitiesByPublishDate(List<Activity> remoteActivities) {
    Collections.sort(remoteActivities, new Activity.NewerAboveComparator());
    return remoteActivities;
  }

  public Timeline refreshHoldingTimelineForStream(String idStream, String idUser,
      Boolean isRealTime) {
    Long streamRefreshDateSince =
        timelineSynchronizationRepository.getStreamTimelineRefreshDate(idStream);

    StreamTimelineParameters streamTimelineParameters = StreamTimelineParameters.builder() //
        .forStream(idStream) //
        .forUser(idUser) //
        .since(streamRefreshDateSince) //
        .realTime(isRealTime) //
        .build();

    List<Shot> newShots =
        remoteShotRepository.getUserShotsForStreamTimeline(streamTimelineParameters);
    if (!newShots.isEmpty()) {
      long lastShotDate = newShots.get(0).getPublishDate().getTime();
      timelineSynchronizationRepository.setStreamTimelineRefreshDate(idStream, lastShotDate);
    }
    return buildSortedTimeline(newShots);
  }
}
