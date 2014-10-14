package gm.mobi.android.service;

import gm.mobi.android.db.objects.Device;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import java.io.IOException;
import java.util.List;

public interface BagdadService {

    public User login(String id, String password) throws IOException;

    public List<User> getFollowers(Long idUserFollowed, Long lastModifiedDate) throws  IOException;

    public List<User> getFollowing(Long idUser, Long lastModifiedDate) throws IOException;

    public List<Shot> getNewShots(List<Long> followingUserIds, Long newestShotDate) throws IOException;

    public List<Shot> getOlderShots(List<Long> followingUserIds, Long oldestShotDate) throws IOException;

    public List<Shot> getShotsByUserIdList(List<Long> followingUserIds, Long lastModifiedDate) throws IOException;

    public Shot postNewShot(Long idUser, String comment) throws IOException;

    public User getUserByIdUser(Long idUser) throws IOException;

    public PaginatedResult<List<User>> searchUsersByNameOrNickNamePaginated(String searchQuery, int pageOffset)
      throws IOException;

    public Follow getFollowByIdUserFollowed(Long currentUserId, Long idUser) throws  IOException;

    public Device updateDevice(Device device) throws IOException;

    public Follow followUser(Follow follow) throws IOException;
}
