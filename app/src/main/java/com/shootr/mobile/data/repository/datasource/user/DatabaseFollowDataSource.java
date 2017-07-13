package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.api.entity.FollowsEntity;
import com.shootr.mobile.data.entity.BlockEntity;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.db.manager.FollowManager;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.util.List;
import javax.inject.Inject;

public class DatabaseFollowDataSource implements FollowDataSource {

    private final SessionRepository sessionRepository;
    private final FollowManager followManager;

    @Inject public DatabaseFollowDataSource(SessionRepository sessionRepository, FollowManager followManager) {
        this.sessionRepository = sessionRepository;
        this.followManager = followManager;
    }

    @Override public List<FollowEntity> putFollows(List<FollowEntity> followEntities) {
        followManager.saveFollowsFromServer(followEntities);
        return followEntities;
    }

    @Override public FollowEntity putFollow(FollowEntity followEntity) {
        followManager.saveFollow(followEntity);
        return followEntity;
    }

    @Override public void removeFollow(String idUser) {
        followManager.deleteFollow(idUser, sessionRepository.getCurrentUserId());
    }

    @Override public void block(BlockEntity block) {
        followManager.saveBlock(block);
    }

    @Override public void removeBlock(String idUser) {
        followManager.deleteBlock(sessionRepository.getCurrentUserId(), idUser);
    }

    @Override public void removeAllBlocks() {
        followManager.removeAllBlocks();
    }

    @Override public List<BlockEntity> getBlockeds() {
        return followManager.getBlockeds();
    }

    @Override public void putBlockeds(List<BlockEntity> blockeds) {
        followManager.saveBlockedsFromServer(blockeds);
    }

    @Override public List<String> getMutuals() {
        return followManager.getMutuals();
    }

    @Override public List<FollowEntity> getFollows(String idUser, Integer page, Long timestamp) {
        throw new IllegalArgumentException("no local implementation");
    }

    @Override
    public FollowsEntity getFollowings(String idUser, String[] type, Long maxTimestamp) {
        throw new IllegalArgumentException("no local implementation");
    }

    @Override public FollowsEntity getFollowers(String idUser, String[] type, Long maxTimestamp) {
        throw new IllegalArgumentException("no local implementation");
    }

    @Override
    public FollowsEntity getStreamFollowers(String idStream, String[] type, Long maxTimestamp) {
        throw new IllegalArgumentException("no local implementation");
    }

    @Override public List<FollowEntity> getEntitiesNotSynchronized() {
        return followManager.getFollowsNotSynchronized();
    }
}
