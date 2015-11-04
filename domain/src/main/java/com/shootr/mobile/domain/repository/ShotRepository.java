package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.ShotDetail;
import java.util.List;

public interface ShotRepository {

    Shot putShot(Shot shot);

    List<Shot> getShotsForStreamTimeline(com.shootr.mobile.domain.StreamTimelineParameters parameters);

    Shot getShot(String shotId);

    List<Shot> getReplies(String shot);

    List<Shot> getMediaByIdStream(String idEvent, List<String> userId, Long maxTimestamp);

    List<Shot> getShotsFromUser(String idUser, Integer limit);

    ShotDetail getShotDetail(String idShot);

    List<Shot> getAllShotsFromUser(String userId);

    List<Shot> getAllShotsFromUserAndDate(String userId, Long currentOldestDate);

    void putShots(List<Shot> shotsFromUser);

    void shareShot(String idShot);

    void deleteShot(String idShot);

    List<Shot> getUserShotsForStreamTimeline(com.shootr.mobile.domain.StreamTimelineParameters timelineParameters);

    void deleteShotsByStream(String idStream);
}