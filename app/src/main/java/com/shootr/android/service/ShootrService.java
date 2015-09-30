package com.shootr.android.service;

import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.UserEntity;
import java.io.IOException;
import java.util.List;

public interface ShootrService {

    FollowEntity getFollowByIdUserFollowed(String currentUserId, String idUser) throws  IOException;

    DeviceEntity updateDevice(DeviceEntity device) throws IOException;

    DeviceEntity getDeviceByUniqueId(String uniqueDeviceId) throws IOException;

    FollowEntity followUser(FollowEntity follow) throws IOException;

    FollowEntity unfollowUser(FollowEntity follow) throws IOException;

    StreamEntity saveStream(StreamEntity streamEntity) throws IOException;

    List<StreamEntity> getStreamsByIds(List<String> streamIds) throws IOException;

    UserEntity saveUserProfile(UserEntity userEntity) throws IOException;
}
