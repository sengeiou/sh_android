package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.repository.datasource.CachedDataSource;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class CachedUserDataSource implements UserDataSource, CachedDataSource {

    private static final long EXPIRATION_TIME_MILLIS = 20 * 1000;

    private final UserDataSource localUserDataSource;
    private final UserDataSource remoteUserDataSource;
    boolean wasValidLastCheck = false;
    long lastCacheUpdateTime;

    @Inject public CachedUserDataSource(@Local UserDataSource localUserDataSource, @Remote UserDataSource remoteUserDataSource) {
        this.localUserDataSource = localUserDataSource;
        this.remoteUserDataSource = remoteUserDataSource;
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

    public void resetCachedUpdateTime() {
        lastCacheUpdateTime = System.currentTimeMillis();
        wasValidLastCheck = true;
    }

    @Override public List<UserEntity> getFollowing(Long userId) {
        throw new RuntimeException("getFollowing not cacheable");
    }

    @Override public UserEntity putUser(UserEntity userEntity) {
        return userEntity;
    }

    @Override public List<UserEntity> putUsers(List<UserEntity> userEntities) {
        return userEntities;
    }

    @Override public UserEntity getUser(Long id) {
        UserEntity cachedUser = null;
        if (isValid()) {
            cachedUser = localUserDataSource.getUser(id);
        }
        if (cachedUser != null) {
            return cachedUser;
        } else {
            UserEntity remoteUser = remoteUserDataSource.getUser(id);
            localUserDataSource.putUser(remoteUser);
            this.resetCachedUpdateTime();
            return remoteUser;
        }
    }

    @Override public List<UserEntity> getUsers(List<Long> userIds) {
        List<UserEntity> cachedUsers = null;
        if (isValid()) {
            cachedUsers = localUserDataSource.getUsers(userIds);
        }
        if (cachedUsers != null) {
            return cachedUsers;
        } else {
            List<UserEntity> remoteUsers = remoteUserDataSource.getUsers(userIds);
            localUserDataSource.putUsers(remoteUsers);
            this.resetCachedUpdateTime();
            return remoteUsers;
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