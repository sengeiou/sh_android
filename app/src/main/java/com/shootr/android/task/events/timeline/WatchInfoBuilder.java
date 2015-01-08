package com.shootr.android.task.events.timeline;

import android.support.v4.util.LongSparseArray;
import com.google.common.collect.TreeMultimap;
import com.shootr.android.data.entity.MatchEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.entity.WatchEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WatchInfoBuilder {

        private TreeMultimap<MatchEntity, UserEntity> map;

        private LongSparseArray<MatchEntity> matchEntities = new LongSparseArray<>();
        private LongSparseArray<UserEntity> userEntities = new LongSparseArray<>();

        private List<Long> matchIdsFromWatches = new ArrayList<>();
        private List<Long> userIdsFromWatches = new ArrayList<>();


        private Set<WatchEntity> watches = new HashSet<>();

        private Comparator<? super MatchEntity> matchComparatorByDate = new Comparator<MatchEntity>() {
            @Override public int compare(MatchEntity match1, MatchEntity match2) {
                return matchEntities.get(match1.getIdMatch()).compareTo(matchEntities.get(match2.getIdMatch()));
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

        public void provideMatches(List<MatchEntity> matches) {
            for (MatchEntity matchEntity : matches) {
                matchEntities.put(matchEntity.getIdMatch(), matchEntity);
            }
        }

        public Map<MatchEntity, Collection<UserEntity>> build() {
            map = TreeMultimap.create(matchComparatorByDate, userComparatorByNameAndCurrentUser);
            buildMapFromWatchesAndEntities();
            return map.asMap();
        }

        private void buildMapFromWatchesAndEntities() {
            for (WatchEntity watch : watches) {
                addWatchingToResult(watch);
            }
        }

        private void addWatchingToResult(WatchEntity watchEntity) {
            MatchEntity matchEntity = matchEntities.get(watchEntity.getIdMatch());
            UserEntity userEntity = userEntities.get(watchEntity.getIdUser());
            if (matchEntity != null && userEntity != null) {
                addWatchingPopulatedToResult(matchEntity, userEntity);
            }
        }

        private void addWatchingPopulatedToResult(MatchEntity matchEntity, UserEntity userEntity) {
            map.put(matchEntity, userEntity);
        }

        private void setWatchesAndExtractIds(Collection<WatchEntity> watches) {
            this.watches.addAll(watches);
            extractMatchesAndUserIdsFromWatches(watches);
        }

        private void extractMatchesAndUserIdsFromWatches(Collection<WatchEntity> watches) {
            for (WatchEntity watch : watches) {
                matchIdsFromWatches.add(watch.getIdMatch());
                userIdsFromWatches.add(watch.getIdUser());
            }
        }

        public List<Long> getUserIds() {
            return userIdsFromWatches;
        }

        public List<Long> getMatchIds() {
            return matchIdsFromWatches;
        }



    }


