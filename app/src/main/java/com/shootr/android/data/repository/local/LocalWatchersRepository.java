package com.shootr.android.data.repository.local;

import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchersRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class LocalWatchersRepository implements WatchersRepository {

    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;

    @Inject
    public LocalWatchersRepository(SessionRepository sessionRepository, @Local UserRepository localUserRepository) {
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
    }

    @Override
    public Integer getWatchers(String eventId) {
        Integer watchers = getWatchersCountByEvents().get(eventId);
        if (watchers == null) {
            watchers = 0;
        }
        return watchers;
    }

    @Override
    public Map<String, Integer> getWatchers() {
        return getWatchersCountByEvents();
    }

    private Map<String, Integer> getWatchersCountByEvents() {
        List<User> people = localUserRepository.getPeople();

        Map<String, Integer> eventsWatchesCounts = new HashMap<>();
        for (User watcher : people) {
            if (watcher.getIdWatchingEvent() != null) {
                Integer currentCount = eventsWatchesCounts.get(watcher.getIdWatchingEvent());
                if (currentCount != null) {
                    eventsWatchesCounts.put(watcher.getIdWatchingEvent(), currentCount + 1);
                } else {
                    eventsWatchesCounts.put(watcher.getIdWatchingEvent(), 1);
                }
            }
        }
        return eventsWatchesCounts;
    }
}
