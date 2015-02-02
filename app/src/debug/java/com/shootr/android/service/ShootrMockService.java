package com.shootr.android.service;

import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.TeamEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.entity.WatchEntity;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class ShootrMockService implements ShootrService {

    private final ShootrService shootrService;
    private long delay;
    private int variancePercentage;
    private int errorPercentage;

    @Inject public ShootrMockService(ShootrService shootrService) {
        //TODO inicializar y usar valores de mockeo mediante preferencias
        //TODO mirar la implementación de AndroidMockValue persistance https://github.com/square/retrofit/blob/master/retrofit-mock/src/main/java/retrofit/android/AndroidMockValuePersistence.java
        this.shootrService = shootrService;
    }

    @Override public UserEntity login(String id, String password) throws IOException {
        return shootrService.login(id, password);
    }

    @Override public List<UserEntity> getFollowers(Long idUserFollowed, Long lastModifiedDate) throws IOException {
        return shootrService.getFollowers(idUserFollowed, lastModifiedDate);
    }

    @Override public List<UserEntity> getFollowing(Long idUser, Long lastModifiedDate) throws IOException {
        return shootrService.getFollowing(idUser, lastModifiedDate);
    }

    @Override public ShotEntity getShotById(Long idShot) throws IOException {
        return shootrService.getShotById(idShot);
    }

    @Override public List<ShotEntity> getNewShots(List<Long> followingUserIds, Long newestShotDate) throws IOException {
        return shootrService.getNewShots(followingUserIds, newestShotDate);
    }

    @Override public List<ShotEntity> getOlderShots(List<Long> followingUserIds, Long oldestShotDate)
      throws IOException {
        return shootrService.getOlderShots(followingUserIds, oldestShotDate);
    }

    @Override public List<ShotEntity> getShotsByUserIdList(List<Long> followingUserIds, Long lastModifiedDate)
      throws IOException {
        return shootrService.getShotsByUserIdList(followingUserIds, lastModifiedDate);
    }

    @Override public ShotEntity postNewShotWithImage(Long idUser, String comment, String imageUrl, Long idEvent)
      throws IOException {
        return shootrService.postNewShotWithImage(idUser, comment, imageUrl, idEvent);
    }

    @Override public UserEntity getUserByIdUser(Long idUser) throws IOException {
        return shootrService.getUserByIdUser(idUser);
    }

    @Override public PaginatedResult<List<UserEntity>> searchUsersByNameOrNickNamePaginated(String searchQuery,
      int pageOffset) throws IOException {
        return shootrService.searchUsersByNameOrNickNamePaginated(searchQuery, pageOffset);
    }

    @Override public FollowEntity getFollowByIdUserFollowed(Long currentUserId, Long idUser) throws IOException {
        return shootrService.getFollowByIdUserFollowed(currentUserId, idUser);
    }

    @Override public DeviceEntity updateDevice(DeviceEntity device) throws IOException {
        return shootrService.updateDevice(device);
    }

    @Override public DeviceEntity getDeviceByUniqueId(String uniqueDeviceId) throws IOException {
        return shootrService.getDeviceByUniqueId(uniqueDeviceId);
    }

    @Override public FollowEntity followUser(FollowEntity follow) throws IOException {
        return shootrService.followUser(follow);
    }

    @Override public FollowEntity unfollowUser(FollowEntity follow) throws IOException {
        return shootrService.unfollowUser(follow);
    }

    @Override public List<WatchEntity> getWatchesFromUsersAndMe(List<Long> followingIds, Long idCurrentUser)
      throws IOException {
        return shootrService.getWatchesFromUsersAndMe(followingIds, idCurrentUser);
    }

    @Override public List<WatchEntity> getWatchesFromUsersByEvent(Long idEvent, List<Long> userIds) throws IOException {
        return shootrService.getWatchesFromUsersByEvent(idEvent, userIds);
    }

    @Override public WatchEntity getVisibleWatch(Long currentUserId) throws IOException {
        return shootrService.getVisibleWatch(currentUserId);
    }

    @Override public List<EventEntity> getEventsByIds(List<Long> eventIds) throws IOException {
        return shootrService.getEventsByIds(eventIds);
    }

    @Override public WatchEntity setWatchStatus(WatchEntity watch) throws IOException {
        return shootrService.setWatchStatus(watch);
    }

    @Override public WatchEntity getWatchStatus(Long idUser, Long idEvent) throws IOException {
        return shootrService.getWatchStatus(idUser, idEvent);
    }

    @Override public EventEntity getEventById(Long idEvent) throws IOException {
        return shootrService.getEventById(idEvent);
    }

    @Override public List<ShotEntity> getLatestsShotsFromIdUser(Long idUser, Long latestShotNumber) throws IOException {
        return shootrService.getLatestsShotsFromIdUser(idUser, latestShotNumber);
    }

    @Override public UserEntity saveUserProfile(UserEntity userEntity) throws IOException {
        return shootrService.saveUserProfile(userEntity);
    }

    @Override public List<TeamEntity> searchTeams(String queryText) throws IOException {
        return shootrService.searchTeams(queryText);
    }

    @Override public List<EventSearchEntity> getEventSearch(String query, Map<Long, Integer> eventsWatchesCounts)
      throws IOException {
        return shootrService.getEventSearch(query, eventsWatchesCounts);
    }

    //region Settings
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
    //endregion
}
