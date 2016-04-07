package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.service.UserApiService;
import com.shootr.mobile.data.entity.BanEntity;
import com.shootr.mobile.data.entity.BlockEntity;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.domain.exception.FollowingBlockedUserException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.SessionRepository;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkArgument;

public class ServiceFollowDataSource implements FollowDataSource {

    public static final String METHOD_NOT_VALID_FOR_SERVICE = "Method not valid for service";
    private final SessionRepository sessionRepository;
    private final UserApiService userApiService;

    @Inject
    public ServiceFollowDataSource(SessionRepository sessionRepository, UserApiService userApiService) {
        this.sessionRepository = sessionRepository;
        this.userApiService = userApiService;
    }

    @Override
    public List<FollowEntity> putFollows(List<FollowEntity> followEntities) {
        throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
    }

    @Override
    public FollowEntity putFollow(FollowEntity followEntity) throws FollowingBlockedUserException {
        checkArgument(followEntity.getIdUser().equals(sessionRepository.getCurrentUserId()),
          "Only follows from the current user are allowed in service");

        try {
            userApiService.follow(followEntity.getFollowedUser());
            return followEntity;
        } catch (ApiException apiException) {
            if (ErrorInfo.FollowingBlockedUserException == apiException.getErrorInfo()) {
                throw new FollowingBlockedUserException(apiException);
            } else {
                throw new ServerCommunicationException(apiException);
            }
        } catch (IOException networkError) {
            throw new ServerCommunicationException(networkError);
        }
    }

    @Override
    public void removeFollow(String idUser) {
        try {
            userApiService.unfollow(idUser);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void block(BlockEntity block) {
        try {
            userApiService.block(block.getIdBlockedUser());
        } catch (IOException | ApiException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public void removeBlock(String idUser) {
        try {
            userApiService.unblock(idUser);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<BlockEntity> getBlockeds() {
        try {
            return userApiService.getBlockedIdUsers();
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void putBlockeds(List<BlockEntity> blockeds) {
        throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
    }

    @Override public void ban(BanEntity banEntity) {
        try {
            userApiService.ban(banEntity.getIdBannedUser());
        } catch (IOException | ApiException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public List<BanEntity> getBanneds() {
        try {
            return userApiService.getBannedIdUsers();
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void putBanneds(List<BanEntity> banneds) {
        throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
    }

    @Override public void unban(String idUser) {
        try {
            userApiService.unban(idUser);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public List<FollowEntity> getEntitiesNotSynchronized() {
        throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
    }
}
