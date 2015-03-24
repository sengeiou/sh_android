package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.repository.datasource.SyncableDataSource;
import java.util.List;

public interface UserDataSource  extends SyncableDataSource<UserEntity>{

    List<UserEntity> getFollowing(Long userId);

    UserEntity putUser(UserEntity userEntity);

    List<UserEntity> putUsers(List<UserEntity> userEntities);

    UserEntity getUser(Long id);

    List<UserEntity> getUsers(List<Long> userIds);

    boolean isFollower(Long from, Long who);

    boolean isFollowing(Long who, Long to);
}
