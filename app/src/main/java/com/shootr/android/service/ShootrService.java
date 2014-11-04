package com.shootr.android.service;

import com.shootr.android.db.objects.DeviceEntity;
import com.shootr.android.db.objects.FollowEntity;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.db.objects.TeamEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.db.objects.WatchEntity;
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

    public ShotEntity postNewShot(Long idUser, String comment) throws IOException;

    public UserEntity getUserByIdUser(Long idUser) throws IOException;

    public PaginatedResult<List<UserEntity>> searchUsersByNameOrNickNamePaginated(String searchQuery, int pageOffset)
      throws IOException;

    public FollowEntity getFollowByIdUserFollowed(Long currentUserId, Long idUser) throws  IOException;

    public DeviceEntity updateDevice(DeviceEntity device) throws IOException;

    public DeviceEntity getDeviceByUniqueId(String uniqueDeviceId) throws IOException;

    public FollowEntity followUser(FollowEntity follow) throws IOException;

    public FollowEntity unfollowUser(FollowEntity follow) throws IOException;

    public MatchEntity getNextMatchWhereMyFavoriteTeamPlays(Long idFavoriteTeam) throws IOException;

    public List<WatchEntity> getWatchesFromUsers(List<Long> followingIds) throws IOException;

    public List<MatchEntity> getMatchesByIds(List<Long> matchIds) throws IOException;

    public WatchEntity setWatchStatus(WatchEntity watch) throws IOException;

    public List<TeamEntity> getTeamsByIdTeams(List<Long> teamIds) throws IOException;

}
