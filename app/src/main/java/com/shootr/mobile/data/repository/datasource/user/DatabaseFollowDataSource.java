package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.entity.BanEntity;
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

    @Override public List<BlockEntity> getBlockeds() {
        return followManager.getBlockeds();
    }

    @Override public void putBlockeds(List<BlockEntity> blockeds) {
        followManager.saveBlockedsFromServer(blockeds);
    }

    @Override public void ban(BanEntity banEntity) {
        followManager.saveBan(banEntity);
    }

    @Override public List<BanEntity> getBanneds() {
        return followManager.getBans();
    }

    @Override public void putBanneds(List<BanEntity> banneds) {
        followManager.saveBansFromServer(banneds);
    }

    @Override public void unban(String idUser) {
        followManager.deleteBan(sessionRepository.getCurrentUserId(), idUser);
    }

    @Override public List<String> getMutuals() {
        return followManager.getMutuals();
    }

    @Override public List<FollowEntity> getEntitiesNotSynchronized() {
        return followManager.getFollowsNotSynchronized();
    }
}
