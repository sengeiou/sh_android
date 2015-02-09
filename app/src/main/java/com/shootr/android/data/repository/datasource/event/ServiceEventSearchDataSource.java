package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.exception.RepositoryException;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class ServiceEventSearchDataSource implements EventSearchDataSource {

    private final FollowManager followManager;
    private final WatchManager watchManager;
    private final ShootrService shootrService;
    private final SessionRepository sessionRepository;

    @Inject public ServiceEventSearchDataSource(FollowManager followManager, WatchManager watchManager,
      ShootrService shootrService, SessionRepository sessionRepository) {
        this.followManager = followManager;
        this.watchManager = watchManager;
        this.shootrService = shootrService;
        this.sessionRepository = sessionRepository;
    }

    @Override public List<EventSearchEntity> getDefaultEvents() {
        return loadEvents(null);
    }

    @Override public List<EventSearchEntity> getEvents(String query) {
        return loadEvents(query);
    }

    private List<EventSearchEntity> loadEvents(String query) {
        try {
            Map<Long, Integer> eventsWatchesCounts = getWatchersCountByEvents();
            return shootrService.getEventSearch(query, eventsWatchesCounts);
        } catch (SQLException | IOException e) {
            throw new RepositoryException(e);
        }
    }

    private Map<Long, Integer> getWatchersCountByEvents() throws SQLException {
        long currentUserId = sessionRepository.getCurrentUserId();

        List<Long> followingAndCurrentUserIds = followManager.getUserFollowingIds(currentUserId);
        followingAndCurrentUserIds.add(currentUserId);

        List<WatchEntity> watches = watchManager.getWatchesNotEndedFromUsers(followingAndCurrentUserIds);

        Map<Long, Integer> eventsWatchesCounts = new HashMap<>();
        for (WatchEntity watch : watches) {
            if (watch.isVisible()) {
                Integer currentCount = eventsWatchesCounts.get(watch.getIdEvent());
                if (currentCount != null) {
                    eventsWatchesCounts.put(watch.getIdEvent(), currentCount + 1);
                } else {
                    eventsWatchesCounts.put(watch.getIdEvent(), 1);
                }
            }
        }
        return eventsWatchesCounts;
    }
}
