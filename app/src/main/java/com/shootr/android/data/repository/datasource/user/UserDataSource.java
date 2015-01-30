package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.UserEntity;
import java.util.List;

public interface UserDataSource {

    List<UserEntity> getFollowing(Long userId);

    boolean isFollower(Long from, Long who);

    boolean isFollowing(Long who, Long to);
}
