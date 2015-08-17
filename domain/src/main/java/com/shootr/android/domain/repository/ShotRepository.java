package com.shootr.android.domain.repository;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.ShotDetail;
import com.shootr.android.domain.StreamTimelineParameters;
import java.util.List;

public interface ShotRepository {

    Shot putShot(Shot shot);

    List<Shot> getShotsForStreamTimeline(StreamTimelineParameters parameters);

    Shot getShot(String shotId);

    List<Shot> getReplies(String shot);

    List<Shot> getMediaByIdStream(String idEvent, List<String> userId);

    List<Shot> getShotsFromUser(String idUser, Integer limit);

    ShotDetail getShotDetail(String idShot);

    List<Shot> getAllShotsFromUser(String userId);

    List<Shot> getAllShotsFromUserAndDate(String userId, Long currentOldestDate);

    void putShots(List<Shot> shotsFromUser);
}
