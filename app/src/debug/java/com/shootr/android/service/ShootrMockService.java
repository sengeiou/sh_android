package com.shootr.android.service;

import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.MatchEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.TeamEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.entity.WatchEntity;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ShootrMockService implements ShootrService {

    private long delay;
    private int variancePercentage;
    private int errorPercentage;

    @Inject public ShootrMockService() {
        //TODO inicializar y usar valores de mockeo mediante preferencias
        //TODO mirar la implementación de AndroidMockValue persistance https://github.com/square/retrofit/blob/master/retrofit-mock/src/main/java/retrofit/android/AndroidMockValuePersistence.java
    }

    @Override
    public UserEntity login(String id, String password) {
        if (id.equals("rafa.vazsan@gmail.com") || id.equals("sloydev")) {
            UserEntity mockUser = new UserEntity();
            mockUser.setIdUser(1L);
            mockUser.setName("Rafa");
            mockUser.setSessionToken("Nnananananananana");
            mockUser.setEmail("rafa.vazsan@gmail.com");
            mockUser.setUserName("rafavazsan");
            mockUser.setFavoriteTeamId(1l);
            mockUser.setFavoriteTeamName("Sevilla FC");
            mockUser.setPhoto("http://example.com");
            return mockUser;
        }
        return null;
    }

    @Override public List<UserEntity> getFollowers(Long idUserFollowed, Long lastModifiedDate) throws IOException {
        return null;
    }

    @Override public List<UserEntity> getFollowing(Long idUser, Long lastModifiedDate) throws IOException {
        return null;
    }

    @Override public ShotEntity getShotById(Long idShot) throws IOException {
        return null;
    }

    @Override public List<ShotEntity> getNewShots(List<Long> followingUserIds, Long newestShotDate) throws IOException {
        return null;
    }

    @Override public List<ShotEntity> getOlderShots(List<Long> followingUserIds, Long oldestShotDate) throws IOException {
        return null;
    }

    @Override public List<ShotEntity> getShotsByUserIdList(List<Long> followingUserIds, Long lastModifiedDate) throws IOException {
        return null;
    }

    @Override public ShotEntity postNewShot(Long idUser, String comment) throws IOException {
        return null;
    }

    @Override public ShotEntity postNewShotWithImage(Long idUser, String comment, String imageUrl) throws IOException {
        return null;
    }

    @Override
    public UserEntity getUserByIdUser(Long idUser) throws IOException {
        return null;
    }

    @Override public PaginatedResult<List<UserEntity>> searchUsersByNameOrNickNamePaginated(String searchQuery,
      int pageOffset) throws IOException {
        return null;
    }

    @Override public FollowEntity getFollowByIdUserFollowed(Long currentUserId, Long idUser) throws IOException {
        return null;
    }

    @Override public DeviceEntity updateDevice(DeviceEntity device) throws IOException {
        return null;
    }

    @Override public DeviceEntity getDeviceByUniqueId(String uniqueDeviceId) throws IOException {
        return null;
    }

    @Override public FollowEntity followUser(FollowEntity follow) throws IOException {
        return null;
    }

    @Override public FollowEntity unfollowUser(FollowEntity follow) throws IOException {
        return null;
    }

    @Override public MatchEntity getNextMatchWhereMyFavoriteTeamPlays(Long idFavoriteTeam)
      throws IOException {
        return null;
    }

    @Override public List<WatchEntity> getWatchesFromUsers(List<Long> followingIds, Long idCurrentUser)
      throws IOException {
        return null;
    }

    @Override public List<MatchEntity> getMatchesByIds(List<Long> matchIds) throws IOException {
        return null;
    }

    @Override public WatchEntity setWatchStatus(WatchEntity watch) throws IOException {
        return null;
    }

    @Override public WatchEntity getWatchStatus(Long idUser, Long idMatch) throws IOException {
        return null;
    }

    @Override public MatchEntity getMatchByIdMatch(Long idMatch) throws IOException {
        return null;
    }

    @Override public List<TeamEntity> getTeamsByIdTeams(List<Long> teamIds) throws IOException {
        return null;
    }

    @Override public List<ShotEntity> getLatestsShotsFromIdUser(Long idUser, Long latestShotNumber) throws IOException {
        return null;
    }

    @Override public List<MatchEntity> searchMatches(String queryText) {
        return null;
    }

    @Override public UserEntity saveUserProfile(UserEntity userEntity) throws IOException {
        return null;
    }

    @Override public List<TeamEntity> searchTeams(String queryText) {
        return null;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public int getVariancePercentage() {
        return variancePercentage;
    }

    public void setVariancePercentage(int variancePercentage) {
        this.variancePercentage = variancePercentage;
    }

    public int getErrorPercentage() {
        return errorPercentage;
    }

    public void setErrorPercentage(int errorPercentage) {
        this.errorPercentage = errorPercentage;
    }
}
