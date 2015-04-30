package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.dagger.EventTimelineInteractorsModule;
import com.shootr.android.ui.fragments.EventTimelineFragment;
import com.shootr.android.ui.fragments.ProfileFragment;
import com.shootr.android.ui.presenter.TimelinePresenter;
import dagger.ObjectGraph;
import javax.inject.Inject;

public class EventTimelineActivity extends BaseToolbarDecoratedActivity {

    public static Intent newIntent(Context context, Long eventId, String eventTitle) {
        Intent intent = new Intent(context, EventTimelineActivity.class);
        intent.putExtra(EventTimelineFragment.EXTRA_EVENT_ID, eventId);
        intent.putExtra(EventTimelineFragment.EXTRA_EVENT_TITLE, eventTitle);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_fragment_container;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        if (getIntent().getExtras() == null) {
            throw new RuntimeException("No intent extras, no party");
        }
        setEventTitleFromIntent();
        setupAndAddFragment(savedInstanceState);
    }

    private void setupAndAddFragment(Bundle savedInstanceState) {
        boolean fragmentAlreadyAddedBySystem = savedInstanceState != null;

        if (!fragmentAlreadyAddedBySystem) {
            Bundle fragmentArguments = getIntent().getExtras();
            EventTimelineFragment eventTimelineFragment = EventTimelineFragment.newInstance(fragmentArguments);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, eventTimelineFragment, ProfileFragment.TAG);
            transaction.commit();
        }
    }

    private void setEventTitleFromIntent() {
        String eventTitle = getIntent().getStringExtra(EventTimelineFragment.EXTRA_EVENT_TITLE);
        getToolbarDecorator().setTitle(eventTitle);
    }

    @Override protected void initializePresenter() {
        /* no-op: no presenter here, just a dummy container activity */
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected ObjectGraph getObjectGraph() {
        return super.getObjectGraph().plus(new EventTimelineInteractorsModule());
    }
}
