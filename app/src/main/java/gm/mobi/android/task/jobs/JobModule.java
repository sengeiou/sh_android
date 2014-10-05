package gm.mobi.android.task.jobs;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.jobs.profile.GetUserInfoJob;

@Module(
        injects = {
              //  GetUserInfoJob.class
        },
        complete = false
)

public class JobModule {

  //  @Provides GetUserInfoJob provideGetUserInfoJob(Application app, Bus bus, SQLiteOpenHelper mDbHelper, BagdadService service, NetworkUtil mNetworkUtil,
  //                                                 UserManager userManager, FollowManager followManager, TeamManager teamManager , SQLiteDatabase db) {
  //      return new GetUserInfoJob(app,bus,mDbHelper,service,mNetworkUtil,userManager,followManager,teamManager, db);
  //  }
}
