package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.data.mapper.ShotEntityMapper;
import com.shootr.mobile.data.repository.datasource.shot.ShotDataSource;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.ShotDetail;
import com.shootr.mobile.domain.StreamTimelineParameters;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;

import java.util.List;

import javax.inject.Inject;

public class SyncShotRepository implements ShotRepository, SyncableRepository {

    private final ShotDataSource remoteShotDataSource;
    private final ShotDataSource localShotDataSource;
    private final ShotEntityMapper shotEntityMapper;
    private final SyncTrigger syncTrigger;

    @Inject public SyncShotRepository(@Remote ShotDataSource remoteShotDataSource, @Local ShotDataSource localShotDataSource,
      ShotEntityMapper shotEntityMapper, SyncTrigger syncTrigger) {
        this.remoteShotDataSource = remoteShotDataSource;
        this.localShotDataSource = localShotDataSource;
        this.shotEntityMapper = shotEntityMapper;
        this.syncTrigger=syncTrigger;
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
        syncTrigger.triggerSync();
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
        List<ShotEntity> shotEntitiesFromTimeline = remoteShotDataSource.getUserShotsForStreamTimeline(timelineParameters);
        localShotDataSource.putShots(shotEntitiesFromTimeline);
        return shotEntityMapper.transform(shotEntitiesFromTimeline);
    }

    @Override public void deleteShotsByStream(String idStream) {
        throw new IllegalArgumentException("deleteShotsByStream not implemented in remote");
    }

    @Override public void hideShot(String idShot, Long timestamp) {
        ShotEntity shotEntity = getShotEntity(idShot);
        try {
            remoteShotDataSource.hideShot(idShot,timestamp);
            shotEntity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
            shotEntity.setProfileHidden(timestamp);
            localShotDataSource.putShot(shotEntity);
        } catch (ServerCommunicationException e) {
            shotEntity.setSynchronizedStatus(Synchronized.SYNC_UPDATED);
            localShotDataSource.hideShot(idShot,timestamp);
            localShotDataSource.putShot(shotEntity);
            syncTrigger.notifyNeedsSync(this);
        }

    }

    private ShotEntity getShotEntity(String idShot) {
        ShotEntity shot = localShotDataSource.getShot(idShot);
        if (shot == null) {
            shot = remoteShotDataSource.getShot(idShot);
        }
        return shot;
    }

    @Override public void unhideShot(String idShot) {
        remoteShotDataSource.unhideShot(idShot);
    }

    @Override public void dispatchSync() {
        List<ShotEntity> pendingEntities = localShotDataSource.getEntitiesNotSynchronized();
        for (ShotEntity entity : pendingEntities) {
            syncHiddenEntities(entity);
        }
    }

    private void syncHiddenEntities(ShotEntity entity) {
        try {
            remoteShotDataSource.hideShot(entity.getIdShot(),entity.getProfileHidden());
            entity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
            localShotDataSource.hideShot(entity.getIdShot(),entity.getProfileHidden());
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
