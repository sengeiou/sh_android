package gm.mobi.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.objects.MatchEntity;
import gm.mobi.android.db.objects.ShotEntity;
import gm.mobi.android.db.objects.TeamEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.db.objects.WatchEntity;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.timeline.ShotsResultEvent;
import gm.mobi.android.ui.model.ShotModel;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class RetrieveInitialTimeLineJob  extends TimelineJob<ShotsResultEvent>{

    private ShotManager shotManager;
    private BagdadService service;
    private FollowManager followManager;
    private UserEntity currentUser;

    @Inject public RetrieveInitialTimeLineJob(Application context, Bus bus, BagdadService service, NetworkUtil networkUtil,
      ShotManager shotManager, FollowManager followManager, SQLiteOpenHelper dbHelper) {
        super(context, bus, service, networkUtil, shotManager, followManager, dbHelper);
        this.shotManager = shotManager;
        this.service = service;
        this.followManager = followManager;
    }

    @Override public void init(UserEntity currentUser) {
        super.init(currentUser);
        this.currentUser = currentUser;
    }

    @Override protected void run() throws SQLException, IOException {
        List<ShotEntity> remoteShots = service.getShotsByUserIdList(getFollowingIds(), 0L);
        shotManager.saveShots(remoteShots);
        // Retrieve from db because we need the user objects associated to the shots
        List<ShotModel> shotsWithUsersFromServer = shotManager.retrieveTimelineWithUsers(currentUser.getIdUser());
        postSuccessfulEvent(new ShotsResultEvent(shotsWithUsersFromServer));
   }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
