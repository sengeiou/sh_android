package com.shootr.android.domain.repository;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.TimelineParameters;
import java.util.List;

public interface ShotRepository {

    Shot putShot(Shot shot);

    List<Shot> getShotsForTimeline(TimelineParameters parameters);

    Shot getShot(String shotId);

    List<Shot> getReplies(String shot);

    int getMediaCountByIdEvent(String idEvent, String idUser);

}
