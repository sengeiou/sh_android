package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.domain.TimelineParameters;
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

    @Override public List<ShotEntity> getShotsForTimeline(TimelineParameters parameters) {
        return shotManager.getShotsByParameters(parameters);
    }
}