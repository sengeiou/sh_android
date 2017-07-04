package com.shootr.mobile.data.repository.remote;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.entity.BlockEntity;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.data.mapper.FollowingEntityMapper;
import com.shootr.mobile.data.repository.datasource.user.FollowDataSource;
import com.shootr.mobile.data.repository.remote.cache.UserCache;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.domain.exception.FollowingBlockedUserException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.model.Following;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.follow.FollowRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class SyncFollowRepository implements FollowRepository, SyncableRepository {

    private static final int PAGE_SIZE = 100;
    private final SessionRepository sessionRepository;
    private final FollowDataSource localFollowDataSource;
    private final FollowDataSource remoteFollowDataSource;
    private final FollowingEntityMapper followingEntityMapper;
    private final SyncTrigger syncTrigger;
    private final UserCache userCache;

    @Inject
    public SyncFollowRepository(SessionRepository sessionRepository, @Local FollowDataSource localFollowDataSource,
        @Remote FollowDataSource remoteFollowDataSource,
        FollowingEntityMapper followingEntityMapper, SyncTrigger syncTrigger, UserCache userCache) {
        this.sessionRepository = sessionRepository;
        this.localFollowDataSource = localFollowDataSource;
        this.remoteFollowDataSource = remoteFollowDataSource;
        this.followingEntityMapper = followingEntityMapper;
        this.syncTrigger = syncTrigger;
        this.userCache = userCache;
    }

    @Override public void follow(String idUser) throws FollowingBlockedUserException {
        FollowEntity followEntity = createFollow(idUser);
        try {
            remoteFollowDataSource.putFollow(followEntity);
            followEntity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
            localFollowDataSource.putFollow(followEntity);
            userCache.invalidatePeople();
        } catch (ServerCommunicationException e) {
            followEntity.setSynchronizedStatus(Synchronized.SYNC_UPDATED);
            localFollowDataSource.putFollow(followEntity);
            syncTrigger.notifyNeedsSync(this);
        }
    }

    @Override public void unfollow(String idUser) {
        try {
            remoteFollowDataSource.removeFollow(idUser);
            localFollowDataSource.removeFollow(idUser);
            userCache.invalidatePeople();
        } catch (ServerCommunicationException e) {
            deleteFollow(idUser);
        }
    }

    private void deleteFollow(String idUser) {
        try {
            FollowEntity deletedFollow = createFollow(idUser);
            deletedFollow.setSynchronizedStatus(Synchronized.SYNC_DELETED);
            localFollowDataSource.putFollow(deletedFollow);
            syncTrigger.notifyNeedsSync(this);
        } catch (FollowingBlockedUserException error) {
            error.printStackTrace();
        }
    }

    @Override public void block(String idUser) {
        BlockEntity blockEntity = createBlock(idUser);
        remoteFollowDataSource.block(blockEntity);
    }

    @Override public void unblock(String idUser) {
        remoteFollowDataSource.removeBlock(idUser);
    }

    @Override public List<String> getBlockedIdUsers() {
        List<BlockEntity> blockeds = remoteFollowDataSource.getBlockeds();
        localFollowDataSource.removeAllBlocks();
        localFollowDataSource.putBlockeds(blockeds);
        List<String> blockedIds = new ArrayList<>(blockeds.size());
        for (BlockEntity blocked : blockeds) {
            blockedIds.add(blocked.getIdBlockedUser());
        }
        return blockedIds;
    }

    @Override public List<String> getMutualIdUsers() {
        Integer page = 0;
        List<String> followedIdUsers = new ArrayList<>();
        List<FollowEntity> follows = remoteFollowDataSource.getFollows(sessionRepository.getCurrentUserId(), page, 0L);
        while (follows.size() == PAGE_SIZE) {
            localFollowDataSource.putFollows(follows);
            page++;
            follows = remoteFollowDataSource.getFollows(sessionRepository.getCurrentUserId(), page, 0L);
        }
        for (FollowEntity follow : follows) {
            if (follow.isFriend() == 1L) {
                followedIdUsers.add(follow.getIdFollowedUser());
            }
        }
        return followedIdUsers;
    }

    @Override public Following getFollowing(String idUser, String[] type, Long maxTimestamp) {
        return followingEntityMapper.map(remoteFollowDataSource.getFollowings(idUser, type, maxTimestamp));
    }

    @Override public void dispatchSync() {
        List<FollowEntity> pendingEntities = localFollowDataSource.getEntitiesNotSynchronized();
        for (FollowEntity entity : pendingEntities) {
            if (Synchronized.SYNC_DELETED.equals(entity.getSynchronizedStatus())) {
                remoteFollowDataSource.removeFollow(entity.getIdFollowedUser());
                localFollowDataSource.removeFollow(entity.getIdFollowedUser());
            } else {
                syncEntities(entity);
            }
            userCache.invalidatePeople();
        }
    }

    private void syncEntities(FollowEntity entity) {
        try {
            remoteFollowDataSource.putFollow(entity);
            entity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
            localFollowDataSource.putFollow(entity);
        } catch (FollowingBlockedUserException error) {
            error.printStackTrace();
        }
    }

    @NonNull protected FollowEntity createFollow(String idUser) {
        FollowEntity followEntity = new FollowEntity();
        followEntity.setIdUser(sessionRepository.getCurrentUserId());
        followEntity.setIdFollowedUser(idUser);
        Date now = new Date();
        followEntity.setBirth(now);
        followEntity.setModified(now);
        return followEntity;
    }

    @NonNull protected BlockEntity createBlock(String idUser) {
        BlockEntity blockEntity = new BlockEntity();
        blockEntity.setIdUser(sessionRepository.getCurrentUserId());
        blockEntity.setIdBlockedUser(idUser);
        return blockEntity;
    }

}
