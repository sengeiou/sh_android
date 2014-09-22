package gm.mobi.android.ui.activities.registro;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.timeline.FollowsResultEvent;
import gm.mobi.android.task.jobs.timeline.FollowsJob;
import gm.mobi.android.ui.base.BaseActivity;

public class FollowingActivity extends BaseActivity{

    @Inject JobManager jobManager;
    @Inject Bus bus;

    @Inject Application app;
    @Inject SQLiteOpenHelper mDbHelper;

    List<Follow> follows;
    FollowsJob mFollowsJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Integer idUser = null;
        if(db!=null){
            User user = UserManager.getCurrentUser(db);
             idUser = user.getIdUser();
        }

        jobManager.addJobInBackground(new FollowsJob(getApplicationContext(), idUser, db));

    }

    @Subscribe
    public void onGetFollowingResult(FollowsResultEvent event){
      follows = event.getFollows();
      if(event.getStatus() == FollowsResultEvent.STATUS_SUCCESS && follows !=null){
          for(Follow f:follows){
              System.out.println(f.getFollowedUser());
          }

      }

    }


    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }
}
