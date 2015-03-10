package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.mapper.ShotEntityMapper;
import com.shootr.android.data.repository.datasource.shot.ShotDataSource;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

public class SyncShotRepository implements ShotRepository {

    private final ShotDataSource remoteShotDataSource;
    private final ShotDataSource localShotDataSource;
    private final ShotEntityMapper shotEntityMapper;
    private final SessionRepository sessionRepository;
    private final UserRepository remoteUserRepository;

    @Inject public SyncShotRepository(@Remote ShotDataSource remoteShotDataSource, @Local ShotDataSource localShotDataSource,
      ShotEntityMapper shotEntityMapper, SessionRepository sessionRepository, @Remote UserRepository remoteUserRepository) {
        this.remoteShotDataSource = remoteShotDataSource;
        this.localShotDataSource = localShotDataSource;
        this.shotEntityMapper = shotEntityMapper;
        this.sessionRepository = sessionRepository;
        this.remoteUserRepository = remoteUserRepository;
    }

    @Override public Shot putShot(Shot shot) {
        ShotEntity shotEntity = shotEntityMapper.transform(shot);
        ShotEntity responseShotEntity = remoteShotDataSource.putShot(shotEntity);
        return shotEntityMapper.transform(responseShotEntity, sessionRepository.getCurrentUser());
    }

    @Override public List<Shot> getShotsForEventAndUsers(Long eventId, List<Long> userIds) {
        List<ShotEntity> shotEntitiesForEvent = remoteShotDataSource.getShotsForEvent(eventId, userIds);
        List<User> usersFromShots = remoteUserRepository.getUsersByIds(userIds);
        //TODO store in local and stuff
        return shotEntityMapper.transform(shotEntitiesForEvent, usersFromShots);
    }

    @Override public List<Shot> getShotsForEventAndUsersWithAuthor(Long eventId, Long authorId, List<Long> userIds) {
        userIds.add(authorId);
        return getShotsForEventAndUsers(eventId, userIds);
    }

    @Override public List<Shot> getShotsWithoutEventFromUsers(List<Long> userIds) {
        return getShotsForEventAndUsers(null, userIds);
    }
}
