package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.shootr.android.R;
import com.shootr.android.ui.NavigationDrawerDecorator;
import com.shootr.android.ui.dagger.ActivityTimelineInteractorsModule;
import com.shootr.android.ui.fragments.ActivityTimelineFragment;
import com.shootr.android.ui.fragments.ProfileFragment;
import dagger.ObjectGraph;

public class ActivityTimelineActivity extends BaseNavDrawerToolbarActivity {


    @Override protected int getNavDrawerItemId() {
        return NavigationDrawerDecorator.NAVDRAWER_ITEM_ACTIVITY;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_fragment_container;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        setupAndAddFragment(savedInstanceState);
    }

    private void setupAndAddFragment(Bundle savedInstanceState) {
        boolean fragmentAlreadyAddedBySystem = savedInstanceState != null;

        if (!fragmentAlreadyAddedBySystem) {
            ActivityTimelineFragment eventTimelineFragment = ActivityTimelineFragment.newInstance();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, eventTimelineFragment, ProfileFragment.TAG);
            transaction.commit();
        }
    }

    @Override protected void initializePresenter() {
        /* no-op: no presenter here, just a dummy container activity */
    }

    @Override protected ObjectGraph buildObjectGraph() {
        ObjectGraph globalObjectGraph = super.buildObjectGraph();
        return globalObjectGraph.plus(new ActivityTimelineInteractorsModule());
    }
}

