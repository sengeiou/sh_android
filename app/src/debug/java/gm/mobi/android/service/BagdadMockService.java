package gm.mobi.android.service;

import gm.mobi.android.db.objects.Device;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class BagdadMockService implements BagdadService {

    private long delay;
    private int variancePercentage;
    private int errorPercentage;

    @Inject public BagdadMockService() {
        //TODO inicializar y usar valores de mockeo mediante preferencias
        //TODO mirar la implementación de AndroidMockValue persistance https://github.com/square/retrofit/blob/master/retrofit-mock/src/main/java/retrofit/android/AndroidMockValuePersistence.java
    }

    @Override
    public User login(String id, String password) {
        if (id.equals("rafa.vazsan@gmail.com") || id.equals("sloydev")) {
            User mockUser = new User();
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

    @Override public List<User> getFollowers(Long idUserFollowed, Long lastModifiedDate) throws IOException {
        return null;
    }

    @Override public List<User> getFollowing(Long idUser, Long lastModifiedDate) throws IOException {
        return null;
    }

    @Override public Shot getShotById(Long idShot) throws IOException {
        return null;
    }

    @Override public List<Shot> getNewShots(List<Long> followingUserIds, Long newestShotDate) throws IOException {
        return null;
    }

    @Override public List<Shot> getOlderShots(List<Long> followingUserIds, Long oldestShotDate) throws IOException {
        return null;
    }

    @Override public List<Shot> getShotsByUserIdList(List<Long> followingUserIds, Long lastModifiedDate) throws IOException {
        return null;
    }

    @Override public Shot postNewShot(Long idUser, String comment) throws IOException {
        return null;
    }

    @Override
    public User getUserByIdUser(Long idUser) throws IOException {
        return null;
    }

    @Override public PaginatedResult<List<User>> searchUsersByNameOrNickNamePaginated(String searchQuery,
      int pageOffset) throws IOException {
        return null;
    }

    @Override public Follow getFollowByIdUserFollowed(Long currentUserId, Long idUser) throws IOException {
        return null;
    }

    @Override public Device updateDevice(Device device) throws IOException {
        return null;
    }

    @Override public Device getDeviceByUniqueId(String uniqueDeviceId) throws IOException {
        return null;
    }

    @Override public Follow followUser(Follow follow) throws IOException {
        return null;
    }

    @Override public Follow unfollowUser(Follow follow) throws IOException {
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
