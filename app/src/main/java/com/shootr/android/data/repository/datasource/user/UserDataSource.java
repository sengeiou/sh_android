package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.repository.datasource.SyncableDataSource;
import java.util.List;

public interface UserDataSource  extends SyncableDataSource<UserEntity>{

    List<UserEntity> getFollowing(String userId);

    UserEntity putUser(UserEntity userEntity);

    List<UserEntity> putUsers(List<UserEntity> userEntities);

    UserEntity getUser(String id);

    List<UserEntity> getUsers(List<String> userIds);

    boolean isFollower(String from, String who);

    boolean isFollowing(String who, String to);
}
