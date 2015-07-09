package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.domain.EventTimelineParameters;
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
    public List<ShotEntity> getShotsForEventTimeline(EventTimelineParameters parameters) {
        return shotManager.getShotsByEventParameters(parameters);
    }

    @Override public ShotEntity getShot(String shotId) {
        return shotManager.getShotById(shotId);
    }

    @Override public List<ShotEntity> getReplies(String shotId) {
        return shotManager.getRepliesTo(shotId);
    }

    @Override public Integer getEventMediaShotsCount(String idEvent, List<String> idUsers) {
        return shotManager.getEventMediaShotsCount(idEvent, idUsers);
    }

    @Override public List<ShotEntity> getEventMediaShots(String idEvent, List<String> userIds) {
        return shotManager.getEventMediaShots(idEvent, userIds);
    }

    @Override
    public List<ShotEntity> getShotsFromUser(String idUser, Integer limit) {
        return shotManager.getShotsFromUser(idUser, limit);
    }
}
