package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.api.entity.mapper.UserApiEntityMapper;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.service.AuthApiService;
import com.shootr.mobile.data.api.service.UserApiService;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.UserNotFoundException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceUserDataSource implements UserDataSource {

    private final UserApiService userApiService;
    private final AuthApiService authApiService;
    private final UserApiEntityMapper userApiEntityMapper;
    private final SessionRepository sessionRepository;

    @Inject
    public ServiceUserDataSource(UserApiService userApiService, AuthApiService authApiService,
        UserApiEntityMapper userApiEntityMapper, SessionRepository sessionRepository) {
        this.userApiService = userApiService;
        this.authApiService = authApiService;
        this.userApiEntityMapper = userApiEntityMapper;
        this.sessionRepository = sessionRepository;
    }

    @Override public UserEntity putUser(UserEntity userEntity) {
        try {
            return userApiService.putUser(userApiEntityMapper.transform(userEntity));
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
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

    @Override public UserEntity getUserByUsername(String username) {
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

    @Override public StreamEntity updateWatch(UserEntity userEntity) {
        try {
            if (userEntity.getIdWatchingStream() != null) {
                return userApiService.watch(userEntity.getIdWatchingStream());
            } else {
                userApiService.unwatch();
                return null;
            }
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<UserEntity> getRelatedUsers(String idUser, Long timestamp) {
        try {
            return userApiService.getRelatedUsers(idUser, timestamp);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public UserEntity updateUser(UserEntity currentUserEntity)
      throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        try {
            return userApiService.putUser(userApiEntityMapper.transform(currentUserEntity));
        } catch (ApiException apiException) {
            if (ErrorInfo.EmailAlreadyExistsException == apiException.getErrorInfo()) {
                throw new EmailAlreadyExistsException(apiException);
            } else if (ErrorInfo.UserNameAlreadyExistsException == apiException.getErrorInfo()) {
                throw new UsernameAlreadyExistsException(apiException);
            } else {
                throw new ServerCommunicationException(apiException);
            }
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public List<UserEntity> findFriends(String searchString, Integer pageOffset, String locale) {
        try {
            return userApiService.search(searchString, pageOffset, locale);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void mute(String idUser) {
        try {
            userApiService.mute(idUser);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void unMute(String idUser) {
        try {
            userApiService.unMute(idUser);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void acceptTerms() {
        try {
            authApiService.acceptPrivacyTerms();
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<UserEntity> getEntitiesNotSynchronized() {
        throw new RuntimeException("Server DataSource can't access synchronization fields");
    }
}
