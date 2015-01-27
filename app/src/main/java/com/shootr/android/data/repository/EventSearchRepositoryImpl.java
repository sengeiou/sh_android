package com.shootr.android.data.repository;

import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.EventSearchEntityMapper;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.exception.RepositoryException;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class EventSearchRepositoryImpl implements EventSearchRepository {

    private final SessionRepository sessionRepository;
    private final FollowManager followManager;
    private final WatchManager watchManager;
    private final ShootrService shootrService;
    private final EventSearchEntityMapper eventSearchEntityMapper;

    @Inject public EventSearchRepositoryImpl(SessionRepository sessionRepository, FollowManager followManager,
      WatchManager watchManager, ShootrService shootrService, EventSearchEntityMapper eventSearchEntityMapper) {
        this.sessionRepository = sessionRepository;
        this.followManager = followManager;
        this.watchManager = watchManager;
        this.shootrService = shootrService;
        this.eventSearchEntityMapper = eventSearchEntityMapper;
    }

    @Override public List<EventSearchResult> getDefaultEvents() {
        return loadEvents(null);
    }

    @Override public List<EventSearchResult> getEvents(String query) {
        return loadEvents(query);
    }

    private List<EventSearchResult> loadEvents(String query) {
        try {
            Map<Long, Integer> eventsWatchesCounts = getWatchersCountByEvents();
            List<EventSearchEntity> eventSearchEntities = shootrService.getEventSearch(query, eventsWatchesCounts);
            return eventSearchEntityMapper.transform(eventSearchEntities);
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
            if (watch.getStatus().equals(1L)) {
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
