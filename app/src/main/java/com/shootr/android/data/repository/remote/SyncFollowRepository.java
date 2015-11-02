package com.shootr.android.data.repository.remote;

import android.support.annotation.NonNull;
import com.shootr.android.data.entity.BlockEntity;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.repository.datasource.user.FollowDataSource;
import com.shootr.android.data.repository.remote.cache.UserCache;
import com.shootr.android.data.repository.sync.SyncTrigger;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.FollowRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class SyncFollowRepository implements FollowRepository, SyncableRepository {

    private final SessionRepository sessionRepository;
    private final FollowDataSource localFollowDataSource;
    private final FollowDataSource remoteFollowDataSource;
    private final SyncTrigger syncTrigger;
    private final UserCache userCache;

    @Inject
    public SyncFollowRepository(SessionRepository sessionRepository,
      @Local FollowDataSource localFollowDataSource,
      @Remote FollowDataSource remoteFollowDataSource,
      SyncTrigger syncTrigger, UserCache userCache) {
        this.sessionRepository = sessionRepository;
        this.localFollowDataSource = localFollowDataSource;
        this.remoteFollowDataSource = remoteFollowDataSource;
        this.syncTrigger = syncTrigger;
        this.userCache = userCache;
    }

    @Override
    public void follow(String idUser) {
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

    @Override
    public void unfollow(String idUser) {
        try {
            remoteFollowDataSource.removeFollow(idUser);
            localFollowDataSource.removeFollow(idUser);
            userCache.invalidatePeople();
        } catch (ServerCommunicationException e) {
            FollowEntity deletedFollow = createFollow(idUser);
            deletedFollow.setSynchronizedStatus(Synchronized.SYNC_DELETED);
            localFollowDataSource.putFollow(deletedFollow);
            syncTrigger.notifyNeedsSync(this);
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
        localFollowDataSource.putBlockeds(blockeds);
        List<String> blockedIds = new ArrayList<>(blockeds.size());
        for (BlockEntity blocked : blockeds) {
            blockedIds.add(blocked.getIdBlockedUser());
        }
        return blockedIds;
    }

    @Override
    public void dispatchSync() {
        List<FollowEntity> pendingEntities = localFollowDataSource.getEntitiesNotSynchronized();
        for (FollowEntity entity : pendingEntities) {
            if (Synchronized.SYNC_DELETED.equals(entity.getSynchronizedStatus())) {
                remoteFollowDataSource.removeFollow(entity.getFollowedUser());
                localFollowDataSource.removeFollow(entity.getFollowedUser());
            } else {
                remoteFollowDataSource.putFollow(entity);
                entity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
                localFollowDataSource.putFollow(entity);
            }
            userCache.invalidatePeople();
        }
    }

    @NonNull
    protected FollowEntity createFollow(String idUser) {
        FollowEntity followEntity = new FollowEntity();
        followEntity.setIdUser(sessionRepository.getCurrentUserId());
        followEntity.setFollowedUser(idUser);
        Date now = new Date();
        followEntity.setBirth(now);
        followEntity.setModified(now);
        return followEntity;
    }

    @NonNull
    protected BlockEntity createBlock(String idUser) {
        BlockEntity blockEntity = new BlockEntity();
        blockEntity.setIdUser(sessionRepository.getCurrentUserId());
        blockEntity.setIdBlockedUser(idUser);
        return blockEntity;
    }
}
