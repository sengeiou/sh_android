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

    private UserManager userManager;
    private FollowManager followManager;
    private UserModelMapper userModelMapper;
    private SessionManager sessionManager;


    @Inject public GetPeopleJob(Application context, Bus bus, ShootrService service, NetworkUtil networkUtil,
      UserManager userManager, FollowManager followManager, UserModelMapper userModelMapper,
      SessionManager sessionManager) {
        super(new Params(PRIORITY),context,bus,networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.userModelMapper = userModelMapper;
        this.sessionManager = sessionManager;
    }


    public void setService(ShootrService service) {
        this.service = service;
    }

    @Override
    protected void run() throws IOException, SQLException {
        retrieveLocalPeople();
        if (hasInternetConnection()) {
            retrievePeopleFromServerAndUpdate();
        }
    }

    private void retrieveLocalPeople() throws SQLException {
        List<UserModel> userModels;
        List<UserEntity> peopleFromDatabase = getPeopleFromDatabase();
        if (peopleFromDatabase != null && !peopleFromDatabase.isEmpty()) {
            userModels = getUserVOs(peopleFromDatabase);
            postSuccessfulEvent(new FollowsResultEvent(userModels));
        }
    }

    private void retrievePeopleFromServerAndUpdate() throws IOException, SQLException {
        List<UserEntity> people = retrievePeopleFromServer();
        savePeople(people);
    }

    private List<UserEntity> retrievePeopleFromServer() throws IOException {
        List<UserEntity> peopleFromServer = service.getFollowing(sessionManager.getCurrentUserId(), 0L);
        if (peopleFromServer != null) {
            List<UserModel> userModels = getUserVOs(peopleFromServer);
            postSuccessfulEvent(new FollowsResultEvent(userModels));
        }
        return peopleFromServer;
    }

    private void savePeople(List<UserEntity> people) throws SQLException {
        userManager.saveUsersFromServer(people);
    }

    private List<UserModel> orderUserModelsByUsername(List<UserModel> originalList) {
        Collections.sort(originalList, new UsernameComparator());
        return originalList;
    }

    public List<UserModel> getUserVOs(List<UserEntity> users){
        List<UserModel> userVOs = new ArrayList<>();
        for(UserEntity user: users){
            Long idUser = user.getIdUser();
            Long currentUserId = sessionManager.getCurrentUserId();
            FollowEntity follow = followManager.getFollowByUserIds(currentUserId, idUser);
            boolean isMe = idUser.equals(currentUserId);
            userVOs.add(userModelMapper.toUserModel(user,follow,isMe));
        }
        userVOs = orderUserModelsByUsername(userVOs);
        return userVOs;
    }

    private List<UserEntity> getPeopleFromDatabase() throws SQLException {
        List<Long> usersFollowingIds = followManager.getUserFollowingIds(sessionManager.getCurrentUserId());
        List<UserEntity> usersFollowing = userManager.getUsersByIds(usersFollowingIds);
        return usersFollowing;
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

    static class UsernameComparator implements Comparator<UserModel> {

        @Override public int compare(UserModel user1, UserModel user2) {
            return user1.getUsername()
              .compareToIgnoreCase(user2.getUsername());
        }

    }
}

