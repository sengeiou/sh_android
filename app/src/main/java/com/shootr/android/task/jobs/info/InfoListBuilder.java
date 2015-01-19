package com.shootr.android.task.jobs.info;

import android.support.v4.util.LongSparseArray;
import com.google.common.collect.TreeMultimap;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.domain.User;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.model.mappers.EventEntityModelMapper;
import com.shootr.android.ui.model.mappers.UserEntityWatchingModelMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;

public class InfoListBuilder {

    private TreeMultimap<EventModel, UserWatchingModel> map;

    private EventEntityModelMapper eventEntityModelMapper;
    private UserEntityWatchingModelMapper userWatchingModelMapper;

    private LongSparseArray<EventModel> eventModelCache = new LongSparseArray<>();

    private LongSparseArray<EventEntity> eventEntities = new LongSparseArray<>();
    private LongSparseArray<UserEntity> userEntities = new LongSparseArray<>();

    private List<Long> eventIdsFromWatches = new ArrayList<>();
    private List<Long> userIdsFromWatches = new ArrayList<>();

    private UserEntity currentUserEntity;

    private Set<WatchEntity> validWatches = new HashSet<>();
    private Set<WatchEntity> allWatches = new HashSet<>();
    private EventEntity myTeamNextEvent;

    private Comparator<? super EventModel> eventComparatorByDate = new Comparator<EventModel>() {
        @Override public int compare(EventModel event1, EventModel event2) {
            return eventEntities.get(event1.getIdEvent()).compareTo(eventEntities.get(event2.getIdEvent()));
        }
    };

    private Comparator<? super UserWatchingModel> userComparatorByNameAndCurrentUser = new Comparator<UserWatchingModel>() {
        @Override public int compare(UserWatchingModel user1, UserWatchingModel user2) {
            if (user1.getIdUser().equals(currentUserEntity.getIdUser())) {
                return -1;
            } else if (user2.getIdUser().equals(currentUserEntity.getIdUser())) {
                return 1;
            } else {
                return userEntities.get(user1.getIdUser()).compareTo(userEntities.get(user2.getIdUser()));
            }
        }
    };

    protected InfoListBuilder(User currentUser, EventEntityModelMapper eventEntityModelMapper,
      UserEntityWatchingModelMapper userWatchingModelMapper, UserEntityMapper userEntityMapper) {
        this.eventEntityModelMapper = eventEntityModelMapper;
        this.userWatchingModelMapper = userWatchingModelMapper;
        this.currentUserEntity = userEntityMapper.transform(currentUser);
        userEntities.put(currentUser.getIdUser(), this.currentUserEntity);
    }

    public void setWatches(List<WatchEntity> validWatches) {
        setWatchesAndExtractIds(validWatches);
    }

    public void provideUsers(List<UserEntity> users) {
        for (UserEntity userEntity : users) {
            userEntities.put(userEntity.getIdUser(), userEntity);
        }
    }

    public void provideEvents(List<EventEntity> events) {
        for (EventEntity eventEntity : events) {
            eventEntities.put(eventEntity.getIdEvent(), eventEntity);
        }
    }

    public void putMyTeamEvent(EventEntity myTeamNextEvent) {
        this.myTeamNextEvent = myTeamNextEvent;
        eventEntities.put(myTeamNextEvent.getIdEvent(), myTeamNextEvent);
    }

    public Map<EventModel, Collection<UserWatchingModel>> build() {
        map = TreeMultimap.create(eventComparatorByDate, userComparatorByNameAndCurrentUser);

        addMyTeamsNextEvent();

        buildMapFromWatchesAndEntities();

        fillResultWithCurrentUser();

        return map.asMap();
    }

    private void addMyTeamsNextEvent() {
        if (myTeamNextEvent != null) {
            WatchEntity meNotWatchingMyTeam = new WatchEntity();
            meNotWatchingMyTeam.setIdEvent(myTeamNextEvent.getIdEvent());
            meNotWatchingMyTeam.setIdUser(currentUserEntity.getIdUser());
            meNotWatchingMyTeam.setStatus(0L);
            if (!allWatches.contains(meNotWatchingMyTeam)) {
                validWatches.add(meNotWatchingMyTeam);
            }
        }
    }

    private void buildMapFromWatchesAndEntities() {
        for (WatchEntity watch : validWatches) {
            addWatchingToResult(watch);
        }
    }

    private void fillResultWithCurrentUser() {
        for (EventModel eventModel : map.keySet()) {
            NavigableSet<UserWatchingModel> userWatchingSet = map.get(eventModel);
            if(!containsCurrentUser(userWatchingSet)) {
                userWatchingSet.add(getCurrentUserNotWatching(eventModel.getIdEvent()));
            }
        }
    }

    private boolean containsCurrentUser(Set<UserWatchingModel> userWatchingModelSet) {
        for (UserWatchingModel user : userWatchingModelSet) {
            if (user.getIdUser().equals(currentUserEntity.getIdUser())) {
                return true;
            }
        }
        return false;
    }

    private UserWatchingModel getCurrentUserNotWatching(Long idEvent) {
        WatchEntity watchEntity = new WatchEntity();
        watchEntity.setIdUser(currentUserEntity.getIdUser());
        watchEntity.setStatus(0L);
        watchEntity.setIdEvent(idEvent);
        EventEntity eventEntity = eventEntities.get(idEvent);
        return getUserWatchingModelFromEntity(currentUserEntity, watchEntity, eventEntity);
    }

    private void addWatchingToResult(WatchEntity watchEntity) {
        EventEntity eventEntity = eventEntities.get(watchEntity.getIdEvent());
        UserEntity userEntity = userEntities.get(watchEntity.getIdUser());
        if (eventEntity != null && userEntity != null) {
            addWatchingPopulatedToResult(eventEntity, userEntity, watchEntity);
        }
    }

    private void addWatchingPopulatedToResult(EventEntity eventEntity, UserEntity userEntity, WatchEntity watchEntity) {
        EventModel eventModel = getEventModelFromEntity(eventEntity);
        UserWatchingModel userWatchingModel = getUserWatchingModelFromEntity(userEntity, watchEntity, eventEntity);
        map.put(eventModel, userWatchingModel);
    }

    private void setWatchesAndExtractIds(Collection<WatchEntity> watches) {
        this.allWatches.addAll(watches);
        List<WatchEntity> watchesVisible = removeWatchesWithNotVisibleEvents(watches);
        this.validWatches.addAll(watchesVisible);
        extractEventsAndUserIdsFromWatches(watchesVisible);
    }

    private List<WatchEntity> removeWatchesWithNotVisibleEvents(Collection<WatchEntity> watches) {
        List<Long> hiddenEventsIds = new ArrayList<>();
        for (WatchEntity watch : watches) {
            if (watch.getIdUser().equals(currentUserEntity.getIdUser()) && !watch.getVisible()) {
                hiddenEventsIds.add(watch.getIdEvent());
            }
        }
        List<WatchEntity> watchesVisible = new ArrayList<>();
        for (WatchEntity watch : watches) {
            if (!hiddenEventsIds.contains(watch.getIdEvent())) {
                watchesVisible.add(watch);
            }
        }
        return watchesVisible;
    }

    private void extractEventsAndUserIdsFromWatches(Collection<WatchEntity> watches) {
        for (WatchEntity watch : watches) {
            eventIdsFromWatches.add(watch.getIdEvent());
            userIdsFromWatches.add(watch.getIdUser());
        }
    }

    public List<Long> getUserIds() {
        return userIdsFromWatches;
    }

    public List<Long> getEventIds() {
        return eventIdsFromWatches;
    }

    public Set<WatchEntity> getValidWatches() {
        return validWatches;
    }

    private UserWatchingModel getUserWatchingModelFromEntity(UserEntity userEntity, WatchEntity watchEntity, EventEntity eventEntity) {
        //TODO Use some cache? maybe? One user can be watching one event but not watching another
        return userWatchingModelMapper.toUserWatchingModel(userEntity, watchEntity.getStatus() == 1, watchEntity.getPlace());
    }

    private EventModel getEventModelFromEntity(EventEntity eventEntity) {
        EventModel eventModel = eventModelCache.get(eventEntity.getIdEvent());
        if (eventModel == null) {
            eventModel = eventEntityModelMapper.toEventModel(eventEntity);
            eventModelCache.put(eventEntity.getIdEvent(), eventModel);
        }
        return eventModel;
    }
}