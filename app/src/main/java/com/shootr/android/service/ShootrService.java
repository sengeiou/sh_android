package com.shootr.android.service;

import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.UserCreateAccountEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.EventTimelineParameters;
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

    List<ShotEntity> getEventShotsByParameters(EventTimelineParameters parameters) throws IOException;

    List<ShotEntity> getActivityShotsByParameters(ActivityTimelineParameters parameters) throws IOException;

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

    List<EventEntity> getEventsByIds(List<Long> eventIds) throws IOException;

    EventEntity getEventById(String idEvent) throws IOException;

    List<ShotEntity> getLatestsShotsFromIdUser(String idUser, Long latestShotNumber) throws IOException;

    UserEntity saveUserProfile(UserEntity userEntity) throws IOException;

    List<EventSearchEntity> getEventSearch(String query, Map<String, Integer> eventsWatchesCounts, String locale)
      throws IOException;

    void performCheckin(String idUser, String idEvent) throws IOException;

    void createAccount(UserCreateAccountEntity userCreateAccountEntity) throws IOException;

    UserEntity getUserByUsername(String username) throws IOException;

    Integer getEventMediaShotsCount(String idEvent, List<String> idUser) throws IOException;

    List<ShotEntity> getEventMedia(String idEvent, String userId) throws IOException;
}
