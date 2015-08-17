package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceUserDataSource implements UserDataSource {

    private final ShootrService service;

    @Inject public ServiceUserDataSource(ShootrService service) {
        this.service = service;
    }

    @Override public List<UserEntity> getFollowing(String userId) {
        try {
            return service.getFollowing(userId, 0L);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public UserEntity putUser(UserEntity userEntity) {
        try {
            return service.saveUserProfile(userEntity);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<UserEntity> putUsers(List<UserEntity> userEntities) {
        throw new RuntimeException("Method not implemented");
    }

    @Override public UserEntity getUser(String id) {
        try {
            return service.getUserByIdUser(id);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public boolean isFollower(String from, String who) {
        throw new RuntimeException("Method not implemented for service.");
    }

    @Override public boolean isFollowing(String who, String to) {
        throw new RuntimeException("Method not implemented for service.");
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        try {
            return service.getUserByUsername(username);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<UserEntity> getEntitiesNotSynchronized() {
        throw new RuntimeException("Server DataSource can't access synchronization fields");
    }
}
