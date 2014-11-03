package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.ShootrApplication;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.objects.FollowEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.service.ShootrService;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.ShootrBaseJob;
import gm.mobi.android.ui.model.UserModel;
import gm.mobi.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class GetUsersFollowsJob extends ShootrBaseJob<FollowsResultEvent> {

    private static final int PRIORITY = 5;

    ShootrService service;
    private Long idUserToRetrieveFollowsFrom;
    FollowManager followManager;
    private Integer followType;
    private UserEntity currentUser;
    private Long currentUserId;
    private UserModelMapper userModelMapper;

    @Inject public GetUsersFollowsJob(Application application, Bus bus,SQLiteOpenHelper openHelper, ShootrService service, NetworkUtil networkUtil, FollowManager followManager, UserModelMapper userModelMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.userModelMapper = userModelMapper;
        this.followManager = followManager;
        setOpenHelper(openHelper);
    }

    public void init(Long userId, Integer followType) {
        this.idUserToRetrieveFollowsFrom = userId;
        this.followType = followType;
        currentUser = ShootrApplication.get(getContext()).getCurrentUser();

        currentUserId = currentUser!= null ? currentUser.getIdUser() : null;
    }

    @Override
    protected void run() throws IOException, SQLException {
        List<UserEntity> users = (followType.equals(UserDtoFactory.GET_FOLLOWERS)) ? getFollowerUsersFromService() : getFollowingUsersFromService();
        postSuccessfulEvent(new FollowsResultEvent(getUserVOs(users)));
    }

    public List<UserModel> getUserVOs(List<UserEntity> users){
        List<UserModel> userVOs = new ArrayList<>();
        for(UserEntity user: users){

            Long idUser = user.getIdUser();
            FollowEntity follow = followManager.getFollowByUserIds(currentUserId, idUser);
            boolean isMe = idUser.equals(currentUserId);
            userVOs.add(userModelMapper.toUserModel(user,follow,isMe));
        }
        return userVOs;
    }

    private List<UserEntity> getFollowingUsersFromService() throws IOException {
        Timber.i("Hace la llamada de getFollowing");
        return service.getFollowing(idUserToRetrieveFollowsFrom, 0L);
    }
    private List<UserEntity> getFollowerUsersFromService() throws  IOException{

        Timber.i("Hace la llamada de getFollowers");
        return service.getFollowers(idUserToRetrieveFollowsFrom,0L);
    }



    @Override protected boolean isNetworkRequired() {
        return true;
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
