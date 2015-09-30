package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.data.repository.datasource.CachedDataSource;
import com.shootr.android.domain.repository.Local;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CachedSuggestedPeopleDataSource implements SuggestedPeopleDataSource, CachedDataSource {

    private static final long EXPIRATION_TIME_MILLIS = 12 * 60 * 60 * 1000;

    private final SuggestedPeopleDataSource localSuggestedPeopleDataSource;
    boolean wasValidLastCheck = false;
    long lastCacheUpdateTime;

    @Inject public CachedSuggestedPeopleDataSource(@Local SuggestedPeopleDataSource localSuggestedPeopleDataSource) {
        this.localSuggestedPeopleDataSource = localSuggestedPeopleDataSource;
    }

    @Override public List<SuggestedPeopleEntity> getSuggestedPeople() {
        List<SuggestedPeopleEntity> suggestedPeople = new ArrayList<>();
        if (isValid()) {
            suggestedPeople = localSuggestedPeopleDataSource.getSuggestedPeople();
        }
        return suggestedPeople;
    }

    @Override public void putSuggestedPeople(List<SuggestedPeopleEntity> suggestedPeople) {
        this.resetCachedUpdateTime();
        localSuggestedPeopleDataSource.putSuggestedPeople(suggestedPeople);
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
