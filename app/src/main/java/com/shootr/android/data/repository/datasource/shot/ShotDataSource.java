package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.StreamTimelineParameters;
import java.util.List;

public interface ShotDataSource {

    ShotEntity putShot(ShotEntity shotEntity);

    void putShots(List<ShotEntity> shotEntities);

    List<ShotEntity> getShotsForEventTimeline(StreamTimelineParameters parameters);

    ShotEntity getShot(String shotId);

    List<ShotEntity> getReplies(String shotId);

    Integer getEventMediaShotsCount(String idEvent, List<String> idUser);

    List<ShotEntity> getEventMediaShots(String idEvent, List<String> userId);

    List<ShotEntity> getShotsFromUser(String idUser, Integer limit);
}
