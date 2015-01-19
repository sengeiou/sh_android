package com.shootr.android.sync;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.db.manager.EventManager;
import com.shootr.android.db.manager.WatchManager;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class InfoCleaner {

    private WatchManager watchManager;
    private EventManager eventManager;

    @Inject public InfoCleaner(EventManager eventManager, WatchManager watchManager) {
        this.eventManager = eventManager;
        this.watchManager = watchManager;
    }

    public void clean() {
        cleanEndedEventsWithTheirWatches();
        cleanRejectedWatches();
    }

    private void cleanEndedEventsWithTheirWatches() {
        List<EventEntity> endedEvents = getEndedEvents();
        List<WatchEntity> watchesFromEndedEvents = getWatchesFromEvents(endedEvents);

        cleanWatches(watchesFromEndedEvents);
        cleanEvents(endedEvents);
    }

    private List<EventEntity> getEndedEvents() {
        return eventManager.getEndedEvents();
    }

    private List<WatchEntity> getWatchesFromEvents(List<EventEntity> events) {
        if (events.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<Long> eventIds = new ArrayList<>();
        for (EventEntity event : events) {
            eventIds.add(event.getIdEvent());
        }
        return watchManager.getWatchesFromEvents(eventIds);
    }

    private void cleanWatches(List<WatchEntity> watches) {
        watchManager.deleteWatches(watches);
    }

    private void cleanEvents(List<EventEntity> events) {
        eventManager.deleteEvents(events);
    }

    private void cleanRejectedWatches() {
        List<WatchEntity> rejectedWatches = getRejectedWatches();
        cleanWatches(rejectedWatches);
    }

    public List<WatchEntity> getRejectedWatches() {
        return watchManager.getWatchesRejected();
    }
}
