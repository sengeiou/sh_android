package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.TimelineParameters;
import java.util.List;

public interface ShotDataSource {

    ShotEntity putShot(ShotEntity shotEntity);

    void putShots(List<ShotEntity> shotEntities);

    List<ShotEntity> getShotsForTimeline(TimelineParameters parameters);

    ShotEntity getShot(String shotId);

    List<ShotEntity> getReplies(String shotId);

    int getEventMediaCount(String idEvent, String idUser);

    List<ShotEntity> getEventMedia(String idEvent, String userId);
}
