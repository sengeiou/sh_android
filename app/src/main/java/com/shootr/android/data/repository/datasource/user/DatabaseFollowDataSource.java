package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.db.manager.FollowManager;
import java.util.List;
import javax.inject.Inject;

public class DatabaseFollowDataSource implements FollowDataSource {

    private final FollowManager followManager;

    @Inject public DatabaseFollowDataSource(FollowManager followManager) {
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
}
