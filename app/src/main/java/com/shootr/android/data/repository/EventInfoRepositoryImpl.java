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
import com.shootr.android.domain.exception.RepositoryException;
import com.shootr.android.domain.repository.EventInfoRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.NetworkConnection;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import java.io.IOException;
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
    private final NetworkConnection networkConnection;

    @Inject public EventInfoRepositoryImpl(SessionRepository sessionRepository, WatchManager watchManager,
      MatchManager matchManager, UserManager userManager, ShootrService shootrService,
      EventEntityMapper eventEntityMapper, WatchEntityMapper watchEntityMapper, UserEntityMapper userEntityMapper,
      NetworkConnection networkConnection) {
        this.sessionRepository = sessionRepository;
        this.watchManager = watchManager;
        this.matchManager = matchManager;
        this.userManager = userManager;
        this.shootrService = shootrService;
        this.eventEntityMapper = eventEntityMapper;
        this.watchEntityMapper = watchEntityMapper;
        this.userEntityMapper = userEntityMapper;
        this.networkConnection = networkConnection;
    }

    @Override public void loadVisibleEventInfo(EventInfoCallback callback) {
        loadEventInfoOffline(callback);
        loadEventInfoOnlineIfAvailable(callback);
    }

    private void loadEventInfoOnlineIfAvailable(EventInfoCallback callback) {
        if (networkConnection.isConnected()) {
            try {
                loadEventInfoOnline(callback);
            } catch (IOException e) {
                callback.onError(new RepositoryException(e)); //TODO change for CommunicationError
            }
        } else {
            callback.onError(new ConnectionNotAvailableEvent());
        }
    }

    private void loadEventInfoOffline(EventInfoCallback callback) {
        WatchEntity watchVisibleByUser = watchManager.getWatchVisibleByUser(sessionRepository.getCurrentUserId());
        if (watchVisibleByUser == null) {
            return;
        }

        Long idEvent = watchVisibleByUser.getIdMatch();
        MatchEntity eventEntity = matchManager.getMatchById(idEvent);
        if (eventEntity == null) {
            return; //TODO mmm should't happen, right? What's going on then?
        }

        List<WatchEntity> watchEntities = watchManager.getWatchesByMatch(eventEntity.getIdMatch());
        List<UserEntity> usersWatching = userManager.getUsersByIds(userIdsFromWatches(watchEntities));

        EventInfo eventInfo =
          mapEntitiesAndBuildEventInfo(watchVisibleByUser, eventEntity, watchEntities, usersWatching);
        callback.onLoaded(eventInfo);
    }

    private void loadEventInfoOnline(EventInfoCallback callback) throws IOException {
        WatchEntity watchVisibleByUser = shootrService.getVisibleWatch(sessionRepository.getCurrentUserId());
        if (watchVisibleByUser == null) {
            callback.onLoaded(noEventVisible());
            return;
        }

        Long idEvent = watchVisibleByUser.getIdMatch();
        MatchEntity eventEntity = shootrService.getMatchByIdMatch(idEvent);
        if (eventEntity == null) {
            callback.onLoaded(noEventVisible());
            return;
        }
        List<UserEntity> usersFollowing = shootrService.getFollowing(sessionRepository.getCurrentUserId(), 0L);
        List<WatchEntity> watchEntities = shootrService.getWatchesFromUsersByMatch(idEvent, userIds(usersFollowing));

        EventInfo eventInfo =
          mapEntitiesAndBuildEventInfo(watchVisibleByUser, eventEntity, watchEntities, usersFollowing);
        callback.onLoaded(eventInfo);

        storeUsers(usersFollowing);
        storeEvent(eventEntity);
        storeWatches(watchEntities);
        storeCurrentUserWatch(watchVisibleByUser);
    }

    private void storeUsers(List<UserEntity> userEntities) {
        userManager.saveUsers(userEntities);
    }

    private void storeEvent(MatchEntity eventEntity) {
        matchManager.saveMatch(eventEntity);
    }

    private void storeWatches(List<WatchEntity> watchEntities) {
        watchManager.saveWatches(watchEntities);
    }

    private void storeCurrentUserWatch(WatchEntity watchEntity) {
        watchManager.saveWatch(watchEntity);
    }

    private EventInfo mapEntitiesAndBuildEventInfo(WatchEntity watchVisibleByUser, MatchEntity eventEntity,
      List<WatchEntity> watchEntities, List<UserEntity> usersWatching) {
        Event event = eventEntityMapper.transform(eventEntity);
        Watch currentUserWatch = watchEntityMapper.transform(watchVisibleByUser, sessionRepository.getCurrentUser());
        List<Watch> watches = combineWatchEntitiesAndUsers(watchEntities, usersWatching);
        removeCurrentUserFromWatches(watches, sessionRepository.getCurrentUserId());

        return buildEventInfo(event, currentUserWatch, watches);
    }

    private List<Long> userIds(List<UserEntity> users) {
        List<Long> ids = new ArrayList<>(users.size());
        for (UserEntity user : users) {
            ids.add(user.getIdUser());
        }
        return ids;
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

    private EventInfo noEventVisible() {
        return new EventInfo();
    }
}
