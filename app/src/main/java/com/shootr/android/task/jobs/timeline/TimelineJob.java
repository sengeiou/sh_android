package com.shootr.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.jobs.ShootrBaseJob.SuccessEvent;

import java.sql.SQLException;
import java.util.List;

public abstract class TimelineJob<T> extends ShootrBaseJob<SuccessEvent> {

    private static final int PRIORITY = 4;

    private ShootrService service;
    private ShotManager shotManager;
    private FollowManager followManager;

    private UserEntity currentUser;

    public TimelineJob(Application context, Bus bus, ShootrService service, NetworkUtil networkUtil, ShotManager shotManager, FollowManager followManager) {
        super(new Params(PRIORITY), context, bus, networkUtil);
        this.service = service;
        this.shotManager = shotManager;
        this.followManager = followManager;
    }

    public void init(UserEntity currentUser) {
        this.currentUser = currentUser;
    }

    public List<Long> getFollowingIds() throws SQLException {
        return followManager.getUserFollowingIdsWithOwnUser(currentUser.getIdUser());
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
