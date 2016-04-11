package com.shootr.mobile.data.repository.remote.cache;

import com.fewlaps.quitnowcache.QNCache;
import com.shootr.mobile.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class UserCache {

    public static final String GET_PEOPLE = "getPeople";
    public static final int GET_PEOPLE_KEEP_ALIVE_SECONDS = 20 * 1000;
    private final QNCache<List<User>> userCache;

    @Inject public UserCache(QNCache userCache) {
        this.userCache = userCache;
    }

    public List<User> getPeople() {
        List<User> people = userCache.get(GET_PEOPLE);
        Timber.d("getPeople cache %s", people != null ? "valid" : "invalid");
        if (people != null) {
            List<User> cachedPeople = new ArrayList<>(people.size());
            cachedPeople.addAll(people);
            return cachedPeople;
        } else {
            return null;
        }
    }

    public void invalidatePeople() {
        userCache.remove(GET_PEOPLE);
    }

    public void putPeople(List<User> people) {
        userCache.set(GET_PEOPLE, people, GET_PEOPLE_KEEP_ALIVE_SECONDS);
    }
}
