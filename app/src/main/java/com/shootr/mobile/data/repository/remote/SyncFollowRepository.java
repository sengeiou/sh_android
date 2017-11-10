package com.shootr.mobile.data.repository.remote;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.entity.BlockEntity;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.mapper.FollowsEntityMapper;
import com.shootr.mobile.data.repository.datasource.user.FollowDataSource;
import com.shootr.mobile.data.repository.remote.cache.UserCache;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.domain.exception.FollowingBlockedUserException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.model.Follows;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.follow.FollowRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SyncFollowRepository implements FollowRepository, SyncableRepository {

  private static final int PAGE_SIZE = 100;
  private final SessionRepository sessionRepository;
  private final FollowDataSource localFollowDataSource;
  private final FollowDataSource remoteFollowDataSource;
  private final FollowsEntityMapper followingEntityMapper;
  private final SyncTrigger syncTrigger;
  private final UserCache userCache;

  @Inject public SyncFollowRepository(SessionRepository sessionRepository,
      @Local FollowDataSource localFollowDataSource,
      @Remote FollowDataSource remoteFollowDataSource, FollowsEntityMapper followingEntityMapper,
      SyncTrigger syncTrigger, UserCache userCache) {
    this.sessionRepository = sessionRepository;
    this.localFollowDataSource = localFollowDataSource;
    this.remoteFollowDataSource = remoteFollowDataSource;
    this.followingEntityMapper = followingEntityMapper;
    this.syncTrigger = syncTrigger;
    this.userCache = userCache;
  }

  @Override public void follow(String idUser) throws FollowingBlockedUserException {
    try {
      remoteFollowDataSource.putFollow(idUser);
      userCache.invalidatePeople();
      syncTrigger.triggerSync();
    } catch (ServerCommunicationException e) {
      localFollowDataSource.putFailedFollow(createFailedFollow(idUser, true));
      syncTrigger.notifyNeedsSync(this);
    }
  }

  @NonNull private FollowEntity createFailedFollow(String idUser, boolean isFollowing) {
    FollowEntity followEntity = new FollowEntity();
    followEntity.setFollowing(isFollowing);
    followEntity.setIdFollowed(idUser);
    followEntity.setType("USER");
    return followEntity;
  }

  @Override public void unfollow(String idUser) {
    try {
      remoteFollowDataSource.removeFollow(idUser);
      userCache.invalidatePeople();
      syncTrigger.triggerSync();
    } catch (ServerCommunicationException e) {
      localFollowDataSource.putFailedFollow(createFailedFollow(idUser, false));
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
    localFollowDataSource.removeAllBlocks();
    localFollowDataSource.putBlockeds(blockeds);
    List<String> blockedIds = new ArrayList<>(blockeds.size());
    for (BlockEntity blocked : blockeds) {
      blockedIds.add(blocked.getIdBlockedUser());
    }
    return blockedIds;
  }

  @Override public Follows getFollowing(String idUser, String[] type, Long maxTimestamp) {
    return followingEntityMapper.map(
        remoteFollowDataSource.getFollowings(idUser, type, maxTimestamp),
        sessionRepository.getCurrentUserId());
  }

  @Override public Follows getFollowers(String idUser, String[] type, Long maxTimestamp) {
    return followingEntityMapper.map(
        remoteFollowDataSource.getFollowers(idUser, type, maxTimestamp),
        sessionRepository.getCurrentUserId());
  }

  @Override public Follows getStreamFollowers(String idStream, String[] type, Long maxTimestamp) {
    return followingEntityMapper.map(
        remoteFollowDataSource.getStreamFollowers(idStream, type, maxTimestamp),
        sessionRepository.getCurrentUserId());
  }

  @Override public void dispatchSync() {
    List<FollowEntity> pendingEntities = localFollowDataSource.getEntitiesNotSynchronized();
    localFollowDataSource.deleteFailedFollows();
    for (FollowEntity entity : pendingEntities) {
      if (entity.isFollowing()) {
        followFailed(entity);
      } else {
        unfollow(entity.getIdFollowedUser());
      }
    }
  }

  private void followFailed(FollowEntity entity) {
    try {
      follow(entity.getIdFollowedUser());
    } catch (FollowingBlockedUserException error) {
      error.printStackTrace();
    }
  }

  @NonNull protected BlockEntity createBlock(String idUser) {
    BlockEntity blockEntity = new BlockEntity();
    blockEntity.setIdUser(sessionRepository.getCurrentUserId());
    blockEntity.setIdBlockedUser(idUser);
    return blockEntity;
  }
}
