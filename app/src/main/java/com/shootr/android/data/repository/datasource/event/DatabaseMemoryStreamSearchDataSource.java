package com.shootr.android.data.repository.datasource.event;

import android.support.v4.util.ArrayMap;
import com.shootr.android.data.entity.StreamSearchEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.StreamManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DatabaseMemoryStreamSearchDataSource implements StreamSearchDataSource {

    private final StreamManager streamManager;
    private final Map<String, StreamSearchEntity> lastEventSearchResults;
    private final SessionRepository sessionRepository;
    private final FollowManager followManager;
    private final UserManager userManager;

    @Inject public DatabaseMemoryStreamSearchDataSource(StreamManager streamManager,
      SessionRepository sessionRepository, FollowManager followManager, UserManager userManager) {
        this.streamManager = streamManager;
        this.sessionRepository = sessionRepository;
        this.followManager = followManager;
        this.userManager = userManager;
        lastEventSearchResults = new ArrayMap<>();
    }

    public void setLastSearchResults(List<StreamSearchEntity> eventSearchEntities) {
        lastEventSearchResults.clear();
        for (StreamSearchEntity streamSearchEntity : eventSearchEntities) {
            lastEventSearchResults.put(streamSearchEntity.getIdEvent(), streamSearchEntity);
        }
    }

    @Override public List<StreamSearchEntity> getDefaultStreams(String locale) {
        Map<String, Integer> watchersCountByEvents = getWatchersCountByEvents();
        List<StreamSearchEntity> defaultEventSearch = streamManager.getDefaultStreamSearch();
        List<StreamSearchEntity> eventSearchEntitiesWithUpdatedWatchNumber =
          updateWatchNumberInEvents(watchersCountByEvents, defaultEventSearch);
        return eventSearchEntitiesWithUpdatedWatchNumber;
    }

    private List<StreamSearchEntity> updateWatchNumberInEvents(Map<String, Integer> watchersCountByEvents,
      List<StreamSearchEntity> defaultEventSearch) {
        for (StreamSearchEntity streamSearchEntity : defaultEventSearch) {
            Integer eventWatchers = watchersCountByEvents.get(streamSearchEntity.getIdEvent());
            if (eventWatchers != null) {
                streamSearchEntity.setWatchers(eventWatchers);
            }
        }
        return defaultEventSearch;
    }

    @Override public void putDefaultStreams(List<StreamSearchEntity> eventSearchEntities) {
        streamManager.putDefaultStreamSearch(eventSearchEntities);
    }

    @Override public void deleteDefaultStreams() {
        streamManager.deleteDefaultStreamSearch();
    }

    @Override public StreamSearchEntity getStreamResult(String idEvent) {
        StreamSearchEntity eventFromDefaultList = streamManager.getStreamSearchResultById(idEvent);
        if (eventFromDefaultList != null) {
            return eventFromDefaultList;
        } else {
            StreamSearchEntity eventFromLastSearchResults = lastEventSearchResults.get(idEvent);
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
