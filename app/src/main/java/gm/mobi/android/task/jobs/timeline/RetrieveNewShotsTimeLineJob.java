package gm.mobi.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.timeline.NewShotsReceivedEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class RetrieveNewShotsTimeLineJob extends TimelineJob<NewShotsReceivedEvent>{

    private ShotManager shotManager;
    private BagdadService service;

    @Inject public RetrieveNewShotsTimeLineJob(Application context, Bus bus, BagdadService service, NetworkUtil networkUtil,
      ShotManager shotManager, FollowManager followManager, SQLiteOpenHelper dbHelper) {
        super(context, bus, service, networkUtil, shotManager, followManager, dbHelper);
        this.shotManager = shotManager;
        this.service = service;
    }

    @Override public void init(User currentUser) {
        super.init(currentUser);
    }

    @Override protected void run() throws SQLException, IOException {
        super.run();
        List<Shot> updatedTimeline = new ArrayList<>();
        List<Shot> newShots = new ArrayList<>();
        Long lastModifiedDate = shotManager.getLastModifiedDate(GMContract.ShotTable.TABLE);
        if(getFollowingIds().size()>0) {
             newShots = service.getNewShots(getFollowingIds(), lastModifiedDate);
            //TODO what if newshots is empty?
            shotManager.saveShots(newShots);
            updatedTimeline = shotManager.retrieveTimelineWithUsers();
        }
        postSuccessfulEvent(new NewShotsReceivedEvent(updatedTimeline, newShots.size()));
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
