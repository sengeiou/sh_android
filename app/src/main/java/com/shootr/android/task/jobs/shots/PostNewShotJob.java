package com.shootr.android.task.jobs.shots;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.shots.PostNewShotResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;

public class PostNewShotJob extends ShootrBaseJob<PostNewShotResultEvent> {

    private static final int PRIORITY = 5;

    ShootrService service;

    private UserEntity currentUser;
    private String comment;

    @Inject public PostNewShotJob(Application application, NetworkUtil networkUtil, Bus bus, ShootrService service) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
    }

    public void init(UserEntity currentUser, String comment){
        this.currentUser = currentUser;
        this.comment = comment;
    }

    @Override
    protected void run() throws SQLException, IOException {
        ShotEntity postedShot = service.postNewShot(currentUser.getIdUser(), comment);
        postSuccessfulEvent(new PostNewShotResultEvent(postedShot));
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }

}
