package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.db.manager.FollowManager;
import com.shootr.mobile.db.manager.UserManager;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class DatabaseUserDataSource implements UserDataSource {

    private final FollowManager followManager;
    private final UserManager userManager;

    @Inject public DatabaseUserDataSource(FollowManager followManager, UserManager userManager) {
        this.followManager = followManager;
        this.userManager = userManager;
    }

    @Override public List<UserEntity> getFollowing(String userId, Integer page, Integer pageSize) {
        List<String> usersFollowingIds = followManager.getUserFollowingIds(userId);
        return userManager.getUsersByIds(usersFollowingIds);
    }

    @Override public UserEntity putUser(UserEntity userEntity) {
        userManager.saveUser(userEntity);
        return userEntity;
    }

    @Override public List<UserEntity> putUsers(List<UserEntity> userEntities) {
        userManager.saveUsers(userEntities);
        return userEntities;
    }

    @Override public UserEntity getUser(String id) {
        return userManager.getUserByIdUser(id);
    }

    @Override public boolean isFollower(String from, String who) {
        if (from == null || who == null) {
            return false;
        }
        FollowEntity follow = followManager.getFollowByUserIds(who, from);
        return follow != null;
    }

    @Override public boolean isFollowing(String who, String to) {
        if (who == null | to == null) {
            return false;
        }
        FollowEntity follow = followManager.getFollowByUserIds(who, to);
        return follow != null;
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

    @Override public void updateWatch(UserEntity userEntity) {
        //TODO save only watching fields?
        userManager.saveUser(userEntity);
    }

    @Override public List<UserEntity> getFollowers(String idUser, Integer page, Integer pageSize) {
        throw new IllegalArgumentException("this method has no local implementation");
    }

    @Override public List<UserEntity> getRelatedUsers(String idUser, Long timestamp) {
        return userManager.getRelatedUsers(idUser);
    }

    @Override public List<UserEntity> getRelatedUsersByIdStream(String idStream, String idUser) {
        return userManager.getRelatedUsersByIdStream(idStream, idUser);
    }

    @Override public UserEntity updateUser(UserEntity currentOrNewUserEntity) {
        throw new IllegalArgumentException("this method has no local implementation");
    }

    @Override public List<UserEntity> findFriends(String searchString, Integer pageOffset, String locale)
      throws IOException {
        return userManager.searchUsers(searchString);
    }

    @Override public List<UserEntity> getEntitiesNotSynchronized() {
        return userManager.getUsersNotSynchronized();
    }
}
