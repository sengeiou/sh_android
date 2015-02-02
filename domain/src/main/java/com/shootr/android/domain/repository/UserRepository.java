package com.shootr.android.domain.repository;

import com.shootr.android.domain.User;
import java.util.List;

public interface UserRepository {

    @Deprecated
    interface UserListCallback {
        void onLoaded(List<User> userList);
    }

    List<User> getPeople();

    boolean isFollower(Long userId);

    boolean isFollowing(Long userId);
}
