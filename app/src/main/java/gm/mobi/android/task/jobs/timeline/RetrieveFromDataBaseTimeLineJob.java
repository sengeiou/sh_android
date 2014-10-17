package gm.mobi.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.timeline.ShotsResultEvent;
import gm.mobi.android.ui.model.ShotVO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class RetrieveFromDataBaseTimeLineJob  extends TimelineJob<ShotsResultEvent>{

    private ShotManager shotManager;
    private User currentUser;

    @Inject public RetrieveFromDataBaseTimeLineJob(Application context, Bus bus, BagdadService service, NetworkUtil networkUtil,
      ShotManager shotManager, FollowManager followManager, SQLiteOpenHelper dbHelper) {
        super(context, bus, service, networkUtil, shotManager, followManager, dbHelper);
        this.shotManager = shotManager;
    }

    @Override public void init(User currentUser) {

        super.init(currentUser);
        this.currentUser = currentUser;
    }

    @Override protected void run() throws SQLException, IOException {
        List<ShotVO> localShots = shotManager.retrieveTimelineWithUsers(currentUser.getIdUser());
        if (localShots != null && localShots.size() > 0) {
            // Got them already :)
            postSuccessfulEvent(new ShotsResultEvent(localShots));
        }
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
