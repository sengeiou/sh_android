package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.FollowEntity;
import java.util.List;

public interface FollowDataSource {

    List<FollowEntity> putFollows(List<FollowEntity> followEntities);

}
