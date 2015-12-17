package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.repository.datasource.SyncableDataSource;
import java.util.List;

public interface UserDataSource  extends SyncableDataSource<UserEntity>{

    List<UserEntity> getFollowing(String userId, Integer page, Integer pageSize);

    UserEntity putUser(UserEntity userEntity);

    List<UserEntity> putUsers(List<UserEntity> userEntities);

    UserEntity getUser(String id);

    boolean isFollower(String from, String who);

    boolean isFollowing(String who, String to);

    UserEntity getUserByUsername(String username);

    List<UserEntity> getAllParticipants(String idStream, Long maxJoinDate);

    List<UserEntity> findParticipants(String idStream, String query);

    void updateWatch(UserEntity userEntity);

    List<UserEntity> getFollowers(String idUser, Integer page, Integer pageSize);
}
