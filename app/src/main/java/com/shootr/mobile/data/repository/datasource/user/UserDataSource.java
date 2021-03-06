package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.repository.datasource.SyncableDataSource;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import java.io.IOException;
import java.util.List;

public interface UserDataSource extends SyncableDataSource<UserEntity> {

    UserEntity putUser(UserEntity userEntity);

    UserEntity getUser(String id);

    UserEntity getUserByUsername(String username);

    List<UserEntity> getAllParticipants(String idStream, Long maxJoinDate);

    List<UserEntity> findParticipants(String idStream, String query);

    StreamEntity updateWatch(UserEntity userEntity);

    List<UserEntity> getRelatedUsers(String idUser, Long timestamp);

    UserEntity updateUser(UserEntity currentOrNewUserEntity)
      throws EmailAlreadyExistsException, UsernameAlreadyExistsException;

    List<UserEntity> findFriends(String searchString, Integer pageOffset, String locale) throws IOException;

    void mute(String idUser);

    void unMute(String idUser);

    void acceptTerms();
}
