package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.EventTimelineParameters;
import java.util.List;

public interface ShotDataSource {

    ShotEntity putShot(ShotEntity shotEntity);

    void putShots(List<ShotEntity> shotEntities);

    List<ShotEntity> getShotsForEventTimeline(EventTimelineParameters parameters);

    List<ShotEntity> getShotsForActivityTimeline(ActivityTimelineParameters parameters);

    ShotEntity getShot(String shotId);

    List<ShotEntity> getReplies(String shotId);

    Integer getEventMediaCount(String idEvent, String idUser);

    List<ShotEntity> getEventMedia(String idEvent, String userId);
}
