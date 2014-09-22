package gm.mobi.android.ui.activities;

import android.app.Application;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.timeline.FollowsResultEvent;
import gm.mobi.android.task.events.timeline.ShotsResultEvent;
import gm.mobi.android.task.events.timeline.UsersResultEvent;
import gm.mobi.android.task.jobs.timeline.FollowsJob;
import gm.mobi.android.task.jobs.timeline.ShotsJob;
import gm.mobi.android.task.jobs.timeline.UsersJob;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import gm.mobi.android.ui.fragments.TimelineFragment;

public class MainActivity extends BaseSignedInActivity {

    @Inject
    JobManager jobManager;
    @Inject
    Bus bus;

    @Inject
    Application app;
    @Inject
    SQLiteOpenHelper mDbHelper;

    SQLiteDatabase db;
    List<Follow> mFollowingList;
    List<Shot> mShotList;
    List<User> mFollowingUserList;

    //TODO recibir parámetros para indicar si viene de registro, login o nueva
    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            // Stop execution if there is no user logged in
            return;
        }
        db = mDbHelper.getReadableDatabase();

        User user = ((GolesApplication)getApplication()).getCurrentUser();
        if(user!=null){
            jobManager.addJobInBackground(new FollowsJob(getApplicationContext(),user.getIdUser(),db));
        }

//        setContainerContent(R.layout.main_activity);
        ButterKnife.inject(this);
    }

    @Subscribe
    public void onGetFollowingResult(FollowsResultEvent event) {
        mFollowingList = event.getFollows();
        List<Integer> followingIds = event.getFollowingIds();
        if (event.getStatus() == FollowsResultEvent.STATUS_SUCCESS && mFollowingList != null) {
            //Aquí llamamos al siqguiente Job, que será obtener los users objects de los followings que hemos retornado
            jobManager.addJobInBackground(new UsersJob(getApplicationContext(), followingIds, db));
        }

    }

    @Subscribe
    public void onGetUsersFollowingResult(UsersResultEvent event){
        mFollowingUserList = event.getUsers();
        List<Integer> followingIds = event.getFollowingUserIds();
        if(event.getStatus() == UsersResultEvent.STATUS_SUCCESS && mFollowingUserList != null){
            //Aquí llamamos a obtener los shots
            jobManager.addJobInBackground(new ShotsJob(getApplicationContext(), followingIds, db));
        }
    }

    @Subscribe
    public void onGetShotsResult(ShotsResultEvent event){
        mShotList = event.getShots();
        if(event.getStatus() == ShotsResultEvent.STATUS_SUCCESS && mShotList!=null){
            //Aquí deberíamos pintar el fragment del timeline

            android.support.v4.app.FragmentTransaction ft =  getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_timeline, new TimelineFragment());
            ft.commit();
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
