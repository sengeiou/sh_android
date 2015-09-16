package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.mapper.ShotEntityMapper;
import com.shootr.android.data.repository.datasource.shot.ShotDataSource;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.ShotDetail;
import com.shootr.android.domain.StreamTimelineParameters;
import com.shootr.android.domain.exception.ShotRemovedException;
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
        try {
            localShotDataSource.putShot(shotEntityMapper.transform(shot));
        } catch (ShotRemovedException e) {
            throw new IllegalArgumentException(e);
        }
        return shot;
    }

    @Override public List<Shot> getShotsForStreamTimeline(StreamTimelineParameters parameters) {
        List<ShotEntity> shotsForEvent = localShotDataSource.getShotsForStreamTimeline(parameters);
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

    @Override public List<Shot> getMediaByIdStream(String idEvent, List<String> userIds) {
        List<ShotEntity> shotEntitiesWithMedia = localShotDataSource.getStreamMediaShots(idEvent, userIds);
        List<Shot> shotsWithMedia = shotEntityMapper.transform(shotEntitiesWithMedia);
        return shotsWithMedia;
    }

    @Override
    public List<Shot> getShotsFromUser(String idUser, Integer limit) {
        return shotEntityMapper.transform(localShotDataSource.getShotsFromUser(idUser, limit));
    }

    @Override
    public ShotDetail getShotDetail(String idShot) throws ShotRemovedException {
        return shotEntityMapper.transform(localShotDataSource.getShotDetail(idShot));
    }

    @Override public List<Shot> getAllShotsFromUser(String userId) {
        return shotEntityMapper.transform(localShotDataSource.getAllShotsFromUser(userId));
    }

    @Override public List<Shot> getAllShotsFromUserAndDate(String userId, Long currentOldestDate) {
        return shotEntityMapper.transform(localShotDataSource.getAllShotsFromUserAndDate(userId, currentOldestDate));
    }

    @Override public void putShots(List<Shot> shotsFromUser) {
        for (Shot shot : shotsFromUser) {
            putShot(shot);
        }
    }

    @Override public void shareShot(String idShot) {
        throw new IllegalArgumentException("No local implementation");
    }

    @Override public void deleteShot(String idShot) {
        localShotDataSource.deleteShot(idShot);
    }
}
