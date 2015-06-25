package com.shootr.android.data.repository.datasource.event;

import android.support.v4.util.ArrayMap;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.EventManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Deprecated @Singleton
public class DatabaseMemoryEventSearchDataSource implements EventSearchDataSource {

    private final EventManager eventManager;
    private final Map<String, EventSearchEntity> lastEventSearchResults;
    private final SessionRepository sessionRepository;
    private final FollowManager followManager;
    private final UserManager userManager;

    @Inject public DatabaseMemoryEventSearchDataSource(EventManager eventManager,
                                                       SessionRepository sessionRepository,
                                                       FollowManager followManager,
                                                       UserManager userManager) {
        this.eventManager = eventManager;
        this.sessionRepository = sessionRepository;
        this.followManager = followManager;
        this.userManager = userManager;
        lastEventSearchResults = new ArrayMap<>();
    }

    public void setLastSearchResults(List<EventSearchEntity> eventSearchEntities) {
        lastEventSearchResults.clear();
        for (EventSearchEntity eventSearchEntity : eventSearchEntities) {
            lastEventSearchResults.put(eventSearchEntity.getIdEvent(), eventSearchEntity);
        }
    }

    @Override public List<EventSearchEntity> getDefaultEvents(String locale) {
        Map<String, Integer> watchersCountByEvents = getWatchersCountByEvents();
        List<EventSearchEntity> defaultEventSearch = eventManager.getDefaultEventSearch();
        List<EventSearchEntity> eventSearchEntitiesWithUpdatedWatchNumber =
          updateWatchNumberInEvents(watchersCountByEvents, defaultEventSearch);
        return eventSearchEntitiesWithUpdatedWatchNumber;
    }

    private List<EventSearchEntity> updateWatchNumberInEvents(Map<String, Integer> watchersCountByEvents,
      List<EventSearchEntity> defaultEventSearch) {
        for (EventSearchEntity eventSearchEntity : defaultEventSearch) {
            Integer eventWatchers = watchersCountByEvents.get(eventSearchEntity.getIdEvent());
            if (eventWatchers != null) {
                eventSearchEntity.setWatchers(eventWatchers);
            }
        }
        return defaultEventSearch;
    }

    @Override public List<EventSearchEntity> getEvents(String query, String locale) {
        throw new IllegalStateException("Search not implemented in local datasource");
    }

    @Override public void putDefaultEvents(List<EventSearchEntity> eventSearchEntities) {
        eventManager.putDefaultEventSearch(eventSearchEntities);
    }

    @Override public void deleteDefaultEvents() {
        eventManager.deleteDefaultEventSearch();
    }

    @Override public EventSearchEntity getEventResult(String idEvent) {
        EventSearchEntity eventFromDefaultList = eventManager.getEventSearchResultById(idEvent);
        if (eventFromDefaultList != null) {
            return eventFromDefaultList;
        } else {
            EventSearchEntity eventFromLastSearchResults = lastEventSearchResults.get(idEvent);
            return eventFromLastSearchResults;
        }
    }

    private Map<String, Integer> getWatchersCountByEvents()  {
        String currentUserId = sessionRepository.getCurrentUserId();

        List<String> followingUserIds = followManager.getUserFollowingIds(currentUserId);
        List<UserEntity> watchers = userManager.getUsersWatchingSomething(followingUserIds);

        Map<String, Integer> eventsWatchesCounts = new HashMap<>();
        for (UserEntity watcher : watchers) {
            Integer currentCount = eventsWatchesCounts.get(watcher.getIdWatchingEvent());
            if (currentCount != null) {
                eventsWatchesCounts.put(watcher.getIdWatchingEvent(), currentCount + 1);
            } else {
                eventsWatchesCounts.put(watcher.getIdWatchingEvent(), 1);
            }
        }
        return eventsWatchesCounts;
    }
}
