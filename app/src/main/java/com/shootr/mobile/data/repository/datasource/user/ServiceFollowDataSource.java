package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.api.entity.FollowsEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.service.UserApiService;
import com.shootr.mobile.data.entity.BlockEntity;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.domain.exception.FollowingBlockedUserException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceFollowDataSource implements FollowDataSource {

    public static final String METHOD_NOT_VALID_FOR_SERVICE = "Method not valid for service";
    private final SessionRepository sessionRepository;
    private final UserApiService userApiService;

    @Inject public ServiceFollowDataSource(SessionRepository sessionRepository, UserApiService userApiService) {
        this.sessionRepository = sessionRepository;
        this.userApiService = userApiService;
    }

    @Override public void putFollow(String idUser) throws FollowingBlockedUserException {
        try {
            userApiService.follow(idUser);
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

    @Override public void removeFollow(String idUser) {
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

    @Override public void removeAllBlocks() {
        throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
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

    @Override public List<FollowEntity> getFollows(String idUser, Integer page, Long timestamp) {
        try {
            return userApiService.getFollows(idUser, page, timestamp);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public FollowsEntity getFollowings(String idUser, String[] type, Long maxTimestamp) {
        try {
            return userApiService.getFollowing(idUser, type, maxTimestamp);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public FollowsEntity getFollowers(String idUser, String[] type, Long maxTimestamp) {
        try {
            return userApiService.getFollowers(idUser, type, maxTimestamp);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public FollowsEntity getStreamFollowers(String idStream, String[] type, Long maxTimestamp) {
        try {
            return userApiService.getStreamFollowers(idStream, type, maxTimestamp);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void putFailedFollow(FollowEntity followEntity) {
        throw new IllegalArgumentException("this method should not have remote implementation");
    }

    @Override public void deleteFailedFollows() {
        throw new IllegalArgumentException("this method should not have remote implementation");
    }

    @Override public List<FollowEntity> getEntitiesNotSynchronized() {
        throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
    }
}
