package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.data.repository.datasource.CachedDataSource;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import java.util.List;
import javax.inject.Inject;

public class CachedSuggestedPeopleDataSource implements SuggestedPeopleDataSource, CachedDataSource {

    private static final long EXPIRATION_TIME_MILLIS = 12 * 60 * 60 * 1000;

    private final SuggestedPeopleDataSource localSuggestedPeopleDataSource;
    private final SuggestedPeopleDataSource remoteSuggestedPeopleDataSource;
    boolean wasValidLastCheck = false;
    long lastCacheUpdateTime;

    @Inject public CachedSuggestedPeopleDataSource(@Local SuggestedPeopleDataSource localSuggestedPeopleDataSource,
      @Remote SuggestedPeopleDataSource remoteSuggestedPeopleDataSource) {
        this.localSuggestedPeopleDataSource = localSuggestedPeopleDataSource;
        this.remoteSuggestedPeopleDataSource = remoteSuggestedPeopleDataSource;
    }

    @Override public List<SuggestedPeopleEntity> getSuggestedPeople(String currentUserId) {
        List<SuggestedPeopleEntity> suggestedPeople = null;
        if (isValid()) {
            suggestedPeople = localSuggestedPeopleDataSource.getSuggestedPeople(currentUserId);
        }
        if (suggestedPeople != null) {
            //TODO  Si no es nulo lo devuelves y tal
            return suggestedPeople;
        } else {
            //TODO Lo apillas de remoto y lo arremetes en local
            suggestedPeople = remoteSuggestedPeopleDataSource.getSuggestedPeople(currentUserId);
            localSuggestedPeopleDataSource.
            this.resetCachedUpdateTime();
           return remotUser;
        }

        return null;
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
}
