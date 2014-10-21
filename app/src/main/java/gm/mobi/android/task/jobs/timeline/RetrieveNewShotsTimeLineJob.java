package gm.mobi.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.objects.ShotEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.timeline.NewShotsReceivedEvent;
import gm.mobi.android.ui.model.ShotModel;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Inject;

public class RetrieveNewShotsTimeLineJob extends TimelineJob<NewShotsReceivedEvent>{

    private ShotManager shotManager;
    private BagdadService service;
    private UserEntity currentUser;

    @Inject public RetrieveNewShotsTimeLineJob(Application context, Bus bus, BagdadService service, NetworkUtil networkUtil,
      ShotManager shotManager, FollowManager followManager, SQLiteOpenHelper dbHelper) {
        super(context, bus, service, networkUtil, shotManager, followManager, dbHelper);
        this.shotManager = shotManager;
        this.service = service;
    }

    @Override public void init(UserEntity currentUser) {

        super.init(currentUser);
        this.currentUser = currentUser;
    }

    @Override protected void run() throws SQLException, IOException {
        List<ShotModel> updatedTimeline = new ArrayList<>();
        List<ShotEntity> newShots = new CopyOnWriteArrayList<>();
        Long lastModifiedDate = shotManager.getLastModifiedDate(GMContract.ShotTable.TABLE);
        if(getFollowingIds().size()>0) {
             newShots = service.getNewShots(getFollowingIds(), lastModifiedDate);
            //TODO what if newshots is empty?
            shotManager.saveShots(newShots);
        }
        updatedTimeline = shotManager.retrieveTimelineWithUsers(currentUser.getIdUser());
        postSuccessfulEvent(new NewShotsReceivedEvent(updatedTimeline, newShots.size()));
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
