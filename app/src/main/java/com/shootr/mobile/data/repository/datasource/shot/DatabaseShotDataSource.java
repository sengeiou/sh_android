package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.entity.ShotDetailEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.db.manager.ShotManager;
import com.shootr.mobile.domain.StreamTimelineParameters;
import java.util.List;
import javax.inject.Inject;

public class DatabaseShotDataSource implements ShotDataSource {

    private final ShotManager shotManager;

    @Inject public DatabaseShotDataSource(ShotManager shotManager) {
        this.shotManager = shotManager;
    }

    @Override public ShotEntity putShot(ShotEntity shotEntity) {
        shotManager.saveShot(shotEntity);
        return shotEntity;
    }

    @Override public void putShots(List<ShotEntity> shotEntities) {
        shotManager.saveShots(shotEntities);
    }

    @Override public List<ShotEntity> getShotsForStreamTimeline(StreamTimelineParameters parameters) {
        return shotManager.getShotsByStreamParameters(parameters);
    }

    @Override public ShotEntity getShot(String shotId, String[] streamTypes, String[] shotTypes) {
        return shotManager.getShotById(shotId);
    }

    @Override public List<ShotEntity> getReplies(String shotId, String[] streamTypes,
        String[] shotTypes) {
        return shotManager.getRepliesTo(shotId);
    }

    @Override public List<ShotEntity> getStreamMediaShots(String idStream, List<String> userIds,
        Long maxTimestamp, String[] streamTypes, String[] shotTypes) {
        return shotManager.getStreamMediaShots(idStream, userIds);
    }

    @Override public List<ShotEntity> getShotsFromUser(String idUser, Integer limit,
        String[] streamTypes, String[] shotTypes) {
        return shotManager.getShotsFromUser(idUser, limit);
    }

    @Override public ShotDetailEntity getShotDetail(String idShot, String[] streamTypes,
        String[] shotTypes) {
        ShotEntity shot = getShot(idShot, streamTypes, shotTypes);
        if (shot != null) {
            ShotDetailEntity shotDetailEntity = new ShotDetailEntity();
            shotDetailEntity.setShot(shot);

            shotDetailEntity.setReplies(getReplies(idShot, streamTypes, shotTypes));

            String parentId = shot.getIdShotParent();
            if (parentId != null) {
                shotDetailEntity.setParentShot(getShot(parentId, streamTypes, shotTypes));
            }

            return shotDetailEntity;
        } else {
            return null;
        }
    }

    @Override public List<ShotEntity> getAllShotsFromUser(String userId, String[] streamTypes,
        String[] shotTypes) {
        return shotManager.getAllShotsFromUser(userId);
    }

    @Override public List<ShotEntity> getAllShotsFromUserAndDate(String userId,
        Long currentOldestDate, String[] streamTypes, String[] shotTypes) {
        throw new IllegalArgumentException("getAllShotsFromUserWithMaxDate should have no local implementation");
    }

    @Override public void shareShot(String idShot) {
        throw new IllegalArgumentException("shareShot should not have local implementation");
    }

    @Override public void deleteShot(String idShot) {
        shotManager.deleteShot(idShot);
    }

    @Override public List<ShotEntity> getUserShotsForStreamTimeline(StreamTimelineParameters timelineParameters) {
        return shotManager.getUserShotsByParameters(timelineParameters);
    }

    @Override public void deleteShotsByIdStream(String idStream) {
        shotManager.deleteShotsByIdStream(idStream);
    }

    @Override public void hideShot(String idShot, Long timeStamp) {
        shotManager.hideShot(idShot, timeStamp);
    }

    @Override public void unhideShot(String idShot) {
        shotManager.pinShot(idShot);
    }

    @Override public List<ShotEntity> getEntitiesNotSynchronized() {
        return shotManager.getHiddenShotNotSynchronized();
    }
}
