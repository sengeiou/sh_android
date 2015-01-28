package com.shootr.android.data.repository.remote;

import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.UserRepository;
import java.util.List;
import javax.inject.Inject;

public class SyncUserRepository implements UserRepository, SyncableRepository {

    @Inject public SyncUserRepository() {
    }

    @Override public void getPeople(UserListCallback callback) {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public List<User> getPeople() {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public boolean isFollower(Long userId) {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public boolean isFollowing(Long userId) {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public void dispatchSync() {
        throw new RuntimeException("Method not implemented yet!");
    }
}
