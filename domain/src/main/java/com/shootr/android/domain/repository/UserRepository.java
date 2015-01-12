package com.shootr.android.domain.repository;

import com.shootr.android.domain.User;
import java.util.List;

public interface UserRepository {

    interface UserListCallback {
        void onLoaded(List<User> userList);
    }

    void getPeople(UserListCallback callback);

    boolean isFollower(Long userId);

    boolean isFollowing(Long userId);
}
