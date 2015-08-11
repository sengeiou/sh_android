package com.shootr.android.data.repository.datasource.event;

import android.support.v4.util.ArrayMap;
import com.shootr.android.data.entity.StreamSearchEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.StreamManager;
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
    private final Map<String, StreamSearchEntity> lastStreamSearchResults;
    private final SessionRepository sessionRepository;
    private final FollowManager followManager;
    private final UserManager userManager;

    @Inject public DatabaseMemoryStreamSearchDataSource(StreamManager streamManager,
      SessionRepository sessionRepository, FollowManager followManager, UserManager userManager) {
        this.streamManager = streamManager;
        this.sessionRepository = sessionRepository;
        this.followManager = followManager;
        this.userManager = userManager;
        lastStreamSearchResults = new ArrayMap<>();
    }

    public void setLastSearchResults(List<StreamSearchEntity> streamSearchEntities) {
        lastStreamSearchResults.clear();
        for (StreamSearchEntity streamSearchEntity : streamSearchEntities) {
            lastStreamSearchResults.put(streamSearchEntity.getIdStream(), streamSearchEntity);
        }
    }

    @Override public List<StreamSearchEntity> getDefaultStreams(String locale) {
        Map<String, Integer> watchersCountByStreams = getWatchersCountByStreams();
        List<StreamSearchEntity> defaultStreamSearch = streamManager.getDefaultStreamSearch();
        List<StreamSearchEntity> streamSearchEntitiesWithUpdatedWatchNumber =
          updateWatchNumberInStreams(watchersCountByStreams, defaultStreamSearch);
        return streamSearchEntitiesWithUpdatedWatchNumber;
    }

    private List<StreamSearchEntity> updateWatchNumberInStreams(Map<String, Integer> watchersCountByStreams,
      List<StreamSearchEntity> defaultStreamSearch) {
        for (StreamSearchEntity streamSearchEntity : defaultStreamSearch) {
            Integer streamWatchers = watchersCountByStreams.get(streamSearchEntity.getIdStream());
            if (streamWatchers != null) {
                streamSearchEntity.setWatchers(streamWatchers);
            }
        }
        return defaultStreamSearch;
    }

    @Override public void putDefaultStreams(List<StreamSearchEntity> streamSearchEntities) {
        streamManager.putDefaultStreamSearch(streamSearchEntities);
    }

    @Override public void deleteDefaultStreams() {
        streamManager.deleteDefaultStreamSearch();
    }

    @Override public StreamSearchEntity getStreamResult(String idStream) {
        StreamSearchEntity streamFromDefaultList = streamManager.getStreamSearchResultById(idStream);
        if (streamFromDefaultList != null) {
            return streamFromDefaultList;
        } else {
            StreamSearchEntity streamFromLastSearchResults = lastStreamSearchResults.get(idStream);
            return streamFromLastSearchResults;
        }
    }

    private Map<String, Integer> getWatchersCountByStreams()  {
        String currentUserId = sessionRepository.getCurrentUserId();

        List<String> followingUserIds = followManager.getUserFollowingIds(currentUserId);
        List<UserEntity> watchers = userManager.getUsersWatchingSomething(followingUserIds);

        Map<String, Integer> streamsWatchesCounts = new HashMap<>();
        for (UserEntity watcher : watchers) {
            Integer currentCount = streamsWatchesCounts.get(watcher.getIdWatchingStream());
            if (currentCount != null) {
                streamsWatchesCounts.put(watcher.getIdWatchingStream(), currentCount + 1);
            } else {
                streamsWatchesCounts.put(watcher.getIdWatchingStream(), 1);
            }
        }
        return streamsWatchesCounts;
    }
}
