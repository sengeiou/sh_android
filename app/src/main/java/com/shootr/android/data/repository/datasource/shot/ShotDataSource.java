package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.TimelineParameters;
import java.util.List;

public interface ShotDataSource {

    ShotEntity putShot(ShotEntity shotEntity);

    List<ShotEntity> getShotsForTimeline(TimelineParameters parameters);
}
