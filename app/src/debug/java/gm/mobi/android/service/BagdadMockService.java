package gm.mobi.android.service;

import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.Team;
import gm.mobi.android.db.objects.User;
import hugo.weaving.DebugLog;
import java.io.IOException;
import java.util.List;
import java.util.Set;
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
    @DebugLog
    public User login(String id, String password) {
        if (id.equals("rafa.vazsan@gmail.com") || id.equals("sloydev")) {
            User mockUser = new User();
            mockUser.setIdUser(1L);
            mockUser.setName("Rafa");
            mockUser.setSessionToken("Nnananananananana");
            mockUser.setEmail("rafa.vazsan@gmail.com");
            mockUser.setUserName("rafavazsan");
            mockUser.setFavouriteTeamId(1l);
            mockUser.setPhoto("http://example.com");
            return mockUser;
        }
        return null;
    }

    @Override public List<Follow> getFollows(Long idUser, Long lastModifiedDate, int typeFollow, boolean includeDeleted)
      throws IOException {
        return null;
    }

    @Override public List<User> getFollowings(Long idUser, Long lastModifiedDate) throws IOException {
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

    @Override
    public Team getTeamByIdTeam(Long idTeam) throws IOException {
        return null;
    }

    @Override
    public List<Team> getTeamsByIdTeams(Set<Long> teamIds, Long lastModifiedDate) throws IOException {
        return null;
    }

    @Override
    public Follow getFollowRelationship(Long idUser, Long idCurrentUser, int typeFollow) throws IOException {
        return null;
    }

    @Override public List<User> searchUsersByNameOrNickName(String searchString) throws IOException {
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
