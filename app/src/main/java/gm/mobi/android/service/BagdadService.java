package gm.mobi.android.service;

import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.Team;
import gm.mobi.android.db.objects.User;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface BagdadService {

    public User login(String id, String password) throws IOException;

    public List<Follow> getFollows(Long idUser, Long lastModifiedDate, int typeFollow, boolean includeDeleted) throws IOException;

    public List<User> getFollowings(Long idUser, Long lastModifiedDate) throws IOException;

    public List<Shot> getNewShots(List<Long> followingUserIds, Long newestShotDate) throws IOException;

    public List<Shot> getOlderShots(List<Long> followingUserIds, Long oldestShotDate) throws IOException;

    public List<Shot> getShotsByUserIdList(List<Long> followingUserIds, Long lastModifiedDate) throws IOException;

    public Shot postNewShot(Long idUser, String comment) throws IOException;

    public User getUserByIdUser(Long idUser) throws IOException;

    public Team getTeamByIdTeam(Long idTeam) throws  IOException;

    public List<Team> getTeamsByIdTeams(Set<Long> teamIds, Long lastModifiedDate) throws  IOException;

    public Follow getFollowRelationship(Long idUser, Long idCurrentUser, int typeFollow) throws IOException;

    public List<User> searchUsersByNameOrNickName(String searchString) throws IOException;

}
