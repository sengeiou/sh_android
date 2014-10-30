package gm.mobi.android.task.jobs.info;

import android.support.v4.util.LongSparseArray;
import com.google.common.collect.TreeMultimap;
import gm.mobi.android.db.objects.MatchEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.db.objects.WatchEntity;
import gm.mobi.android.ui.model.MatchModel;
import gm.mobi.android.ui.model.UserWatchingModel;
import gm.mobi.android.ui.model.mappers.MatchModelMapper;
import gm.mobi.android.ui.model.mappers.UserWatchingModelMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class InfoListBuilder {

    private TreeMultimap<MatchModel, UserWatchingModel> map;

    private MatchModelMapper matchModelMapper;
    private UserWatchingModelMapper userWatchingModelMapper;
    private DataProvider dataProvider;

    private LongSparseArray<MatchModel> matchModelCache = new LongSparseArray<>();
    private LongSparseArray<UserWatchingModel> userWatchingModelCache = new LongSparseArray<>();

    private LongSparseArray<MatchEntity> matchEntities = new LongSparseArray<>();
    private LongSparseArray<UserEntity> userEntities = new LongSparseArray<>();

    private List<Long> matchIds = new ArrayList<>();
    private List<Long> userIds = new ArrayList<>();

    private Long currentUserId;

    private Comparator<? super MatchModel> matchComparator = new Comparator<MatchModel>() {
        @Override public int compare(MatchModel match1, MatchModel match2) {
            return matchEntities.get(match1.getIdMatch()).compareTo(matchEntities.get(match2.getIdMatch()));
        }
    };

    private Comparator<? super UserWatchingModel> userComparator = new Comparator<UserWatchingModel>() {
        @Override public int compare(UserWatchingModel user1, UserWatchingModel user2) {
            if (user1.getIdUser().equals(currentUserId)) {
                return -1;
            }else if (user2.getIdUser().equals(currentUserId)) {
                return 1;
            } else {
                return userEntities.get(user1.getIdUser()).compareTo(userEntities.get(user2.getIdUser()));
            }
        }
    };

    protected InfoListBuilder(DataProvider dataProvider, Long currentUserId, MatchModelMapper matchModelMapper, UserWatchingModelMapper userWatchingModelMapper) {
        this.dataProvider = dataProvider;
        this.currentUserId = currentUserId;
        this.matchModelMapper = matchModelMapper;
        this.userWatchingModelMapper = userWatchingModelMapper;
    }

    public Map<MatchModel, Collection<UserWatchingModel>> build(List<WatchEntity> watches) {
        map = TreeMultimap.create(matchComparator, userComparator);

        getIdsFromWatches(watches);

        getMatchEntitiesFromIds();

        getUserEntitiesFromIds();

        buildResult(watches);

        return map.asMap();
    }

    private void buildResult(List<WatchEntity> watches) {
        for (WatchEntity watch : watches) {
            addWatching(watch);
        }
    }

    private void getUserEntitiesFromIds() {
        for (UserEntity userEntity : getUsersByIds(userIds)) {
            userEntities.put(userEntity.getIdUser(), userEntity);
        }
    }

    private void getMatchEntitiesFromIds() {
        for (MatchEntity matchEntity : getMatchesByIds(matchIds)) {
            matchEntities.put(matchEntity.getIdMatch(), matchEntity);
        }
    }

    private void getIdsFromWatches(List<WatchEntity> watches) {
        for (WatchEntity watch : watches) {
            matchIds.add(watch.getIdMatch());
            userIds.add(watch.getIdUser());
        }
    }

    private void addWatching(WatchEntity watchEntity) {
        MatchEntity matchEntity = matchEntities.get(watchEntity.getIdMatch());
        UserEntity userEntity = userEntities.get(watchEntity.getIdUser());
        addWatching(matchEntity, userEntity, watchEntity);
    }

    private void addWatching(MatchEntity matchEntity, UserEntity userEntity, WatchEntity watchEntity) {
        MatchModel matchModel = getMatchModel(matchEntity);
        UserWatchingModel userWatchingModel = getUserWatchingModel(userEntity, watchEntity);
        map.put(matchModel, userWatchingModel);
    }

    private UserWatchingModel getUserWatchingModel(UserEntity userEntity, WatchEntity watchEntity) {
        //TODO Use some cache? maybe? One user can be watching one match but not watching another
        return userWatchingModelMapper.toUserWatchingModel(userEntity, watchEntity.getStatus() == 1);
    }

    private MatchModel getMatchModel(MatchEntity matchEntity) {
        MatchModel matchModel = matchModelCache.get(matchEntity.getIdMatch());
        if (matchModel == null) {
            matchModel = matchModelMapper.toMatchModel(matchEntity);
            matchModelCache.put(matchEntity.getIdMatch(), matchModel);
        }
        return matchModel;
    }

    public List<UserEntity> getUsersByIds(List<Long> userIds) {
        return dataProvider.getUsersByIds(userIds);
    }

    public List<MatchEntity> getMatchesByIds(List<Long> matchIds) {
        return dataProvider.getMatchesByIds(matchIds);
    }

    public static interface DataProvider {

        public List<UserEntity> getUsersByIds(List<Long> userIds);

        public List<MatchEntity> getMatchesByIds(List<Long> matchIds);
    }
}
