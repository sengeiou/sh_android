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
import com.shootr.android.ui.views.StreamMediaView;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class StreamMediaActivity extends BaseToolbarDecoratedActivity implements StreamMediaView {

    private static final String EXTRA_STREAM_ID = "streamId";
    private static final String EXTRA_STREAM_MEDIA_COUNT = "streamMediaCount";

    private MediaAdapter mediaAdapter;

    @Bind(R.id.stream_media_recycler_view) RecyclerView mediaView;
    @Bind(R.id.media_empty) View emptyView;
    @Bind(R.id.stream_media_loading) View loadingView;

    @Inject EventMediaPresenter presenter;
    @Inject PicassoWrapper picasso;

    @Override protected int getLayoutResource() {
        return R.layout.activity_stream_media;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        mediaView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.media_adapter_number_of_columns)));
        mediaAdapter = new MediaAdapter(this, picasso);
        mediaView.setAdapter(mediaAdapter);
    }

    @Override protected void initializePresenter() {
        Intent intent = getIntent();
        String idStream = intent.getStringExtra(EXTRA_STREAM_ID);
        Integer streamMediaCount = intent.getIntExtra(EXTRA_STREAM_MEDIA_COUNT, 0);
        presenter.initialize(this, idStream, streamMediaCount);
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
