package com.shootr.android.domain.repository;

import com.shootr.android.domain.Shot;
import java.util.List;

public interface ShotRepository {

    Shot putShot(Shot shot);

    List<Shot> getShotsForEventAndUsers(Long eventId, List<Long> userIds);

    List<Shot> getShotsForEventAndUsersWithAuthor(Long eventId, Long authorId, List<Long> userIds);

    List<Shot> getShotsWithoutEventFromUsers(List<Long> userIds);
}
