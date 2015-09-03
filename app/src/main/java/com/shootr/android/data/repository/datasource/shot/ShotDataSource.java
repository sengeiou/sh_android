package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotDetailEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.StreamTimelineParameters;
import java.util.List;

public interface ShotDataSource {

    ShotEntity putShot(ShotEntity shotEntity);

    void putShots(List<ShotEntity> shotEntities);

    List<ShotEntity> getShotsForStreamTimeline(StreamTimelineParameters parameters);

    ShotEntity getShot(String shotId);

    List<ShotEntity> getReplies(String shotId);

    List<ShotEntity> getStreamMediaShots(String idStream, List<String> userId);

    List<ShotEntity> getShotsFromUser(String idUser, Integer limit);

    ShotDetailEntity getShotDetail(String idShot);

    List<ShotEntity> getAllShotsFromUser(String userId);

    List<ShotEntity> getAllShotsFromUserAndDate(String userId, Long currentOldestDate);

    void shareShot(String idShot);
}
