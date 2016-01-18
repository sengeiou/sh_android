package com.shootr.mobile.ui.activities;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.fragments.ActivityTimelineFragment;
import java.util.List;

public class ActivityTimelineContainerActivity extends BaseToolbarDecoratedActivity {

    @Override
    protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_fragment_container;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            ActivityTimelineFragment activityTimelineFragment = ActivityTimelineFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, activityTimelineFragment);
            transaction.commit();
        }
    }

    @Override
    protected void initializePresenter() {
        /* no-op */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBackIntent();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void handleBackIntent() {
        ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );
        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
        if(taskList.get(0).numActivities == 1 &&
          taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
            Intent intent = new Intent(this, MainTabbedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            finish();
        }
    }
}
