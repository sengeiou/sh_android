package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import com.shootr.android.R;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.fragments.ProfileFragment;
import com.shootr.android.ui.fragments.StreamTimelineFragment;
import dagger.ObjectGraph;
import javax.inject.Inject;

public class StreamTimelineActivity extends BaseToolbarDecoratedActivity {

    @Inject SelectStreamInteractor selectStreamInteractor;

    public static Intent newIntent(Context context, String streamId, String streamTitle, Boolean removed) {
        Intent intent = new Intent(context, StreamTimelineActivity.class);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_ID, streamId);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_TITLE, streamTitle);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_REMOVED, removed);
        return intent;
    }

    public static Intent newIntent(Context context, String streamId, String streamTitle) {
        Intent intent = new Intent(context, StreamTimelineActivity.class);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_ID, streamId);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_TITLE, streamTitle);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_fragment_container;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        if (getIntent().getExtras() == null) {
            throw new RuntimeException("No intent extras, no party");
        }

        String streamId = getIntent().getStringExtra(StreamTimelineFragment.EXTRA_STREAM_ID);

        setStreamTitleFromIntent();

        getStream(streamId);

        setupAndAddFragment(savedInstanceState);
    }

    //FIXME Esto es una ñapa como un castillo
    private void getStream(String streamId) {
        selectStreamInteractor.selectStream(streamId, new Interactor.Callback<StreamSearchResult>() {
            @Override public void onLoaded(StreamSearchResult streamSearchResult) {
                setStreamTitle(streamSearchResult.getStream().getTag());
            }
        });
    }

    @Override public void onResume() {
        super.onResume();
        String streamId = getIntent().getStringExtra(StreamTimelineFragment.EXTRA_STREAM_ID);
        getStream(streamId);
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
            StreamTimelineFragment streamTimelineFragment = StreamTimelineFragment.newInstance(fragmentArguments);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, streamTimelineFragment, ProfileFragment.TAG);
            transaction.commit();
        }
    }

    private void setStreamTitleFromIntent() {
        String streamTitle = getIntent().getStringExtra(StreamTimelineFragment.EXTRA_STREAM_TITLE);
        getToolbarDecorator().setTitle(streamTitle);
    }

    private void setStreamTitle(String title) {
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
