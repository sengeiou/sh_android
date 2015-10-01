package com.shootr.android.data.repository.remote.cache;

import com.shootr.android.domain.User;
import java.util.List;
import javax.inject.Inject;

public class UserCache {

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
}
