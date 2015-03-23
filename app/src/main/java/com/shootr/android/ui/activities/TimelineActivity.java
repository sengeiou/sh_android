package com.shootr.android.ui.activities;

import com.path.android.jobqueue.JobManager;
import com.shootr.android.R;
import com.shootr.android.ShootrApplication;
import com.shootr.android.task.jobs.loginregister.GCMRegistrationJob;
import com.shootr.android.ui.NavigationDrawerDecorator;
import javax.inject.Inject;

public class TimelineActivity extends BaseNavDrawerToolbarActivity {

    @Inject JobManager jobManager;

    @Override protected int getNavDrawerItemId() {
        return NavigationDrawerDecorator.NAVDRAWER_ITEM_TIMELINE;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_timeline;
    }

    @Override protected void initializeViews() {
        /* no-op: For now the activity uses the old TimelineFragment internally */
    }

    @Override protected void initializePresenter() {
        getToolbarDecorator().setTitle(null);

        //TODO well... the method's name is a lie right now. GCM Registration should be done from the actual presenter I guess
        startGCMRegistration();
    }

    @Deprecated
    private void startGCMRegistration() {
        GCMRegistrationJob job = ShootrApplication.get(this).getObjectGraph().get(GCMRegistrationJob.class);
        jobManager.addJobInBackground(job);
    }
}
