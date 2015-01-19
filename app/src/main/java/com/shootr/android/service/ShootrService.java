package com.shootr.android.service;

import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.TeamEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.entity.WatchEntity;
import java.io.IOException;
import java.util.List;

public interface ShootrService {

    public UserEntity login(String id, String password) throws IOException;

    public List<UserEntity> getFollowers(Long idUserFollowed, Long lastModifiedDate) throws  IOException;

    public List<UserEntity> getFollowing(Long idUser, Long lastModifiedDate) throws IOException;

    public ShotEntity getShotById(Long idShot) throws IOException;

    public List<ShotEntity> getNewShots(List<Long> followingUserIds, Long newestShotDate) throws IOException;

    public List<ShotEntity> getOlderShots(List<Long> followingUserIds, Long oldestShotDate) throws IOException;

    public List<ShotEntity> getShotsByUserIdList(List<Long> followingUserIds, Long lastModifiedDate) throws IOException;

    public ShotEntity postNewShot(Long idUser, String comment, Long idEvent) throws IOException;

    ShotEntity postNewShotWithImage(Long idUser, String comment, String imageUrl, Long idEvent) throws IOException;

    public UserEntity getUserByIdUser(Long idUser) throws IOException;

    public PaginatedResult<List<UserEntity>> searchUsersByNameOrNickNamePaginated(String searchQuery, int pageOffset)
      throws IOException;

    public FollowEntity getFollowByIdUserFollowed(Long currentUserId, Long idUser) throws  IOException;

    public DeviceEntity updateDevice(DeviceEntity device) throws IOException;

    public DeviceEntity getDeviceByUniqueId(String uniqueDeviceId) throws IOException;

    public FollowEntity followUser(FollowEntity follow) throws IOException;

    public FollowEntity unfollowUser(FollowEntity follow) throws IOException;

    public EventEntity getNextEventWhereMyFavoriteTeamPlays(Long idFavoriteTeam) throws IOException;

    public List<WatchEntity> getWatchesFromUsers(List<Long> followingIds, Long idCurrentUser) throws IOException;

    public List<WatchEntity> getWatchesFromUsersByEvent(Long idEvent, List<Long> userIds) throws IOException;

    public WatchEntity getVisibleWatch(Long currentUserId) throws IOException;

    public List<EventEntity> getEventsByIds(List<Long> eventIds) throws IOException;

    public WatchEntity setWatchStatus(WatchEntity watch) throws IOException;

    public WatchEntity getWatchStatus(Long idUser, Long idEvent) throws IOException;

    public EventEntity getEventById(Long idEvent) throws IOException;

    public List<TeamEntity> getTeamsByIdTeams(List<Long> teamIds) throws IOException;

    public List<ShotEntity> getLatestsShotsFromIdUser(Long idUser, Long latestShotNumber) throws IOException;

    public UserEntity saveUserProfile(UserEntity userEntity) throws IOException;

    public List<TeamEntity> searchTeams(String queryText) throws IOException;
}
