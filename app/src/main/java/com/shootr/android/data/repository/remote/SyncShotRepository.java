package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.mapper.ShotEntityMapper;
import com.shootr.android.data.repository.datasource.shot.ShotDataSource;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.ShotDetail;
import com.shootr.android.domain.StreamTimelineParameters;
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

    @Override public List<Shot> getShotsForStreamTimeline(StreamTimelineParameters parameters) {
        List<ShotEntity> shotEntitiesFromTimeline = remoteShotDataSource.getShotsForStreamTimeline(parameters);
        localShotDataSource.putShots(shotEntitiesFromTimeline);
        return shotEntityMapper.transform(shotEntitiesFromTimeline);
    }

    @Override public Shot getShot(String shotId) {
        ShotEntity shot = localShotDataSource.getShot(shotId);
        if (shot == null) {
            shot = remoteShotDataSource.getShot(shotId);
        }
        return shotEntityMapper.transform(shot);
    }

    @Override public List<Shot> getReplies(String shot) {
        return shotEntityMapper.transform(remoteShotDataSource.getReplies(shot));
    }

    @Override public List<Shot> getMediaByIdStream(String idEvent, List<String> userId, Long maxTimestamp) {
        List<ShotEntity> shotEntitiesWithMedia = remoteShotDataSource.getStreamMediaShots(idEvent, userId, maxTimestamp);
        return shotEntityMapper.transform(shotEntitiesWithMedia);
    }

    @Override
    public List<Shot> getShotsFromUser(String idUser, Integer limit) {
        return shotEntityMapper.transform(remoteShotDataSource.getShotsFromUser(idUser, limit));
    }

    @Override
    public ShotDetail getShotDetail(String idShot) {
        return shotEntityMapper.transform(remoteShotDataSource.getShotDetail(idShot));
    }

    @Override public List<Shot> getAllShotsFromUser(String userId) {
        return shotEntityMapper.transform(remoteShotDataSource.getAllShotsFromUser(userId));
    }

    @Override public List<Shot> getAllShotsFromUserAndDate(String userId, Long currentOldestDate) {
        return shotEntityMapper.transform(remoteShotDataSource.getAllShotsFromUserAndDate(userId, currentOldestDate));
    }

    @Override public void putShots(List<Shot> shotsFromUser) {
        throw new IllegalArgumentException("putShots not implemented in remote");
    }

    @Override public void shareShot(String idShot) {
        remoteShotDataSource.shareShot(idShot);
    }

    @Override public void deleteShot(String idShot) {
        remoteShotDataSource.deleteShot(idShot);
    }

    @Override public List<Shot> getUserShotsForStreamTimeline(StreamTimelineParameters timelineParameters) {
        throw new IllegalArgumentException("not implemented yet");
    }
}
