package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.mapper.ShotEntityMapper;
import com.shootr.android.data.repository.datasource.shot.ShotDataSource;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.EventTimelineParameters;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.ShotRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalShotRepository implements ShotRepository {

    private final ShotDataSource localShotDataSource;
    private final ShotEntityMapper shotEntityMapper;

    @Inject public LocalShotRepository(@Local ShotDataSource localShotDataSource, ShotEntityMapper shotEntityMapper) {
        this.localShotDataSource = localShotDataSource;
        this.shotEntityMapper = shotEntityMapper;
    }

    @Override public Shot putShot(Shot shot) {
        localShotDataSource.putShot(shotEntityMapper.transform(shot));
        return shot;
    }

    @Override public List<Shot> getShotsForEventTimeline(EventTimelineParameters parameters) {
        List<ShotEntity> shotsForEvent = localShotDataSource.getShotsForEventTimeline(parameters);
        return shotEntityMapper.transform(shotsForEvent);
    }

    @Override
    public List<Shot> getShotsForActivityTimeline(ActivityTimelineParameters parameters) {
        List<ShotEntity> shotsForEvent = localShotDataSource.getShotsForActivityTimeline(parameters);
        return shotEntityMapper.transform(shotsForEvent);
    }

    @Override public Shot getShot(String shotId) {
        ShotEntity shot = localShotDataSource.getShot(shotId);
        if (shot != null) {
            return shotEntityMapper.transform(shot);
        } else {
            return null;
        }
    }

    @Override public List<Shot> getReplies(String shot) {
        return shotEntityMapper.transform(localShotDataSource.getReplies(shot));
    }

    @Override public Integer getMediaCountByIdEvent(String idEvent, String idUser) {
        return localShotDataSource.getEventMediaShotsByUserCount(idEvent, idUser);
    }

    @Override public List<Shot> getMediaByIdEvent(String idEvent, String userId) {
        List<ShotEntity> shotEntitiesWithMedia = localShotDataSource.getEventMediaShotsByUser(idEvent, userId);
        List<Shot> shotsWithMedia = shotEntityMapper.transform(shotEntitiesWithMedia);
        return shotsWithMedia;
    }
}
