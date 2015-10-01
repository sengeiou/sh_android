package com.shootr.android.data.repository.remote.cache;

import com.fewlaps.quitnowcache.QNCache;
import com.shootr.android.domain.User;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

public class UserCache {

    private QNCache peopleCache;

    @Inject
    public UserCache() {
    }

    public List<User> getPeople() {
        //TODO
        return null;
    }

    public void invalidatePeople() {
        //TODO
    }

    public void putPeople(List<User> people) {
        //TODO
    }
}
