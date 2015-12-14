package com.shootr.mobile.ui.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.fragments.ProfileFragment;
import com.shootr.mobile.ui.fragments.StreamTimelineFragment;
import dagger.ObjectGraph;
import java.util.List;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class StreamTimelineActivity extends BaseToolbarDecoratedActivity {

    public static Intent newIntent(Context context, String streamId, String streamShortTitle, String authorId) {
        Intent intent = new Intent(context, StreamTimelineActivity.class);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_ID, streamId);
        intent.putExtra(StreamTimelineFragment.EXTRA_ID_USER, authorId);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_SHORT_TITLE, streamShortTitle);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_fragment_container;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        checkNotNull(getIntent().getExtras());
        setupAndAddFragment(savedInstanceState);
    }

    @Override public void onResume() {
        super.onResume();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
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

    private void setupAndAddFragment(Bundle savedInstanceState) {
        boolean fragmentAlreadyAddedBySystem = savedInstanceState != null;

        if (!fragmentAlreadyAddedBySystem) {
            Bundle fragmentArguments = getIntent().getExtras();
            StreamTimelineFragment streamTimelineFragment = StreamTimelineFragment.newInstance(fragmentArguments);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, streamTimelineFragment, ProfileFragment.TAG);
            transaction.commit();
        }
    }

    @Override protected void initializePresenter() {
        /* no-op: no presenter here, just a dummy container activity */
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected ObjectGraph buildObjectGraph() {
        return super.buildObjectGraph();
    }
}
