package com.shootr.android.domain.repository;

import com.shootr.android.domain.User;

import java.util.List;

public interface UserRepository {

    List<User> getPeople();

    User getUserById(String id);

    User getUserByUsername(String username);

    List<User> getUsersByIds(List<String> ids);

    boolean isFollower(String userId);

    boolean isFollowing(String userId);

    User putUser(User user);
}
