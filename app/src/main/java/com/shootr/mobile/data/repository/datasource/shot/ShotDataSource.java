package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.entity.ShotDetailEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.domain.StreamTimelineParameters;
import java.util.List;

public interface ShotDataSource {

    ShotEntity putShot(ShotEntity shotEntity);

    void putShots(List<ShotEntity> shotEntities);

    List<ShotEntity> getShotsForStreamTimeline(StreamTimelineParameters parameters);

    ShotEntity getShot(String shotId);

    List<ShotEntity> getReplies(String shotId);

    List<ShotEntity> getStreamMediaShots(String idStream, List<String> userId, Long maxTimestamp);

    List<ShotEntity> getShotsFromUser(String idUser, Integer limit);

    ShotDetailEntity getShotDetail(String idShot);

    List<ShotEntity> getAllShotsFromUser(String userId);

    List<ShotEntity> getAllShotsFromUserAndDate(String userId, Long currentOldestDate);

    void shareShot(String idShot);

    void deleteShot(String idShot);

    List<ShotEntity> getUserShotsForStreamTimeline(StreamTimelineParameters timelineParameters);

    void deleteShotsByIdStream(String idStream);
}
