package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.presenter.StreamMediaPresenter;
import com.shootr.mobile.ui.views.StreamMediaView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import java.util.List;
import javax.inject.Inject;

public class StreamMediaActivity extends BaseToolbarDecoratedActivity implements StreamMediaView {

    private static final String EXTRA_STREAM_ID = "streamId";
    private static final String EXTRA_STREAM_MEDIA_COUNT = "streamMediaCount";

    private MediaAdapter mediaAdapter;
    private GridLayoutManager layoutManager;

    @BindView(R.id.stream_media_recycler_view) RecyclerView mediaView;
    @BindView(R.id.media_empty) View emptyView;
    @BindView(R.id.stream_media_loading) View loadingView;
    @BindString(R.string.stream_media_no_more_media) String noMoreMedia;
    @BindString(R.string.analytics_screen_stream_media) String analyticsScreenStreamMedia;

    @Inject StreamMediaPresenter presenter;
    @Inject ImageLoader imageLoader;
    @Inject FeedbackMessage feedbackMessage;

    @Inject AnalyticsTool analyticsTool;

    @NonNull protected static Intent newIntent(Context context, String idStream, int predictedMediaCount) {
        Intent intent = new Intent(context, StreamMediaActivity.class);
        intent.putExtra(EXTRA_STREAM_ID, idStream);
        intent.putExtra(EXTRA_STREAM_MEDIA_COUNT, predictedMediaCount);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_stream_media;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        analyticsTool.analyticsStart(getBaseContext(), analyticsScreenStreamMedia);
        layoutManager =
          new GridLayoutManager(this, getResources().getInteger(R.integer.media_adapter_number_of_columns));
        mediaView.setLayoutManager(layoutManager);
        mediaAdapter = new MediaAdapter(this, imageLoader);
        mediaView.setAdapter(mediaAdapter);
        setupListScrollListeners();
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

    private void setupListScrollListeners() {
        mediaView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                checkIfEndOfListVisible();
            }
        });
    }

    private void checkIfEndOfListVisible() {
        int lastItemPosition = mediaAdapter.getCount() - 1;
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        if (lastItemPosition == lastVisiblePosition && lastItemPosition >= 1) {
            presenter.showingLastMedia(mediaAdapter.getLastMedia());
        }
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
        feedbackMessage.show(getView(), errorMessage);
    }

    @Override public void addOldMedia(List<ShotModel> shotWithMedia) {
        mediaAdapter.addShotsWithMedia(shotWithMedia);
    }

    @Override public void showLoadingOldMedia() {
        mediaAdapter.setFooterVisible(true);
    }

    @Override public void hideLoadingOldMedia() {
        mediaAdapter.setFooterVisible(false);
    }

    @Override public void showNoMoreMedia() {
        feedbackMessage.show(getView(), noMoreMedia);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onPause() {
        super.onPause();
    }
}
