package gm.mobi.android.service;

import gm.mobi.android.db.objects.DeviceEntity;
import gm.mobi.android.db.objects.FollowEntity;
import gm.mobi.android.db.objects.ShotEntity;
import gm.mobi.android.db.objects.UserEntity;
import java.io.IOException;
import java.util.List;

public interface BagdadService {

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
}
