package com.shootr.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import com.shootr.android.ShootrApplication;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.objects.FollowEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.task.events.follows.SearchPeopleLocalResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SearchPeopleLocalJob extends ShootrBaseJob<SearchPeopleLocalResultEvent> {

    private static final int PRIORITY = 4;

    private UserManager userManager;
    private FollowManager followManager;

    private UserModelMapper userModelMapper;

    private String searchString;

    private Long currentUserId;

    @Inject
    public SearchPeopleLocalJob(Application app, Bus bus, NetworkUtil networkUtil, SQLiteOpenHelper openHelper,
      UserManager userManager, FollowManager followManager, UserModelMapper userModelMapper) {
        super(new Params(PRIORITY).groupBy(SearchPeopleRemoteJob.SEARCH_PEOPLE_GROUP), app, bus, networkUtil);
        this.userManager = userManager;
        this.followManager = followManager;
        this.userModelMapper = userModelMapper;
        setOpenHelper(openHelper);
    }

    public void init(String searchString) {

        this.searchString = searchString;
        currentUserId  = ShootrApplication.get(getContext()).getCurrentUser().getIdUser();
    }

    @Override protected void createDatabase() {
        createReadableDb();
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        userManager.setDataBase(db);
        followManager.setDataBase(db);
    }

    @Override protected void run() throws SQLException, IOException {
        List<UserEntity> results = retrieveDataFromDatabase();
        postSuccessfulEvent(new SearchPeopleLocalResultEvent(getUserVOs(results)));
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }


    public List<UserModel> getUserVOs(List<UserEntity> users){
        List<UserModel> userVOs = new ArrayList<>();
        for(UserEntity user:users){
            Long idUser = user.getIdUser();
            FollowEntity follow = followManager.getFollowByUserIds(currentUserId, idUser);
            boolean isMe = idUser.equals(currentUserId);
            userVOs.add(userModelMapper.toUserModel(user,follow,isMe));
        }
        return userVOs;
    }

    public List<UserEntity> retrieveDataFromDatabase(){
        return userManager.searchUsers(searchString);
    }

}