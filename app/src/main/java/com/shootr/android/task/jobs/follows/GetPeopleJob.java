package com.shootr.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.objects.FollowEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.follows.FollowsResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class GetPeopleJob extends ShootrBaseJob<FollowsResultEvent> {

    public static final int PRIORITY = 5;
    ShootrService service;

    private Long currentUserId;
    private UserManager userManager;
    private FollowManager followManager;
    private UserModelMapper userModelMapper;


    @Inject public GetPeopleJob(Application context, Bus bus, ShootrService service, NetworkUtil networkUtil, SQLiteOpenHelper openHelper,UserManager userManager, FollowManager followManager, UserModelMapper userModelMapper) {
        super(new Params(PRIORITY),context,bus,networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.userModelMapper = userModelMapper;
        setOpenHelper(openHelper);
    }


    public void setService(ShootrService service) {
        this.service = service;
    }

    @Override
    protected void run() throws IOException, SQLException {

        List<UserEntity> peopleFromDatabase = getPeopleFromDatabase();
        List<UserModel> userVOs = getUserVOs(peopleFromDatabase);
        if (peopleFromDatabase != null && peopleFromDatabase.size() > 0) {
            postSuccessfulEvent(new FollowsResultEvent(userVOs));
        }
        if (hasInternetConnection()) {
            List<UserEntity> peopleFromServer = service.getFollowing(currentUserId, 0L);
            Collections.sort(peopleFromServer, new NameComparator());
            userVOs = getUserVOs(peopleFromServer);
            postSuccessfulEvent(new FollowsResultEvent(userVOs));
        }
    }

    public List<UserModel> getUserVOs(List<UserEntity> users){
        List<UserModel> userVOs = new ArrayList<>();
        for(UserEntity user: users){
            Long idUser = user.getIdUser();
            FollowEntity follow = followManager.getFollowByUserIds(currentUserId, idUser);
            boolean isMe = currentUserId.equals(idUser);
            userVOs.add(userModelMapper.toUserModel(user,follow,isMe));
        }
        return userVOs;
    }

    public void init(long currentUserId) {
        this.currentUserId = currentUserId;
    }

    private List<UserEntity> getPeopleFromDatabase() throws SQLException {
        List<Long> usersFollowingIds = followManager.getUserFollowingIds(currentUserId);
        List<UserEntity> usersFollowing = userManager.getUsersByIds(usersFollowingIds);
        return usersFollowing;
    }

    @Override
    protected void createDatabase() {
        createReadableDb();
    }

    @Override
    protected void setDatabaseToManagers(SQLiteDatabase db) {
        followManager.setDataBase(db);
        userManager.setDataBase(db);
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

    static class NameComparator implements Comparator<UserEntity> {

        @Override public int compare(UserEntity user1, UserEntity user2) {
            return user1.getName().compareTo(user2.getName());
        }

    }
}

