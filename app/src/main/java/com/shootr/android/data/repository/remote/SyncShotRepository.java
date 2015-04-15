package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.mapper.ShotEntityMapper;
import com.shootr.android.data.repository.datasource.shot.ShotDataSource;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import java.util.List;
import javax.inject.Inject;

public class SyncShotRepository implements ShotRepository {

    private final ShotDataSource remoteShotDataSource;
    private final ShotDataSource localShotDataSource;
    private final ShotEntityMapper shotEntityMapper;

    @Inject public SyncShotRepository(@Remote ShotDataSource remoteShotDataSource, @Local ShotDataSource localShotDataSource,
      ShotEntityMapper shotEntityMapper) {
        this.remoteShotDataSource = remoteShotDataSource;
        this.localShotDataSource = localShotDataSource;
        this.shotEntityMapper = shotEntityMapper;
    }

    @Override public Shot putShot(Shot shot) {
        ShotEntity shotEntity = shotEntityMapper.transform(shot);
        ShotEntity responseShotEntity = remoteShotDataSource.putShot(shotEntity);
        return shotEntityMapper.transform(responseShotEntity);
    }

    @Override public List<Shot> getShotsForTimeline(TimelineParameters parameters) {
        List<ShotEntity> shotEntitiesFromTimeline = remoteShotDataSource.getShotsForTimeline(parameters);
        //TODO wanna put only in some cases? Say that through TimelineParameters
        localShotDataSource.putShots(shotEntitiesFromTimeline);
        return shotEntityMapper.transform(shotEntitiesFromTimeline);
    }

    @Override public Shot getShot(Long shotId) {
        ShotEntity shot = localShotDataSource.getShot(shotId);
        if (shot == null) {
            shot = remoteShotDataSource.getShot(shotId);
        }
        return shotEntityMapper.transform(shot);
    }

    @Override public List<Shot> getReplies(Long shot) {
        return null;
    }
}
