package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.repository.datasource.CachedDataSource;
import com.shootr.android.domain.repository.Local;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class CachedUserDataSource implements UserDataSource, CachedDataSource {

    private static final long EXPIRATION_TIME_MILLIS = 20 * 1000;

    private final UserDataSource localUserDataSource;
    boolean wasValidLastCheck = false;
    long lastCacheUpdateTime;

    @Inject public CachedUserDataSource(@Local UserDataSource localUserDataSource) {
        this.localUserDataSource = localUserDataSource;
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

    @Override public List<UserEntity> getFollowing(Long userId) {
        if (isValid()) {
            Timber.d("Cache hit: getFollowing");
            return localUserDataSource.getFollowing(userId);
        } else {
            return null;
        }
    }

    @Override public UserEntity putUser(UserEntity userEntity) {
        resetCachedUpdateTime();
        return userEntity;
    }

    @Override public List<UserEntity> putUsers(List<UserEntity> userEntities) {
        resetCachedUpdateTime();
        return userEntities;
    }

    @Override public UserEntity getUser(Long id) {
        if (isValid()) {
            Timber.d("Cache hit: getUser %d", id);
            return localUserDataSource.getUser(id);
        } else {
            return null;
        }
    }

    @Override public List<UserEntity> getUsers(List<Long> userIds) {
        if (isValid()) {
            Timber.d("Cache hit: getUsers");
            return localUserDataSource.getUsers(userIds);
        } else {
            return null;
        }
    }

    @Override public boolean isFollower(Long from, Long who) {
        throw new RuntimeException("Can't use cache for follow status check");
    }

    @Override public boolean isFollowing(Long who, Long to) {
        throw new RuntimeException("Can't use cache for follow status check");
    }

    @Override public List<UserEntity> getEntitiesNotSynchronized() {
        throw new RuntimeException("Can't use cache for synchronization manipulation");
    }
}
