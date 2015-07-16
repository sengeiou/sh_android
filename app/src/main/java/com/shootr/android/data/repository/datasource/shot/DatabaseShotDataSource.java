package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotDetailEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.domain.StreamTimelineParameters;
import com.shootr.android.domain.exception.RepositoryException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class DatabaseShotDataSource implements ShotDataSource {

    private final ShotManager shotManager;

    @Inject public DatabaseShotDataSource(ShotManager shotManager) {
        this.shotManager = shotManager;
    }

    @Override public ShotEntity putShot(ShotEntity shotEntity) {
        try {
            shotManager.saveShot(shotEntity);
            return shotEntity;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override public void putShots(List<ShotEntity> shotEntities) {
        shotManager.saveShots(shotEntities);
    }

    @Override
    public List<ShotEntity> getShotsForStreamTimeline(StreamTimelineParameters parameters) {
        return shotManager.getShotsByStreamParameters(parameters);
    }

    @Override public ShotEntity getShot(String shotId) {
        return shotManager.getShotById(shotId);
    }

    @Override public List<ShotEntity> getReplies(String shotId) {
        return shotManager.getRepliesTo(shotId);
    }

    @Override public Integer getStreamMediaShotsCount(String idStream, List<String> idUsers) {
        return shotManager.getStreamMediaShotsCount(idStream, idUsers);
    }

    @Override public List<ShotEntity> getStreamMediaShots(String idStream, List<String> userIds) {
        return shotManager.getStreamMediaShots(idStream, userIds);
    }

    @Override
    public List<ShotEntity> getShotsFromUser(String idUser, Integer limit) {
        return shotManager.getShotsFromUser(idUser, limit);
    }

    @Override
    public ShotDetailEntity getShotDetail(String idShot) {
        ShotDetailEntity shotDetailEntity = new ShotDetailEntity();

        ShotEntity shot = getShot(idShot);
        shotDetailEntity.setShot(shot);

        shotDetailEntity.setReplies(getReplies(idShot));

        String parentId = shot.getIdShotParent();
        if (parentId != null) {
            shotDetailEntity.setParentShot(getShot(parentId));
        }

        return shotDetailEntity;
    }
}
