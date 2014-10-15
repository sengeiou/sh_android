package gm.mobi.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.timeline.ShotsResultEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class RetrieveInitialTimeLineJob  extends TimelineJob<ShotsResultEvent>{

    private ShotManager shotManager;
    private BagdadService service;
    @Inject public RetrieveInitialTimeLineJob(Application context, Bus bus, BagdadService service, NetworkUtil networkUtil,
      ShotManager shotManager, FollowManager followManager, SQLiteOpenHelper dbHelper) {
        super(context, bus, service, networkUtil, shotManager, followManager, dbHelper);
        this.shotManager = shotManager;
        this.service = service;
    }

    @Override protected void run() throws SQLException, IOException {
        super.run();
        List<Shot> remoteShots = service.getShotsByUserIdList(getFollowingIds(), 0L);
        shotManager.saveShots(remoteShots);
        // Retrieve from db because we need the user objects associated to the shots
        List<Shot> shotsWithUsersFromServer = shotManager.retrieveTimelineWithUsers();
        postSuccessfulEvent(new ShotsResultEvent(shotsWithUsersFromServer));
   }


    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
