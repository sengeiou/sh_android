package com.shootr.android.task.jobs.follows;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.follows.FollowUnFollowResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class GetFollowUnfollowUserOnlineJob extends ShootrBaseJob<FollowUnFollowResultEvent> {

    private static final int PRIORITY = 6;

    final ShootrService service;
    final UserManager userManager;
    final FollowManager followManager;

    @Inject
    public GetFollowUnfollowUserOnlineJob(Application application, NetworkUtil networkUtil, @Main Bus bus, ShootrService service, UserManager userManager, FollowManager followManager) {
        super(new Params(PRIORITY).requireNetwork(), application, bus, networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
    }

    @Override protected void run() throws SQLException, IOException {
         synchronized (followManager) {
             checkIfWeHaveSomeChangesInFollowAndSendToServer();
         }
    }

    public void checkIfWeHaveSomeChangesInFollowAndSendToServer() throws IOException, SQLException {
        List<FollowEntity> followsToUpdate = followManager.getDatasForSendToServerInCase();
        for (FollowEntity f : followsToUpdate) {
            if ("D".equals(f.getSynchronizedStatus())) {
                unfollowUserAndRecordInDatabase(f);
            } else {
                followUserAndRecordInDatabase(f);
            }
        }
    }


    public FollowEntity followUserAndRecordInDatabase(FollowEntity f) throws SQLException, IOException {
       FollowEntity followReceived = service.followUser(f);
        if(followReceived!=null){
            followManager.saveFollowFromServer(followReceived);
        }
        return followReceived;
    }

    public FollowEntity unfollowUserAndRecordInDatabase(FollowEntity f) throws IOException, SQLException {
        FollowEntity followReceived = service.unfollowUser(f);
        if(followReceived!=null) {
            followManager.saveFollowFromServer(followReceived);
        }
        return followReceived;
    }


    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
