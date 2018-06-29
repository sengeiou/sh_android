package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.db.manager.StreamManager;
import com.shootr.mobile.db.manager.UserManager;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class DatabaseUserDataSource implements UserDataSource {

    private final UserManager userManager;
    private final StreamManager streamManager;

    @Inject public DatabaseUserDataSource(UserManager userManager,
        StreamManager streamManager) {
        this.userManager = userManager;
        this.streamManager = streamManager;
    }

    @Override public UserEntity putUser(UserEntity userEntity) {
        userManager.saveUser(userEntity);
        return userEntity;
    }

    @Override public UserEntity getUser(String id) {
        return userManager.getUserByIdUser(id);
    }

    @Override public UserEntity getUserByUsername(String username) {
        return userManager.getUserByUsername(username);
    }

    @Override public List<UserEntity> getAllParticipants(String idStream, Long maxJoinDate) {
        throw new IllegalArgumentException("getAllParticipants has no local implementation");
    }

    @Override public List<UserEntity> findParticipants(String idStream, String query) {
        throw new IllegalStateException("Find Participants has no local implementation");
    }

    @Override public StreamEntity updateWatch(UserEntity userEntity) {
        //TODO save only watching fields?
        userManager.saveUser(userEntity);
        return userEntity.getIdWatchingStream() != null ? streamManager.getStreamById(
            userEntity.getIdWatchingStream()) : null;
    }

    @Override public List<UserEntity> getRelatedUsers(String idUser, Long timestamp) {
        return userManager.getRelatedUsers(idUser);
    }

    @Override public UserEntity updateUser(UserEntity currentOrNewUserEntity) {
        throw new IllegalArgumentException("this method has no local implementation");
    }

    @Override public List<UserEntity> findFriends(String searchString, Integer pageOffset, String locale)
      throws IOException {
        return userManager.searchUsers(searchString);
    }

    @Override public void mute(String idUser) {
        userManager.unMute(idUser);
    }

    @Override public void unMute(String idUser) {
        userManager.mute(idUser);
    }

    @Override public void acceptTerms() {
        throw new IllegalArgumentException("this method has no local implementation");
    }

    @Override public List<UserEntity> getEntitiesNotSynchronized() {
        return userManager.getUsersNotSynchronized();
    }
}
