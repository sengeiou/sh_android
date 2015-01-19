package com.shootr.android.task.events.timeline;

import android.support.v4.util.LongSparseArray;
import com.google.common.collect.TreeMultimap;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.entity.WatchEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Deprecated
public class WatchInfoBuilder {

        private TreeMultimap<EventEntity, UserEntity> map;

        private LongSparseArray<EventEntity> eventEntities = new LongSparseArray<>();
        private LongSparseArray<UserEntity> userEntities = new LongSparseArray<>();

        private List<Long> eventIdsFromWatches = new ArrayList<>();
        private List<Long> userIdsFromWatches = new ArrayList<>();


        private Set<WatchEntity> watches = new HashSet<>();

        private Comparator<? super EventEntity> eventComparatorByDate = new Comparator<EventEntity>() {
            @Override public int compare(EventEntity event1, EventEntity event2) {
                return eventEntities.get(event1.getIdEvent()).compareTo(eventEntities.get(event2.getIdEvent()));
            }
        };

    private Comparator<? super UserEntity> userComparatorByNameAndCurrentUser = new Comparator<UserEntity>() {
        @Override public int compare(UserEntity user1, UserEntity user2) {
            return userEntities.get(user1.getIdUser()).compareTo(userEntities.get(user2.getIdUser()));
        }
    };

        public WatchInfoBuilder() {

        }

        public void setWatches(List<WatchEntity> watches) {
            setWatchesAndExtractIds(watches);
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

        public Map<EventEntity, Collection<UserEntity>> build() {
            map = TreeMultimap.create(eventComparatorByDate, userComparatorByNameAndCurrentUser);
            buildMapFromWatchesAndEntities();
            return map.asMap();
        }

        private void buildMapFromWatchesAndEntities() {
            for (WatchEntity watch : watches) {
                addWatchingToResult(watch);
            }
        }

        private void addWatchingToResult(WatchEntity watchEntity) {
            EventEntity eventEntity = eventEntities.get(watchEntity.getIdEvent());
            UserEntity userEntity = userEntities.get(watchEntity.getIdUser());
            if (eventEntity != null && userEntity != null) {
                addWatchingPopulatedToResult(eventEntity, userEntity);
            }
        }

        private void addWatchingPopulatedToResult(EventEntity eventEntity, UserEntity userEntity) {
            map.put(eventEntity, userEntity);
        }

        private void setWatchesAndExtractIds(Collection<WatchEntity> watches) {
            this.watches.addAll(watches);
            extractEventsAndUserIdsFromWatches(watches);
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



    }


