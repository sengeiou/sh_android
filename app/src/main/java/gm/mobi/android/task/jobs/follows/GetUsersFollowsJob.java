package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserVO;
import gm.mobi.android.ui.model.mappers.UserVOMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class GetUsersFollowsJob extends BagdadBaseJob<FollowsResultEvent> {

    private static final int PRIORITY = 5;

    BagdadService service;
    private Long idUserToRetrieveFollowsFrom;
    FollowManager followManager;
    private Integer followType;
    private User currentUser;
    private Long currentUserId;
    private UserVOMapper userVOMapper;

    @Inject public GetUsersFollowsJob(Application application, Bus bus,SQLiteOpenHelper openHelper, BagdadService service, NetworkUtil networkUtil, FollowManager followManager, UserVOMapper userVOMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.userVOMapper = userVOMapper;
        this.followManager = followManager;
        setOpenHelper(openHelper);
    }

    public void init(Long userId, Integer followType) {
        this.idUserToRetrieveFollowsFrom = userId;
        this.followType = followType;
        currentUser = GolesApplication.get(getContext()).getCurrentUser();
        currentUserId = currentUser!= null ? currentUser.getIdUser() : null;
    }

    @Override
    protected void run() throws IOException, SQLException {
        List<User> users = (followType.equals(UserDtoFactory.GET_FOLLOWERS)) ? getFollowerUsersFromService() : getFollowingUsersFromService();
        postSuccessfulEvent(new FollowsResultEvent(getUserVOs(users)));
    }

    public List<UserVO> getUserVOs(List<User> users){
        List<UserVO> userVOs = new ArrayList<>();
        for(User user: users){
            Follow follow = followManager.getFollowByUserIds(currentUserId, user.getIdUser());
            userVOs.add(userVOMapper.toVO(user,follow,currentUserId));
        }
        return userVOs;
    }

    private List<User> getFollowingUsersFromService() throws IOException {
        Timber.i("Hace la llamada de getFollowing");
        return service.getFollowing(idUserToRetrieveFollowsFrom, 0L);
    }
    private List<User> getFollowerUsersFromService() throws  IOException{
        Timber.i("Hace la llamada de getFollowers");
        return service.getFollowers(idUserToRetrieveFollowsFrom,0L);
    }



    @Override protected boolean isNetworkRequired() {
        return false;
    }

    @Override protected void createDatabase() {
        /* no-op */
        createWritableDb();
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        /* no-op */
        followManager.setDataBase(db);
    }


}
