package com.shootr.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.EventMediaPresenter;
import com.shootr.android.ui.views.EventMediaView;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class EventMediaActivity extends BaseToolbarDecoratedActivity implements EventMediaView {

    private static final String EXTRA_EVENT_ID = "eventId";
    private static final String EXTRA_EVENT_MEDIA_COUNT = "eventMediaCount";

    private MediaAdapter mediaAdapter;

    @Bind(R.id.event_media_recycler_view) RecyclerView mediaView;
    @Bind(R.id.media_empty) View emptyView;
    @Bind(R.id.event_media_loading) View loadingView;

    @Inject EventMediaPresenter presenter;
    @Inject PicassoWrapper picasso;

    @Override protected int getLayoutResource() {
        return R.layout.activity_event_media;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        mediaView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.media_adapter_number_of_columns)));
        mediaAdapter = new MediaAdapter(this, picasso);
        mediaView.setAdapter(mediaAdapter);
    }

    @Override protected void initializePresenter() {
        Intent intent = getIntent();
        String idEvent = intent.getStringExtra(EXTRA_EVENT_ID);
        Integer eventMediaCount = intent.getIntExtra(EXTRA_EVENT_MEDIA_COUNT, 0);
        presenter.initialize(this, idEvent, eventMediaCount);
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override public void setMedia(List<ShotModel> shotsWithMedia) {
        mediaAdapter.setShotsWithMedia(shotsWithMedia);
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

    @Override public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
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
