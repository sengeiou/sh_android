package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.exception.RepositoryException;
import com.shootr.android.domain.repository.SessionRepository;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class DatabaseUserDataSource implements UserDataSource {

    private final FollowManager followManager;
    private final UserManager userManager;

    @Inject public DatabaseUserDataSource(SessionRepository sessionRepository, FollowManager followManager, UserManager userManager) {
        this.followManager = followManager;
        this.userManager = userManager;
    }

    @Override public List<UserEntity> getFollowing(Long userId) {
        try {
            List<Long> usersFollowingIds = followManager.getUserFollowingIds(userId);
            return userManager.getUsersByIds(usersFollowingIds);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override public UserEntity putUser(UserEntity userEntity) {
        userManager.saveUser(userEntity);
        return userEntity;
    }

    @Override public List<UserEntity> putUsers(List<UserEntity> userEntities) {
        userManager.saveUsers(userEntities);
        return userEntities;
    }

    @Override public boolean isFollower(Long from, Long who) {
        FollowEntity follow = followManager.getFollowByUserIds(who, from);
        return follow != null;
    }

    @Override public boolean isFollowing(Long who, Long to) {
        FollowEntity follow = followManager.getFollowByUserIds(who, to);
        return follow != null;
    }
}
