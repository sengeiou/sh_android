package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.api.entity.FollowsEntity;
import com.shootr.mobile.data.entity.BlockEntity;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.db.manager.FollowManager;
import com.shootr.mobile.db.manager.UserManager;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.util.List;
import javax.inject.Inject;

public class DatabaseFollowDataSource implements FollowDataSource {

    private final SessionRepository sessionRepository;
    private final FollowManager followManager;
    private final UserManager userManager;

    @Inject public DatabaseFollowDataSource(SessionRepository sessionRepository,
        FollowManager followManager, UserManager userManager) {
        this.sessionRepository = sessionRepository;
        this.followManager = followManager;
        this.userManager = userManager;
    }

    @Override public void putFollow(String idUser) {
        userManager.updateFollowing(idUser, true);
    }

    @Override public void removeFollow(String idUser) {
        userManager.updateFollowing(idUser, false);
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

    @Override public void putFailedFollow(FollowEntity followEntity) {
        followManager.saveFailedFollow(followEntity);
    }

    @Override public void deleteFailedFollows() {
        followManager.deleteFailedFollows();
    }

    @Override public List<FollowEntity> getEntitiesNotSynchronized() {
        return followManager.getFailedFollows();
    }
}
