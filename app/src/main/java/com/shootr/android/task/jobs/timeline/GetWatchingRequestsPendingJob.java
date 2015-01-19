package com.shootr.android.task.jobs.timeline;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.db.manager.EventManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.task.events.timeline.WatchInfoBuilder;
import com.shootr.android.task.events.timeline.WatchingRequestPendingEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.WatchingRequestModel;
import com.shootr.android.ui.model.mappers.WatchingRequestModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class GetWatchingRequestsPendingJob extends ShootrBaseJob<WatchingRequestPendingEvent> {

    private static final long WATCHING_REQUEST_THRESHOLD_MILLIS = 15 * 60 * 1000;
    private static final int PRIORITY = 9;
    private WatchManager watchManager;
    private EventManager eventManager;
    private UserManager userManager;
    private SessionRepository sessionRepository;
    private WatchingRequestModelMapper watchingRequestModelMapper;


    @Inject public GetWatchingRequestsPendingJob(Application application, Bus bus, NetworkUtil networkUtil, WatchManager watchManager, EventManager eventManager, UserManager userManager,
      SessionRepository sessionRepository, WatchingRequestModelMapper watchingRequestModelMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.watchManager = watchManager;
        this.eventManager = eventManager;
        this.userManager = userManager;
        this.sessionRepository = sessionRepository;
        this.watchingRequestModelMapper = watchingRequestModelMapper;
    }

    @Override protected void run() throws SQLException, IOException {
        Set<WatchingRequestModel> watchingRequestModelResultCollection = new HashSet<>();

        WatchingRequestModel watchingRequestForMyTeam = getWatchingRequestForMyTeam();
        if (watchingRequestForMyTeam != null) {
            watchingRequestModelResultCollection.add(watchingRequestForMyTeam);
        }

        List<WatchingRequestModel> watchingRequestsIamNotWatching =
          getWatchingRequestsForEventThatMyFollowingAreWatchingAndIAmNot();

        if (watchingRequestsIamNotWatching != null) {
            watchingRequestModelResultCollection.addAll(watchingRequestsIamNotWatching);
        }

        postSuccessfulEvent(new WatchingRequestPendingEvent(new ArrayList<>(watchingRequestModelResultCollection)));
    }

    private List<WatchingRequestModel> getWatchingRequestsForEventThatMyFollowingAreWatchingAndIAmNot() {
        List<WatchEntity> watchesWhereIAmNot = getWatchesWhereIAmNot();
        List<Long> eventsIds = new ArrayList<>();
        List<Long> usersIds = new ArrayList<>();
        List<WatchingRequestModel> watchingRequestModels = new ArrayList<>();
        if(!watchesWhereIAmNot.isEmpty()){
            for (WatchEntity watchEntity : watchesWhereIAmNot) {
                eventsIds.add(watchEntity.getIdEvent());
                usersIds.add(watchEntity.getIdUser());
            }
            List<EventEntity> eventsByIds = eventManager.getEventsByIds(eventsIds);
            List<EventEntity> events = getRightEventsByTime(eventsByIds);
            List<UserEntity> usersByIds = userManager.getUsersByIds(usersIds);

            WatchInfoBuilder watchInfoBuilder = new WatchInfoBuilder();
            watchInfoBuilder.setWatches(watchesWhereIAmNot);
            watchInfoBuilder.provideEvents(events);
            watchInfoBuilder.provideUsers(usersByIds);

            Map<EventEntity, Collection<UserEntity>> mapWatchInfo = watchInfoBuilder.build();
            List<UserEntity> userEntities;
            for(EventEntity event:mapWatchInfo.keySet()){
                userEntities = new ArrayList<>(mapWatchInfo.get(event));
                WatchingRequestModel watchingRequestModel =  watchingRequestModelMapper.toWatchingRequestModel(event,userEntities);
                watchingRequestModels.add(watchingRequestModel);
            }
        }

        return watchingRequestModels;
    }

    private List<EventEntity> getRightEventsByTime(List<EventEntity> events) {
        List<EventEntity> eventsToReturn = new ArrayList<>();
        for(EventEntity m:events){
            if(isInWatchingRequestThreshold(m)){
                eventsToReturn.add(m);
            }
        }
        return eventsToReturn;
    }


    private List<WatchEntity> getWatchesWhereIAmNot() {
        return watchManager.getWatchesWhereUserNot(sessionRepository.getCurrentUserId());
    }

    private WatchingRequestModel getWatchingRequestForMyTeam() {
        EventEntity myTeamNextEvent = getMyTeamEvent();
        if (myTeamNextEvent != null && isInWatchingRequestThreshold(myTeamNextEvent) && isCurrentUserAnswerPending(myTeamNextEvent)) {
            List<UserEntity> usersWatchingMyTeamNextEvent = getUsersWatchingEvent(myTeamNextEvent);
            return watchingRequestModelMapper.toWatchingRequestModel(myTeamNextEvent, usersWatchingMyTeamNextEvent);
        }
        return null;
    }

    private boolean isCurrentUserAnswerPending(EventEntity myTeamNextEvent) {
        WatchEntity watchFromUserForItsEvent = watchManager.getWatchByEventAndUser(myTeamNextEvent.getIdEvent(),
          sessionRepository.getCurrentUserId());
        return watchFromUserForItsEvent == null;
    }

    private boolean isInWatchingRequestThreshold(EventEntity eventEntity) {
        long currentTime = System.currentTimeMillis();
        long eventTime = eventEntity.getBeginDate().getTime();
        long eventTimethreshold = eventTime - WATCHING_REQUEST_THRESHOLD_MILLIS;
        return currentTime >= eventTimethreshold;
    }

    private List<UserEntity> getUsersWatchingEvent(EventEntity eventEntity) {
        List<WatchEntity> watches = watchManager.getWatchesByEvent(eventEntity.getIdEvent());
        List<Long> userIds = new ArrayList<>();
        for(WatchEntity watch : watches){
            userIds.add(watch.getIdUser());
        }
        return userManager.getUsersByIds(userIds);
    }

    private EventEntity getMyTeamEvent() {
        return eventManager.getNextEventFromTeam(sessionRepository.getCurrentUser().getFavoriteTeamId());
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
