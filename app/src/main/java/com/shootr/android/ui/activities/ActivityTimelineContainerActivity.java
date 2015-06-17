package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.fragments.ActivityTimelineFragment;

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
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
