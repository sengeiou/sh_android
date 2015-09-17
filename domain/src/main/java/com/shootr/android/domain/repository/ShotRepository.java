package com.shootr.android.domain.repository;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.ShotDetail;
import com.shootr.android.domain.StreamTimelineParameters;
import com.shootr.android.domain.exception.ShotRemovedException;
import java.util.List;

public interface ShotRepository {

    Shot putShot(Shot shot) throws ShotRemovedException;

    List<Shot> getShotsForStreamTimeline(StreamTimelineParameters parameters);

    Shot getShot(String shotId) throws ShotRemovedException;

    List<Shot> getReplies(String shot);

    List<Shot> getMediaByIdStream(String idEvent, List<String> userId, Long maxTimestamp);

    List<Shot> getShotsFromUser(String idUser, Integer limit);

    ShotDetail getShotDetail(String idShot) throws ShotRemovedException;

    List<Shot> getAllShotsFromUser(String userId);

    List<Shot> getAllShotsFromUserAndDate(String userId, Long currentOldestDate);

    void putShots(List<Shot> shotsFromUser);

    void shareShot(String idShot) throws ShotRemovedException;

    void deleteShot(String idShot);
}
