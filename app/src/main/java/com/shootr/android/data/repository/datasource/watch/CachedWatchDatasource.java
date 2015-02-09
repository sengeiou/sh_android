package com.shootr.android.data.repository.datasource.watch;

import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.repository.datasource.CachedDataSource;
import com.shootr.android.domain.repository.Local;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class CachedWatchDatasource implements WatchDataSource, CachedDataSource {

    private static final long EXPIRATION_TIME_MILLIS = 20 * 1000;

    private final WatchDataSource localWatchDataSource;
    boolean wasValidLastCheck = false;
    long lastCacheUpdateTime;

    @Inject public CachedWatchDatasource(@Local WatchDataSource localWatchDataSource) {
        this.localWatchDataSource = localWatchDataSource;
    }

    @Override public boolean isValid() {
        boolean isValidNow = wasValidLastCheck && !hasExpired();
        if (!isValidNow) {
            wasValidLastCheck = false;
        }
        return isValidNow;
    }

    @Override public void invalidate() {
        wasValidLastCheck = false;
    }


    protected boolean hasExpired() {
        return System.currentTimeMillis() > lastCacheUpdateTime + EXPIRATION_TIME_MILLIS;
    }

    protected void resetCachedUpdateTime() {
        lastCacheUpdateTime = System.currentTimeMillis();
        wasValidLastCheck = true;
    }

    @Override public WatchEntity putWatch(WatchEntity watchEntity) {
        resetCachedUpdateTime();
        return watchEntity;
    }

    @Override public List<WatchEntity> putWatches(List<WatchEntity> watchEntities) {
        resetCachedUpdateTime();
        return watchEntities;
    }

    @Override public WatchEntity getWatch(Long idEvent, Long idUser) {
        if (isValid()) {
            Timber.d("Cache hit: getWatch");
            return localWatchDataSource.getWatch(idEvent, idUser);
        } else {
            Timber.d("Cache miss: getWatch");
            return null;
        }
    }

    @Override public WatchEntity getVisible(Long userId) {
        if (isValid()) {
            Timber.d("Cache hit: getVisible");
            return localWatchDataSource.getVisible(userId);
        } else {
            Timber.d("Cache miss: getVisible");
            return null;
        }
    }

    @Override public List<WatchEntity> getWatchesForUsersAndEvent(List<Long> users, Long idEvent) {
        if (isValid()) {
            Timber.d("Cache hit: getWatchesForUsersAndEvent");
            return localWatchDataSource.getWatchesForUsersAndEvent(users, idEvent);
        } else {
            Timber.d("Cache miss: getWatchesForUsersAndEvent");
            return null;
        }
    }

    @Override public List<WatchEntity> getWatchesFromUsers(List<Long> users) {
        if (isValid()) {
            Timber.d("Cache hit: getWatchesFromUsers");
            return localWatchDataSource.getWatchesFromUsers(users);
        } else {
            Timber.d("Cache miss: getWatchesFromUsers");
            return null;
        }
    }

    @Override public void deleteAllWatchesNotPending() {
        throw new RuntimeException("Can't delete entities from the cache");
    }

    @Override public List<WatchEntity> getEntitiesNotSynchronized() {
        if (isValid()) {
            Timber.d("Cache hit: getEntitiesNotSynchronized");
            return localWatchDataSource.getEntitiesNotSynchronized();
        } else {
            Timber.d("Cache miss: getEntitiesNotSynchronized");
            return null;
        }
    }
}
