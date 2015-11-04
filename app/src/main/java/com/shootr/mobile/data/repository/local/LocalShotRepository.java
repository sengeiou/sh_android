package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.mapper.ShotEntityMapper;
import com.shootr.mobile.data.repository.datasource.shot.ShotDataSource;
import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.ShotDetail;
import com.shootr.mobile.domain.StreamTimelineParameters;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.ShotRepository;
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

    @Override public List<Shot> getShotsForStreamTimeline(StreamTimelineParameters parameters) {
        List<ShotEntity> shotsForEvent = localShotDataSource.getShotsForStreamTimeline(parameters);
        return shotEntityMapper.transform(shotsForEvent);
    }

    @Override public Shot getShot(String shotId) {
        ShotEntity shot = localShotDataSource.getShot(shotId);
        return shotEntityMapper.transform(shot);
    }

    @Override public List<Shot> getReplies(String shot) {
        return shotEntityMapper.transform(localShotDataSource.getReplies(shot));
    }

    @Override public List<Shot> getMediaByIdStream(String idEvent, List<String> userIds, Long maxTimestamp) {
        List<ShotEntity> shotEntitiesWithMedia = localShotDataSource.getStreamMediaShots(idEvent, userIds, maxTimestamp);
        List<Shot> shotsWithMedia = shotEntityMapper.transform(shotEntitiesWithMedia);
        return shotsWithMedia;
    }

    @Override
    public List<Shot> getShotsFromUser(String idUser, Integer limit) {
        return shotEntityMapper.transform(localShotDataSource.getShotsFromUser(idUser, limit));
    }

    @Override
    public ShotDetail getShotDetail(String idShot) {
        return shotEntityMapper.transform(localShotDataSource.getShotDetail(idShot));
    }

    @Override public List<Shot> getAllShotsFromUser(String userId) {
        return shotEntityMapper.transform(localShotDataSource.getAllShotsFromUser(userId));
    }

    @Override public List<Shot> getAllShotsFromUserAndDate(String userId, Long currentOldestDate) {
        return shotEntityMapper.transform(localShotDataSource.getAllShotsFromUserAndDate(userId, currentOldestDate));
    }

    @Override public void putShots(List<Shot> shotsFromUser) {
        localShotDataSource.putShots(shotEntityMapper.transformInEntities(shotsFromUser));
    }

    @Override public void shareShot(String idShot) {
        throw new IllegalArgumentException("No local implementation");
    }

    @Override public void deleteShot(String idShot) {
        localShotDataSource.deleteShot(idShot);
    }

    @Override public List<Shot> getUserShotsForStreamTimeline(StreamTimelineParameters timelineParameters) {
        List<ShotEntity> shotsForStream = localShotDataSource.getUserShotsForStreamTimeline(timelineParameters);
        return shotEntityMapper.transform(shotsForStream);
    }

    @Override public void deleteShotsByStream(String idStream) {
        localShotDataSource.deleteShotsByIdStream(idStream);
    }
}
