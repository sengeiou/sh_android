package com.shootr.android.service;

import com.shootr.android.data.entity.FollowEntity;
import java.io.IOException;

public interface ShootrService {

    FollowEntity getFollowByIdUserFollowed(String currentUserId, String idUser) throws IOException;

    FollowEntity followUser(FollowEntity follow) throws IOException;

    FollowEntity unfollowUser(FollowEntity follow) throws IOException;
}
