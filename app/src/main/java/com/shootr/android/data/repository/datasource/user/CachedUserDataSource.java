package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.repository.datasource.CachedDataSource;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.List;
import javax.inject.Inject;

public class CachedUserDataSource implements UserDataSource, CachedDataSource {

    private static final long EXPIRATION_TIME_MILLIS = 20 * 1000;

    private final SessionRepository sessionRepository;
    private final UserDataSource localUserDataSource;
    private final UserDataSource remoteUserDataSource;
    boolean wasValidLastCheck = false;
    private boolean wasValidLastCheckPeople = false;
    long lastCacheUpdateTime;
    private long lastCacheUpdateTimePeople;

    @Inject public CachedUserDataSource(SessionRepository sessionRepository,
      @Local UserDataSource localUserDataSource,
      @Remote UserDataSource remoteUserDataSource) {
        this.sessionRepository = sessionRepository;
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

    private boolean isValidPeopleCache() {
        boolean isValidNow = wasValidLastCheckPeople && !hasExpiredPeopleCache();
        if (!isValidNow) {
            wasValidLastCheckPeople = false;
        }
        return isValidNow;
    }

    @Override public void invalidate() {
        wasValidLastCheck = false;
        wasValidLastCheckPeople = false;
    }

    protected boolean hasExpired() {
        return System.currentTimeMillis() > lastCacheUpdateTime + EXPIRATION_TIME_MILLIS;
    }

    private boolean hasExpiredPeopleCache() {
        return System.currentTimeMillis() > lastCacheUpdateTimePeople + EXPIRATION_TIME_MILLIS;
    }

    public void resetCachedUpdateTime() {
        lastCacheUpdateTime = System.currentTimeMillis();
        wasValidLastCheck = true;
    }

    public void resetCachedUpdateTimePeople() {
        lastCacheUpdateTimePeople = System.currentTimeMillis();
        wasValidLastCheckPeople = true;
    }

    @Override public synchronized List<UserEntity> getFollowing(String userId) {
        if (userId.equals(sessionRepository.getCurrentUserId())) {
            if (isValidPeopleCache()) {
                List<UserEntity> cachedFollowing = localUserDataSource.getFollowing(userId);
                if (cachedFollowing != null && !cachedFollowing.isEmpty()) {
                    return cachedFollowing;
                }
            }
            List<UserEntity> remoteFollowing = remoteUserDataSource.getFollowing(userId);
            localUserDataSource.putUsers(remoteFollowing);
            this.resetCachedUpdateTimePeople();
            return remoteFollowing;
        } else {
            return remoteUserDataSource.getFollowing(userId);
        }
    }

    @Override public UserEntity putUser(UserEntity userEntity) {
        return userEntity;
    }

    @Override public List<UserEntity> putUsers(List<UserEntity> userEntities) {
        return userEntities;
    }

    @Override public UserEntity getUser(String id) {
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

    @Override public boolean isFollower(String from, String who) {
        throw new IllegalStateException("Can't use cache for follow status check");
    }

    @Override public boolean isFollowing(String who, String to) {
        throw new IllegalStateException("Can't use cache for follow status check");
    }

    @Override public List<UserEntity> getEntitiesNotSynchronized() {
        throw new IllegalStateException("Can't use cache for synchronization manipulation");
    }

    @Override
    public UserEntity getUserByUsername(String username){
        throw new IllegalStateException("Username filtering is not cached");
    }

    @Override public List<UserEntity> getAllParticipants(String idStream, Long maxJoinDate) {
        throw new IllegalStateException("getAllParticipants is not cached");
    }

    @Override public List<UserEntity> findParticipants(String idStream, String query) {
        throw new IllegalStateException("Find Participants is not cached");
    }

    @Override
    public void updateWatch(UserEntity userEntity) {
        throw new IllegalStateException("updateWatch is not cached");
    }
}
