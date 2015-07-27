package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.StreamTimelineParameters;
import java.util.List;

public interface ShotDataSource {

    ShotEntity putShot(ShotEntity shotEntity);

    void putShots(List<ShotEntity> shotEntities);

    List<ShotEntity> getShotsForStreamTimeline(StreamTimelineParameters parameters);

    ShotEntity getShot(String shotId);

    List<ShotEntity> getReplies(String shotId);

    Integer getStreamMediaShotsCount(String idStream, List<String> idUser);

    List<ShotEntity> getStreamMediaShots(String idStream, List<String> userId);

    List<ShotEntity> getShotsFromUser(String idUser, Integer limit);
}
