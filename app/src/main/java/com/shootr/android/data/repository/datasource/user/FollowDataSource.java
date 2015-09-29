package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.repository.datasource.SyncableDataSource;
import java.util.List;

public interface FollowDataSource extends SyncableDataSource<FollowEntity> {

    List<FollowEntity> putFollows(List<FollowEntity> followEntities);

    FollowEntity putFollow(FollowEntity followEntity);

    void removeFollow(String idUser);
}
