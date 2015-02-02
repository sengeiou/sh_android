package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.exception.RepositoryException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceUserDataSource implements UserDataSource {

    public final ShootrService service;

    @Inject public ServiceUserDataSource(ShootrService service) {
        this.service = service;
    }

    @Override public List<UserEntity> getFollowing(Long userId) {
        try {
            return service.getFollowing(userId, 0L);
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    @Override public UserEntity putUser(UserEntity userEntity) {
        throw new RuntimeException("Method not implemented");
    }

    @Override public List<UserEntity> putUsers(List<UserEntity> userEntities) {
        throw new RuntimeException("Method not implemented");
    }

    @Override public boolean isFollower(Long from, Long who) {
        throw new RuntimeException("Method not implemented for service.");
    }

    @Override public boolean isFollowing(Long who, Long to) {
        throw new RuntimeException("Method not implemented for service.");
    }
}
