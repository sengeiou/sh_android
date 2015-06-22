package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

//TODO this implementation is a bit... raw. I'll keep it with the hope of removing it thanks to backend's mercy
public class ServiceEventSearchDataSource implements EventSearchDataSource {

    private final FollowManager followManager;
    private final ShootrService shootrService;
    private final UserManager userManager;
    private final SessionRepository sessionRepository;

    @Inject public ServiceEventSearchDataSource(FollowManager followManager, ShootrService shootrService,
      UserManager userManager, SessionRepository sessionRepository) {
        this.followManager = followManager;
        this.shootrService = shootrService;
        this.userManager = userManager;
        this.sessionRepository = sessionRepository;
    }

    @Override public List<EventSearchEntity> getDefaultEvents(String locale) {
        return loadEvents(null, locale);
    }

    @Override public List<EventSearchEntity> getEvents(String query, String locale) {
        return loadEvents(query, locale);
    }

    @Override public void putDefaultEvents(List<EventSearchEntity> transform) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public void deleteDefaultEvents() {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public EventSearchEntity getEventResult(String idEvent) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    private List<EventSearchEntity> loadEvents(String query, String locale) {
        try {
            Map<String, Integer> eventsWatchesCounts = getWatchersCountByEvents();
            return shootrService.getEventSearch(query, eventsWatchesCounts, locale);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
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
