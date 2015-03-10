package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.mapper.ShotEntityMapper;
import com.shootr.android.data.repository.datasource.shot.ShotDataSource;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalShotRepository implements ShotRepository {

    private final ShotDataSource localShotDataSource;
    private final UserRepository localUserRepository;
    private final ShotEntityMapper shotEntityMapper;

    @Inject public LocalShotRepository(@Local ShotDataSource localShotDataSource, @Local UserRepository localUserRepository,
      ShotEntityMapper shotEntityMapper) {
        this.localShotDataSource = localShotDataSource;
        this.localUserRepository = localUserRepository;
        this.shotEntityMapper = shotEntityMapper;
    }

    @Override public Shot putShot(Shot shot) {
        localShotDataSource.putShot(shotEntityMapper.transform(shot));
        return shot;
    }

    @Override public List<Shot> getShotsForEventAndUsers(Long eventId, List<Long> userIds) {
        List<ShotEntity> shotsForEvent = localShotDataSource.getShotsForEvent(eventId, userIds);
        List<User> usersFromShots = localUserRepository.getUsersByIds(userIds);
        return shotEntityMapper.transform(shotsForEvent, usersFromShots);
    }

    @Override public List<Shot> getShotsForEventAndUsersWithAuthor(Long eventId, Long authorId, List<Long> userIds) {
        userIds.add(authorId);
        return getShotsForEventAndUsers(eventId, userIds);
    }

    @Override public List<Shot> getShotsWithoutEventFromUsers(List<Long> userIds) {
        return getShotsForEventAndUsers(null, userIds);
    }
}
