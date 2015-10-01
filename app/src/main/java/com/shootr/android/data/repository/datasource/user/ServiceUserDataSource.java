package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.api.entity.mapper.UserApiEntityMapper;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.UserApiService;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceUserDataSource implements UserDataSource {

    private final ShootrService service;
    private final UserApiService userApiService;
    private final UserApiEntityMapper userApiEntityMapper;

    @Inject public ServiceUserDataSource(ShootrService service, UserApiService userApiService,
      UserApiEntityMapper userApiEntityMapper) {
        this.service = service;
        this.userApiService = userApiService;
        this.userApiEntityMapper = userApiEntityMapper;
    }

    @Override public List<UserEntity> getFollowing(String userId) {
        try {
            return userApiService.getFollowing(userId);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public UserEntity putUser(UserEntity userEntity) {
        try {
            return userApiService.putUser(userApiEntityMapper.transform(userEntity));
        } catch (IOException | ApiException e) {
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

    @Override public List<UserEntity> getAllParticipants(String idStream, Long maxJoinDate) {
        try {
            return userApiService.getAllParticipants(idStream, maxJoinDate);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<UserEntity> findParticipants(String idStream, String query) {
        try {
            return userApiService.findParticipants(idStream, query);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public void updateWatch(UserEntity userEntity) {
        try {
            if (userEntity.getIdWatchingStream() != null) {
                userApiService.watch(userEntity.getIdWatchingStream());
            } else {
                userApiService. unwatch();
            }
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<UserEntity> getEntitiesNotSynchronized() {
        throw new RuntimeException("Server DataSource can't access synchronization fields");
    }
}
