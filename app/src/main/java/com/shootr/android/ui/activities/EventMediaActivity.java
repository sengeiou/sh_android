package com.shootr.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.EventMediaPresenter;
import com.shootr.android.ui.views.EventMediaView;
import java.util.List;
import javax.inject.Inject;

public class EventMediaActivity extends BaseToolbarDecoratedActivity implements EventMediaView {

    @InjectView(R.id.event_media_recycler_view) RecyclerView mediaView;

    @InjectView(R.id.media_empty) View emptyView;

    @Inject EventMediaPresenter presenter;

    private static final String EXTRA_EVENT_ID = "eventId";

    @Override protected int getLayoutResource() {
        return R.layout.activity_event_media;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.inject(this);
        mediaView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override protected void initializePresenter() {
        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        String idEvent = bundle.getString(EXTRA_EVENT_ID);
        presenter.initialize(this, idEvent);
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override public void setMedia(List<ShotModel> shotsWithMedia) {
        MediaAdapter mediaAdapter = new MediaAdapter(getBaseContext(),shotsWithMedia);
        mediaView.setAdapter(mediaAdapter);
    }

    @Override public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }
}
