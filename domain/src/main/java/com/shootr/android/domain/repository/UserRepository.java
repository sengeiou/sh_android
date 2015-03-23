package com.shootr.android.domain.repository;

import com.shootr.android.domain.User;
import java.util.List;

public interface UserRepository {

    List<User> getPeople();

    User getUserById(Long id);

    List<User> getUsersByIds(List<Long> ids);

    boolean isFollower(Long userId);

    boolean isFollowing(Long userId);

    void putUser(User user);
}
