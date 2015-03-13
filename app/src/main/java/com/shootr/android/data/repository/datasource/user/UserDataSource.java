package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.UserEntity;
import java.util.List;

public interface UserDataSource {

    List<UserEntity> getFollowing(Long userId);

    UserEntity putUser(UserEntity userEntity);

    List<UserEntity> putUsers(List<UserEntity> userEntities);

    UserEntity getUser(Long id);

    boolean isFollower(Long from, Long who);

    boolean isFollowing(Long who, Long to);
}
