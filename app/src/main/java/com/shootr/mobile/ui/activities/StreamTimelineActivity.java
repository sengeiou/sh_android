package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.fragments.ProfileFragment;
import com.shootr.mobile.ui.fragments.StreamTimelineFragment;
import com.shootr.mobile.util.BackStackHandler;

import javax.inject.Inject;

import dagger.ObjectGraph;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class StreamTimelineActivity extends BaseToolbarDecoratedActivity {

    @Inject BackStackHandler backStackHandler;

    public static Intent newIntent(Context context, String streamId, String streamShortTitle, String authorId) {
        Intent intent = new Intent(context, StreamTimelineActivity.class);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_ID, streamId);
        intent.putExtra(StreamTimelineFragment.EXTRA_ID_USER, authorId);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_SHORT_TITLE, streamShortTitle);
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
        checkNotNull(getIntent().getExtras());
        setupAndAddFragment(savedInstanceState);
    }

    @Override public void onResume() {
        super.onResume();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backStackHandler.handleBackStack(this);
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
