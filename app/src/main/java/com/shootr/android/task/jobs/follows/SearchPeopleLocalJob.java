package com.shootr.android.task.jobs.follows;

import android.app.Application;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.task.events.follows.SearchPeopleLocalResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SearchPeopleLocalJob extends ShootrBaseJob<SearchPeopleLocalResultEvent> {

    private UserManager userManager;
    private FollowManager followManager;

    private UserEntityModelMapper userModelMapper;

    private String searchString;
    private SessionRepository sessionRepository;

    @Inject
    public SearchPeopleLocalJob(Application app, @Main Bus bus, UserManager userManager,
      FollowManager followManager, UserEntityModelMapper userModelMapper, SessionRepository sessionRepository) {
        super(app, bus);
        this.userManager = userManager;
        this.followManager = followManager;
        this.userModelMapper = userModelMapper;
        this.sessionRepository = sessionRepository;
    }

    public void init(String searchString) {
        this.searchString = searchString;
    }

    @Override protected void run() throws SQLException, IOException {
        List<UserEntity> results = retrieveDataFromDatabase();
        postSuccessfulEvent(new SearchPeopleLocalResultEvent(getUserVOs(results)));
    }

    public List<UserModel> getUserVOs(List<UserEntity> users){
        List<UserModel> userVOs = new ArrayList<>();
        for(UserEntity user:users){
            String idUser = user.getIdUser();
            FollowEntity follow = followManager.getFollowByUserIds(sessionRepository.getCurrentUserId(), idUser);
            boolean isMe = idUser.equals(sessionRepository.getCurrentUserId());
            userVOs.add(userModelMapper.toUserModel(user,follow,isMe));
        }
        return userVOs;
    }

    public List<UserEntity> retrieveDataFromDatabase(){
        return userManager.searchUsers(searchString);
    }

}
