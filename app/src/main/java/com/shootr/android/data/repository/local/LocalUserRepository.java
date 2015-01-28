package com.shootr.android.data.repository.local;

import com.shootr.android.data.repository.UserRepositoryImpl;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.UserRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalUserRepository implements UserRepository {

    private final UserRepositoryImpl oldUserRepository;

    @Inject public LocalUserRepository(UserRepositoryImpl oldUserRepository) {
        this.oldUserRepository = oldUserRepository;
    }

    @Override public void getPeople(UserListCallback callback) {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public List<User> getPeople() {
        return oldUserRepository.getPeople();
    }

    @Override public boolean isFollower(Long userId) {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public boolean isFollowing(Long userId) {
        throw new RuntimeException("Method not implemented yet!");
    }
}
