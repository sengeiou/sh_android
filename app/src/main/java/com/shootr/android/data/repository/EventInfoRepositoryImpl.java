package com.shootr.android.data.repository;

import android.support.v4.util.LongSparseArray;
import com.shootr.android.data.entity.MatchEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.data.mapper.WatchEntityMapper;
import com.shootr.android.db.manager.MatchManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.repository.EventInfoRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.service.ShootrService;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class EventInfoRepositoryImpl implements EventInfoRepository {

    private final SessionRepository sessionRepository;
    private final WatchManager watchManager;
    private final MatchManager matchManager;
    private final UserManager userManager;
    private final ShootrService shootrService;
    private final EventEntityMapper eventEntityMapper;
    private final WatchEntityMapper watchEntityMapper;
    private final UserEntityMapper userEntityMapper;

    @Inject public EventInfoRepositoryImpl(SessionRepository sessionRepository, WatchManager watchManager,
      MatchManager matchManager, UserManager userManager, ShootrService shootrService,
      EventEntityMapper eventEntityMapper, WatchEntityMapper watchEntityMapper, UserEntityMapper userEntityMapper) {
        this.sessionRepository = sessionRepository;
        this.watchManager = watchManager;
        this.matchManager = matchManager;
        this.userManager = userManager;
        this.shootrService = shootrService;
        this.eventEntityMapper = eventEntityMapper;
        this.watchEntityMapper = watchEntityMapper;
        this.userEntityMapper = userEntityMapper;
    }

    @Override public void loadVisibleEventInfo(EventInfoCallback callback) {
        loadEventInfoOffline(callback);
    }

    private void loadEventInfoOffline(EventInfoCallback callback) {
        WatchEntity watchVisibleByUser = watchManager.getWatchVisibleByUser(sessionRepository.getCurrentUserId());

        Long idEvent = watchVisibleByUser.getIdMatch();
        MatchEntity eventEntity = matchManager.getMatchById(idEvent);

        List<WatchEntity> watchEntities = watchManager.getWatchesByMatch(eventEntity.getIdMatch());
        List<UserEntity> usersWatching = userManager.getUsersByIds(userIdsFromWatches(watchEntities));

        EventInfo eventInfo = eventInfoFromEntities(watchVisibleByUser, eventEntity, watchEntities, usersWatching);
        callback.onLoaded(eventInfo);
    }

    private EventInfo eventInfoFromEntities(WatchEntity watchVisibleByUser, MatchEntity eventEntity,
      List<WatchEntity> watchEntities, List<UserEntity> usersWatching) {
        Event event = eventEntityMapper.transform(eventEntity);
        Watch currentUserWatch = watchEntityMapper.transform(watchVisibleByUser, sessionRepository.getCurrentUser());
        List<Watch> watches = combineWatchEntitiesAndUsers(watchEntities, usersWatching);
        removeCurrentUserFromWatches(watches, sessionRepository.getCurrentUserId());

        return buildEventInfo(event, currentUserWatch, watches);
    }

    private void removeCurrentUserFromWatches(List<Watch> watches, long currentUserId) {
        for (int i = 0; i < watches.size(); i++) {
            if (watches.get(i).getUser().getIdUser() == currentUserId) {
                watches.remove(i);
                break;
            }
        }
    }

    private List<Watch> combineWatchEntitiesAndUsers(List<WatchEntity> watchEntities, List<UserEntity> usersWatching) {
        LongSparseArray<UserEntity> usersSparseArray = userListToSparseArray(usersWatching);
        long currentUserId = sessionRepository.getCurrentUserId();
        List<Watch> watches = new ArrayList<>(watchEntities.size());
        for (WatchEntity watchEntity : watchEntities) {
            UserEntity userEntity = usersSparseArray.get(watchEntity.getIdUser());
            //TODO check null
            User user = userEntityMapper.transform(userEntity, currentUserId);
            watches.add(watchEntityMapper.transform(watchEntity, user));
        }
        return watches;
    }

    private LongSparseArray<UserEntity> userListToSparseArray(List<UserEntity> userEntities) {
        LongSparseArray<UserEntity> sparseArray = new LongSparseArray<>(userEntities.size());
        for (UserEntity userEntity : userEntities) {
            sparseArray.put(userEntity.getIdUser(), userEntity);
        }
        return sparseArray;
    }

    private List<Long> userIdsFromWatches(List<WatchEntity> watchEntities) {
        List<Long> ids = new ArrayList<>();
        for (WatchEntity watchEntity : watchEntities) {
            ids.add(watchEntity.getIdUser());
        }
        return ids;
    }

    private EventInfo buildEventInfo(Event currentVisibleEvent, Watch visibleEventWatch, List<Watch> followingWatches) {
        return new EventInfo.Builder().event(currentVisibleEvent)
          .currentUserWatch(visibleEventWatch)
          .watchers(followingWatches)
          .build();
    }
}
