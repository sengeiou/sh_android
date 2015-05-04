package com.shootr.android.service;

import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.TeamEntity;
import com.shootr.android.data.entity.UserCreateAccountEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.TimelineParameters;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ShootrService {

    UserEntity login(String id, String password) throws IOException;

    List<UserEntity> getFollowers(String idUserFollowed, Long lastModifiedDate) throws  IOException;

    List<UserEntity> getFollowing(String idUser, Long lastModifiedDate) throws IOException;

    List<UserEntity> getUsersById(List<String> userIds) throws IOException;

    UserEntity getUserByIdUser(String idUser) throws IOException;

    ShotEntity getShotById(String idShot) throws IOException;

    List<ShotEntity> getShotsByParameters(TimelineParameters parameters) throws IOException;

    List<ShotEntity> getNewShots(List<Long> followingUserIds, Long newestShotDate) throws IOException;

    List<ShotEntity> getOlderShots(List<Long> followingUserIds, Long oldestShotDate) throws IOException;

    List<ShotEntity> getShotsByUserIdList(List<Long> followingUserIds, Long lastModifiedDate) throws IOException;

    List<ShotEntity> getRepliesToShot(Long shotId) throws IOException;

    ShotEntity postNewShotWithImage(ShotEntity shotTemplate) throws IOException;

    PaginatedResult<List<UserEntity>> searchUsersByNameOrNickNamePaginated(String searchQuery, int pageOffset)
      throws IOException;

    FollowEntity getFollowByIdUserFollowed(String currentUserId, String idUser) throws  IOException;

    DeviceEntity updateDevice(DeviceEntity device) throws IOException;

    DeviceEntity getDeviceByUniqueId(String uniqueDeviceId) throws IOException;

    FollowEntity followUser(FollowEntity follow) throws IOException;

    FollowEntity unfollowUser(FollowEntity follow) throws IOException;

    EventEntity saveEvent(EventEntity eventEntity) throws IOException;

    List<EventEntity> getEventsByIds(List<Long> eventIds) throws IOException;

    EventEntity getEventById(String idEvent) throws IOException;

    List<ShotEntity> getLatestsShotsFromIdUser(String idUser, Long latestShotNumber) throws IOException;

    UserEntity saveUserProfile(UserEntity userEntity) throws IOException;

    List<EventSearchEntity> getEventSearch(String query, Map<String, Integer> eventsWatchesCounts)
      throws IOException;

    void performCheckin(String idUser, String idEvent) throws IOException;

    void createAccount(UserCreateAccountEntity userCreateAccountEntity) throws IOException;
}
