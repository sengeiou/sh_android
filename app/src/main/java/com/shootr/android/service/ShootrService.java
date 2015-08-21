package com.shootr.android.service;

import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.data.entity.UserEntity;
import java.io.IOException;
import java.util.List;

public interface ShootrService {

    List<UserEntity> getFollowers(String idUserFollowed, Long lastModifiedDate) throws  IOException;

    List<UserEntity> getFollowing(String idUser, Long lastModifiedDate) throws IOException;

    List<UserEntity> getUsersById(List<String> userIds) throws IOException;

    UserEntity getUserByIdUser(String idUser) throws IOException;

    ShotEntity postNewShotWithImage(ShotEntity shotTemplate) throws IOException;

    PaginatedResult<List<UserEntity>> searchUsersByNameOrNickNamePaginated(String searchQuery, int pageOffset)
      throws IOException;

    FollowEntity getFollowByIdUserFollowed(String currentUserId, String idUser) throws  IOException;

    DeviceEntity updateDevice(DeviceEntity device) throws IOException;

    DeviceEntity getDeviceByUniqueId(String uniqueDeviceId) throws IOException;

    FollowEntity followUser(FollowEntity follow) throws IOException;

    FollowEntity unfollowUser(FollowEntity follow) throws IOException;

    StreamEntity saveStream(StreamEntity streamEntity) throws IOException;

    List<StreamEntity> getStreamsByIds(List<String> streamIds) throws IOException;

    StreamEntity getStreamById(String idStream) throws IOException;

    UserEntity saveUserProfile(UserEntity userEntity) throws IOException;

    void performCheckin(String idUser, String idStream) throws IOException;

    UserEntity getUserByUsername(String username) throws IOException;

    Integer getStreamMediaShotsCount(String idStream, List<String> idUser) throws IOException;

    List<ShotEntity> getStreamMediaShots(String idStream, List<String> userId) throws IOException;

    void logout(String idUser, String sessionToken) throws IOException;

    List<SuggestedPeopleEntity> getSuggestedPeople(String currentUserId) throws IOException;
}
