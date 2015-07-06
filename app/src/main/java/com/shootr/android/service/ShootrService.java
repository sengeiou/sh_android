package com.shootr.android.service;

import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.UserEntity;
import java.io.IOException;
import java.util.List;

public interface ShootrService {

    List<UserEntity> getFollowers(String idUserFollowed, Long lastModifiedDate) throws  IOException;

    List<UserEntity> getFollowing(String idUser, Long lastModifiedDate) throws IOException;

    List<UserEntity> getUsersById(List<String> userIds) throws IOException;

    UserEntity getUserByIdUser(String idUser) throws IOException;

    ShotEntity getShotById(String idShot) throws IOException;

    List<ShotEntity> getNewShots(List<Long> followingUserIds, Long newestShotDate) throws IOException;

    List<ShotEntity> getOlderShots(List<Long> followingUserIds, Long oldestShotDate) throws IOException;

    List<ShotEntity> getShotsByUserIdList(List<Long> followingUserIds, Long lastModifiedDate) throws IOException;

    List<ShotEntity> getRepliesToShot(String shotId) throws IOException;

    ShotEntity postNewShotWithImage(ShotEntity shotTemplate) throws IOException;

    PaginatedResult<List<UserEntity>> searchUsersByNameOrNickNamePaginated(String searchQuery, int pageOffset)
      throws IOException;

    FollowEntity getFollowByIdUserFollowed(String currentUserId, String idUser) throws  IOException;

    DeviceEntity updateDevice(DeviceEntity device) throws IOException;

    DeviceEntity getDeviceByUniqueId(String uniqueDeviceId) throws IOException;

    FollowEntity followUser(FollowEntity follow) throws IOException;

    FollowEntity unfollowUser(FollowEntity follow) throws IOException;

    EventEntity saveEvent(EventEntity eventEntity) throws IOException;

    List<EventEntity> getEventsByIds(List<String> eventIds) throws IOException;

    EventEntity getEventById(String idEvent) throws IOException;

    List<ShotEntity> getLatestsShotsFromIdUser(String idUser, Long latestShotNumber) throws IOException;

    UserEntity saveUserProfile(UserEntity userEntity) throws IOException;

    void performCheckin(String idUser, String idEvent) throws IOException;

    UserEntity getUserByUsername(String username) throws IOException;

    Integer getEventMediaShotsCount(String idEvent, List<String> idUser) throws IOException;

    List<ShotEntity> getEventMediaShots(String idEvent, List<String> userId) throws IOException;

    Integer getListingCount(String idUser) throws IOException;

    void logout(String idUser, String sessionToken) throws IOException;
}
