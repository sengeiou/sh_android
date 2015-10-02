package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.api.entity.mapper.UserApiEntityMapper;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.UserApiService;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.UserNotFoundException;
import com.shootr.android.domain.repository.SessionRepository;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceUserDataSource implements UserDataSource {

    private final UserApiService userApiService;
    private final UserApiEntityMapper userApiEntityMapper;
    private final SessionRepository sessionRepository;

    @Inject public ServiceUserDataSource(UserApiService userApiService,
      UserApiEntityMapper userApiEntityMapper,
      SessionRepository sessionRepository) {
        this.userApiService = userApiService;
        this.userApiEntityMapper = userApiEntityMapper;
        this.sessionRepository = sessionRepository;
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
            if (id.equals(sessionRepository.getCurrentUserId())) {
                return userApiService.getUser();
            } else {
                return userApiService.getUser(id);
            }
        } catch (IOException | ApiException e) {
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
            return userApiService.getUserByUsername(username);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        } catch (ApiException e) {
            if (e.getErrorInfo().httpCode() == 404) {
                throw new UserNotFoundException(username);
            } else {
                throw new ServerCommunicationException(e);
            }
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
