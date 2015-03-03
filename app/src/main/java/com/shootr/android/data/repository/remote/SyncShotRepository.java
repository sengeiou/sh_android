package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.mapper.ShotEntityMapper;
import com.shootr.android.data.repository.datasource.shot.ShotDataSource;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import javax.inject.Inject;

public class SyncShotRepository implements ShotRepository {

    private final ShotDataSource remoteShotDataSource;
    private final ShotEntityMapper shotEntityMapper;
    private final SessionRepository sessionRepository;

    @Inject public SyncShotRepository(@Remote ShotDataSource remoteShotDataSource, ShotEntityMapper shotEntityMapper,
      SessionRepository sessionRepository) {
        this.remoteShotDataSource = remoteShotDataSource;
        this.shotEntityMapper = shotEntityMapper;
        this.sessionRepository = sessionRepository;
    }

    @Override public Shot putShot(Shot shot) {
        ShotEntity shotEntity = shotEntityMapper.transform(shot);
        ShotEntity responseShotEntity = remoteShotDataSource.putShot(shotEntity);
        return shotEntityMapper.transform(responseShotEntity, sessionRepository.getCurrentUser());
    }
}
