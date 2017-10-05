package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.fragments.StreamTimelineFragment;
import com.shootr.mobile.util.BackStackHandler;
import dagger.ObjectGraph;
import javax.inject.Inject;

public class StreamTimelineActivity extends BaseToolbarDecoratedActivity {

    @Inject BackStackHandler backStackHandler;
    private Fragment currentFragment;

    public static Intent newIntent(Context context, String streamId, String streamTitle, String authorId) {
        Intent intent = new Intent(context, StreamTimelineActivity.class);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_ID, streamId);
        intent.putExtra(StreamTimelineFragment.EXTRA_ID_USER, authorId);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_TITLE, streamTitle);
        return intent;
    }

    public static Intent newIntent(Context context, String streamId) {
        Intent intent = new Intent(context, StreamTimelineActivity.class);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_ID, streamId);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.stream_timeline_fragment_container;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        getIntent().getExtras();
        setupAndAddFragment(savedInstanceState);
    }

    @Override public void onResume() {
        super.onResume();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backStackHandler.handleBackStack(this);
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setupAndAddFragment(Bundle savedInstanceState) {
        boolean fragmentAlreadyAddedBySystem = savedInstanceState != null;

        if (!fragmentAlreadyAddedBySystem) {
            Bundle fragmentArguments = getIntent().getExtras();
            StreamTimelineFragment streamTimelineFragment = StreamTimelineFragment.newInstance(fragmentArguments);
            currentFragment = streamTimelineFragment;

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, streamTimelineFragment, StreamTimelineFragment.TAG);
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

    @Override public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                if (currentFragment instanceof StreamTimelineFragment) {
                    ((StreamTimelineFragment) currentFragment).pickImage();
                }
            }
        }
    }
}
