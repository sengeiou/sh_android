package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.List;
import javax.inject.Inject;

public class DatabaseFollowDataSource implements FollowDataSource {

    private final SessionRepository sessionRepository;
    private final FollowManager followManager;

    @Inject public DatabaseFollowDataSource(SessionRepository sessionRepository, FollowManager followManager) {
        this.sessionRepository = sessionRepository;
        this.followManager = followManager;
    }

    @Override public List<FollowEntity> putFollows(List<FollowEntity> followEntities) {
        followManager.saveFollowsFromServer(followEntities);
        return followEntities;
    }

    @Override
    public FollowEntity putFollow(FollowEntity followEntity) {
        followManager.saveFollow(followEntity);
        return followEntity;
    }

    @Override
    public void removeFollow(String idUser) {
        followManager.deleteFollow(idUser, sessionRepository.getCurrentUserId());
    }

    @Override
    public List<FollowEntity> getEntitiesNotSynchronized() {
        return followManager.getFollowsNotSynchronized();
    }
}
