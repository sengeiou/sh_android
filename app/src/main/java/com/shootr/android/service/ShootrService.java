package com.shootr.android.service;

import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.TeamEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.service.PaginatedResult;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ShootrService {

    UserEntity login(String id, String password) throws IOException;

    List<UserEntity> getFollowers(Long idUserFollowed, Long lastModifiedDate) throws  IOException;

    List<UserEntity> getFollowing(Long idUser, Long lastModifiedDate) throws IOException;

    List<UserEntity> getUsersById(List<Long> userIds) throws IOException;

    UserEntity getUserByIdUser(Long idUser) throws IOException;

    ShotEntity getShotById(Long idShot) throws IOException;

    List<ShotEntity> getShotsByParameters(TimelineParameters parameters) throws IOException;

    List<ShotEntity> getNewShots(List<Long> followingUserIds, Long newestShotDate) throws IOException;

    List<ShotEntity> getOlderShots(List<Long> followingUserIds, Long oldestShotDate) throws IOException;

    List<ShotEntity> getShotsByUserIdList(List<Long> followingUserIds, Long lastModifiedDate) throws IOException;

    ShotEntity postNewShotWithImage(ShotEntity shotTemplate) throws IOException;

    PaginatedResult<List<UserEntity>> searchUsersByNameOrNickNamePaginated(String searchQuery, int pageOffset)
      throws IOException;

    FollowEntity getFollowByIdUserFollowed(Long currentUserId, Long idUser) throws  IOException;

    DeviceEntity updateDevice(DeviceEntity device) throws IOException;

    DeviceEntity getDeviceByUniqueId(String uniqueDeviceId) throws IOException;

    FollowEntity followUser(FollowEntity follow) throws IOException;

    FollowEntity unfollowUser(FollowEntity follow) throws IOException;

    EventEntity saveEvent(EventEntity eventEntity) throws IOException;

    List<EventEntity> getEventsByIds(List<Long> eventIds) throws IOException;

    EventEntity getEventById(Long idEvent) throws IOException;

    List<ShotEntity> getLatestsShotsFromIdUser(Long idUser, Long latestShotNumber) throws IOException;

    UserEntity saveUserProfile(UserEntity userEntity) throws IOException;

    List<TeamEntity> searchTeams(String queryText) throws IOException;

    List<EventSearchEntity> getEventSearch(String query, Map<Long, Integer> eventsWatchesCounts)
      throws IOException;

    void performCheckin(Long idUser, Long idEvent) throws IOException;
}
