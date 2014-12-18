package com.shootr.android.task.jobs.follows;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
import com.squareup.otto.Bus;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.FollowEntity;
import com.shootr.android.domain.UserEntity;
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
    private SessionManager sessionManager;

    @Inject
    public SearchPeopleLocalJob(Application app, Bus bus, NetworkUtil networkUtil, UserManager userManager,
      FollowManager followManager, UserModelMapper userModelMapper, SessionManager sessionManager) {
        super(new Params(PRIORITY).groupBy(SearchPeopleRemoteJob.SEARCH_PEOPLE_GROUP), app, bus, networkUtil);
        this.userManager = userManager;
        this.followManager = followManager;
        this.userModelMapper = userModelMapper;
        this.sessionManager = sessionManager;
    }

    public void init(String searchString) {
        this.searchString = searchString;
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
            FollowEntity follow = followManager.getFollowByUserIds(sessionManager.getCurrentUserId(), idUser);
            boolean isMe = idUser.equals(sessionManager.getCurrentUserId());
            userVOs.add(userModelMapper.toUserModel(user,follow,isMe));
        }
        return userVOs;
    }

    public List<UserEntity> retrieveDataFromDatabase(){
        return userManager.searchUsers(searchString);
    }

}
