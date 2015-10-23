package com.shootr.android.data.repository.local;

import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchersRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class LocalWatchersRepository implements WatchersRepository {

    private final UserRepository localUserRepository;

    @Inject
    public LocalWatchersRepository(@Local UserRepository localUserRepository) {
        this.localUserRepository = localUserRepository;
    }

    @Override
    public Integer getWatchers(String streamId) {
        Integer watchers = getWatchersCountByStreams().get(streamId);
        if (watchers == null) {
            watchers = 0;
        }
        return watchers;
    }

    @Override
    public Map<String, Integer> getWatchers() {
        return getWatchersCountByStreams();
    }

    private Map<String, Integer> getWatchersCountByStreams() {
        List<User> people = localUserRepository.getPeople();

        Map<String, Integer> streamsWatchesCounts = new HashMap<>();
        for (User watcher : people) {
            if (watcher.getIdWatchingStream() != null) {
                Integer currentCount = streamsWatchesCounts.get(watcher.getIdWatchingStream());
                if (currentCount != null) {
                    streamsWatchesCounts.put(watcher.getIdWatchingStream(), currentCount + 1);
                } else {
                    streamsWatchesCounts.put(watcher.getIdWatchingStream(), 1);
                }
            }
        }
        return streamsWatchesCounts;
    }
}
