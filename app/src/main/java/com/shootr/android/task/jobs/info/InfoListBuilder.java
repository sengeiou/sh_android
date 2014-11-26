package com.shootr.android.task.jobs.info;

import android.support.v4.util.LongSparseArray;
import com.google.common.collect.TreeMultimap;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.db.objects.WatchEntity;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.model.mappers.MatchModelMapper;
import com.shootr.android.ui.model.mappers.UserWatchingModelMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;

public class InfoListBuilder {

    private TreeMultimap<MatchModel, UserWatchingModel> map;

    private MatchModelMapper matchModelMapper;
    private UserWatchingModelMapper userWatchingModelMapper;

    private LongSparseArray<MatchModel> matchModelCache = new LongSparseArray<>();

    private LongSparseArray<MatchEntity> matchEntities = new LongSparseArray<>();
    private LongSparseArray<UserEntity> userEntities = new LongSparseArray<>();

    private List<Long> matchIdsFromWatches = new ArrayList<>();
    private List<Long> userIdsFromWatches = new ArrayList<>();

    private UserEntity currentUser;

    private Set<WatchEntity> validWatches = new HashSet<>();
    private Set<WatchEntity> allWatches = new HashSet<>();
    private MatchEntity myTeamNextMatch;

    private Comparator<? super MatchModel> matchComparatorByDate = new Comparator<MatchModel>() {
        @Override public int compare(MatchModel match1, MatchModel match2) {
            return matchEntities.get(match1.getIdMatch()).compareTo(matchEntities.get(match2.getIdMatch()));
        }
    };

    private Comparator<? super UserWatchingModel> userComparatorByNameAndCurrentUser = new Comparator<UserWatchingModel>() {
        @Override public int compare(UserWatchingModel user1, UserWatchingModel user2) {
            if (user1.getIdUser().equals(currentUser.getIdUser())) {
                return -1;
            } else if (user2.getIdUser().equals(currentUser.getIdUser())) {
                return 1;
            } else {
                return userEntities.get(user1.getIdUser()).compareTo(userEntities.get(user2.getIdUser()));
            }
        }
    };

    protected InfoListBuilder(UserEntity currentUser, MatchModelMapper matchModelMapper, UserWatchingModelMapper userWatchingModelMapper) {
        this.currentUser = currentUser;
        this.matchModelMapper = matchModelMapper;
        this.userWatchingModelMapper = userWatchingModelMapper;
        userEntities.put(currentUser.getIdUser(), currentUser);
    }

    public void setWatches(List<WatchEntity> validWatches) {
        setWatchesAndExtractIds(validWatches);
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

    public void putMyTeamMatch(MatchEntity myTeamNextMatch) {
        this.myTeamNextMatch = myTeamNextMatch;
        matchEntities.put(myTeamNextMatch.getIdMatch(), myTeamNextMatch);
    }

    public Map<MatchModel, Collection<UserWatchingModel>> build() {
        map = TreeMultimap.create(matchComparatorByDate, userComparatorByNameAndCurrentUser);

        addMyTeamsNextMatch();

        buildMapFromWatchesAndEntities();

        fillResultWithCurrentUser();

        return map.asMap();
    }

    private void addMyTeamsNextMatch() {
        if (myTeamNextMatch != null) {
            WatchEntity meNotWatchingMyTeam = new WatchEntity();
            meNotWatchingMyTeam.setIdMatch(myTeamNextMatch.getIdMatch());
            meNotWatchingMyTeam.setIdUser(currentUser.getIdUser());
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
        for (MatchModel matchModel : map.keySet()) {
            NavigableSet<UserWatchingModel> userWatchingSet = map.get(matchModel);
            if(!containsCurrentUser(userWatchingSet)) {
                userWatchingSet.add(getCurrentUserNotWatching(matchModel.getIdMatch()));
            }
        }
    }

    private boolean containsCurrentUser(Set<UserWatchingModel> userWatchingModelSet) {
        for (UserWatchingModel user : userWatchingModelSet) {
            if (user.getIdUser().equals(currentUser.getIdUser())) {
                return true;
            }
        }
        return false;
    }

    private UserWatchingModel getCurrentUserNotWatching(Long matchId) {
        WatchEntity watchEntity = new WatchEntity();
        watchEntity.setIdUser(currentUser.getIdUser());
        watchEntity.setStatus(0L);
        watchEntity.setIdMatch(matchId);
        MatchEntity matchEntity = matchEntities.get(matchId);
        return getUserWatchingModelFromEntity(currentUser, watchEntity, matchEntity);
    }

    private void addWatchingToResult(WatchEntity watchEntity) {
        MatchEntity matchEntity = matchEntities.get(watchEntity.getIdMatch());
        UserEntity userEntity = userEntities.get(watchEntity.getIdUser());
        if (matchEntity != null && userEntity != null) {
            addWatchingPopulatedToResult(matchEntity, userEntity, watchEntity);
        }
    }

    private void addWatchingPopulatedToResult(MatchEntity matchEntity, UserEntity userEntity, WatchEntity watchEntity) {
        MatchModel matchModel = getMatchModelFromEntity(matchEntity);
        UserWatchingModel userWatchingModel = getUserWatchingModelFromEntity(userEntity, watchEntity, matchEntity);
        map.put(matchModel, userWatchingModel);
    }

    private void setWatchesAndExtractIds(Collection<WatchEntity> watches) {
        this.allWatches.addAll(watches);
        List<WatchEntity> watchesVisible = removeWatchesWithNotVisibleMatches(watches);
        this.validWatches.addAll(watchesVisible);
        extractMatchesAndUserIdsFromWatches(watchesVisible);
    }

    private List<WatchEntity> removeWatchesWithNotVisibleMatches(Collection<WatchEntity> watches) {
        List<Long> hiddenMatchesIds = new ArrayList<>();
        for (WatchEntity watch : watches) {
            if (watch.getIdUser().equals(currentUser.getIdUser()) && !watch.getVisible()) {
                hiddenMatchesIds.add(watch.getIdMatch());
            }
        }
        List<WatchEntity> watchesVisible = new ArrayList<>();
        for (WatchEntity watch : watches) {
            if (!hiddenMatchesIds.contains(watch.getIdMatch())) {
                watchesVisible.add(watch);
            }
        }
        return watchesVisible;
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

    public Set<WatchEntity> getValidWatches() {
        return validWatches;
    }

    private UserWatchingModel getUserWatchingModelFromEntity(UserEntity userEntity, WatchEntity watchEntity, MatchEntity matchEntity) {
        //TODO Use some cache? maybe? One user can be watching one match but not watching another
        return userWatchingModelMapper.toUserWatchingModel(userEntity, watchEntity.getStatus() == 1, matchEntity.getStatus()==1, watchEntity.getPlace());
    }

    private MatchModel getMatchModelFromEntity(MatchEntity matchEntity) {
        MatchModel matchModel = matchModelCache.get(matchEntity.getIdMatch());
        if (matchModel == null) {
            matchModel = matchModelMapper.toMatchModel(matchEntity);
            matchModelCache.put(matchEntity.getIdMatch(), matchModel);
        }
        return matchModel;
    }
}