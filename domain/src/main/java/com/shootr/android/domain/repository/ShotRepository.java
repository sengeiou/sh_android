package com.shootr.android.domain.repository;

import com.shootr.android.domain.StreamTimelineParameters;
import com.shootr.android.domain.Shot;
import java.util.List;

public interface ShotRepository {

    Shot putShot(Shot shot);

    List<Shot> getShotsForEventTimeline(StreamTimelineParameters parameters);

    Shot getShot(String shotId);

    List<Shot> getReplies(String shot);

    Integer getMediaCountByIdEvent(String idEvent, List<String> idUser);

    List<Shot> getMediaByIdEvent(String idEvent, List<String> userId);

    List<Shot> getShotsFromUser(String idUser, Integer limit);
}
