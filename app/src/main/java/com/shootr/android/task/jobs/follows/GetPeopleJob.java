package com.shootr.android.task.jobs.follows;

import android.app.Application;
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
import java.util.Arrays;
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


    @Inject public GetPeopleJob(Application context, Bus bus, ShootrService service, NetworkUtil networkUtil,
      UserManager userManager, FollowManager followManager, UserModelMapper userModelMapper) {
        super(new Params(PRIORITY),context,bus,networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.userModelMapper = userModelMapper;
    }


    public void setService(ShootrService service) {
        this.service = service;
    }

    @Override
    protected void run() throws IOException, SQLException {
        List<UserModel> userModels;
        List<UserEntity> peopleFromDatabase = getPeopleFromDatabase();
        if (peopleFromDatabase != null && !peopleFromDatabase.isEmpty()) {
            userModels = getUserVOs(peopleFromDatabase);
            postSuccessfulEvent(new FollowsResultEvent(userModels));
        }
        if (hasInternetConnection()) {
            List<UserEntity> peopleFromServer = service.getFollowing(currentUserId, 0L);
            if (peopleFromServer != null) {
                userModels = getUserVOs(peopleFromServer);
                postSuccessfulEvent(new FollowsResultEvent(userModels));
            }
        }
    }

    private List<UserModel> orderUserModelsByUsername(List<UserModel> originalList) {
        Collections.sort(originalList, new UsernameComparator());
        return originalList;
    }

    public List<UserModel> getUserVOs(List<UserEntity> users){
        List<UserModel> userVOs = new ArrayList<>();
        for(UserEntity user: users){
            Long idUser = user.getIdUser();
            FollowEntity follow = followManager.getFollowByUserIds(currentUserId, idUser);
            boolean isMe = currentUserId.equals(idUser);
            userVOs.add(userModelMapper.toUserModel(user,follow,isMe));
        }
        userVOs = orderUserModelsByUsername(userVOs);
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

