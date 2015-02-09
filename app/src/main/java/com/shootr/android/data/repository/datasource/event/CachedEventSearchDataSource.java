package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.repository.datasource.CachedDataSource;
import com.shootr.android.domain.repository.Remote;
import java.util.List;
import javax.inject.Inject;

public class CachedEventSearchDataSource implements EventSearchDataSource, CachedDataSource {

    private static final long EXPIRATION_TIME_MILLIS = 30 * 1000;

    private final EventSearchDataSource remoteEventSearchDataSource;

    private List<EventSearchEntity> cachedEvents;
    boolean wasValidLastCheck = false;
    long lastCacheUpdateTime;

    @Inject public CachedEventSearchDataSource(@Remote EventSearchDataSource remoteEventSearchDataSource) {
        this.remoteEventSearchDataSource = remoteEventSearchDataSource;
    }

    @Override public boolean isValid() {
        boolean isValidNow = wasValidLastCheck && !hasExpired();
        if (!isValidNow) {
            wasValidLastCheck = false;
        }
        return isValidNow;
    }

    protected boolean hasExpired() {
        return System.currentTimeMillis() > lastCacheUpdateTime + EXPIRATION_TIME_MILLIS;
    }

    @Override public void invalidate() {
        wasValidLastCheck = false;
    }

    protected void resetCachedUpdateTime() {
        lastCacheUpdateTime = System.currentTimeMillis();
        wasValidLastCheck = true;
    }

    @Override public List<EventSearchEntity> getDefaultEvents() {
        if (isValid()) {
            return cachedEvents;
        } else {
            List<EventSearchEntity> defaultEvents = remoteEventSearchDataSource.getDefaultEvents();
            cachedEvents = defaultEvents;
            resetCachedUpdateTime();
            return defaultEvents;
        }
    }

    @Override public List<EventSearchEntity> getEvents(String query) {
        return remoteEventSearchDataSource.getEvents(query);
    }
}
