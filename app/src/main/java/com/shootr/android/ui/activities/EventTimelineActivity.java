package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import com.shootr.android.R;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.fragments.EventTimelineFragment;
import com.shootr.android.ui.fragments.ProfileFragment;
import dagger.ObjectGraph;
import javax.inject.Inject;

public class EventTimelineActivity extends BaseToolbarDecoratedActivity {

    @Inject SelectEventInteractor selectEventInteractor;

    public static Intent newIntent(Context context, String eventId, String eventTitle) {
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

        String eventId = getIntent().getStringExtra(EventTimelineFragment.EXTRA_EVENT_ID);

        setEventTitleFromIntent();

        getEvent(eventId);

        setupAndAddFragment(savedInstanceState);
    }

    //FIXME Esto es una ñapa como un castillo
    private void getEvent(String eventId) {
        selectEventInteractor.selectEvent(eventId, new Interactor.Callback<StreamSearchResult>() {
            @Override
            public void onLoaded(StreamSearchResult streamSearchResult) {
                setEventTitle(streamSearchResult.getStream().getTag());
            }
        });
    }

    @Override public void onResume() {
        super.onResume();
        String eventId = getIntent().getStringExtra(EventTimelineFragment.EXTRA_EVENT_ID);
        getEvent(eventId);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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

    private void setEventTitle(String title) {
        getToolbarDecorator().setTitle(title);
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
