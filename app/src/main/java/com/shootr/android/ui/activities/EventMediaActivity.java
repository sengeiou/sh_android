package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
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

    private static final String EXTRA_EVENT_ID = "eventId";
    private static final String EXTRA_EVENT_MEDIA_COUNT = "eventMediaCount";

    @InjectView(R.id.event_media_recycler_view) RecyclerView mediaView;
    @InjectView(R.id.media_empty) View emptyView;
    @InjectView(R.id.media_loading) View loadingView;

    @Inject EventMediaPresenter presenter;

    @Override protected int getLayoutResource() {
        return R.layout.activity_event_media;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.inject(this);
        mediaView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    @Override protected void initializePresenter() {
        Bundle bundle = getIntent().getExtras();
        String idEvent = bundle.getString(EXTRA_EVENT_ID);
        Integer eventMediaCount = bundle.getInt(EXTRA_EVENT_MEDIA_COUNT);
        presenter.initialize(this, idEvent, eventMediaCount);
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override public void setMedia(List<ShotModel> shotsWithMedia) {
        MediaAdapter mediaAdapter = new MediaAdapter(getBaseContext(), shotsWithMedia);
        mediaView.setAdapter(mediaAdapter);
    }

    @Override public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
}
