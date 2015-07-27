package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.StreamsListAdapter;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.presenter.ListingListPresenter;
import com.shootr.android.ui.views.ListingView;
import java.util.List;
import javax.inject.Inject;

public class ListingActivity extends BaseToolbarDecoratedActivity implements ListingView {

    private static final String EXTRA_ID_USER = "idUser";

    @Bind(R.id.listing_list) RecyclerView listingList;
    @Bind(R.id.listing_loading) View loadingView;
    @Bind(R.id.listing_empty) View emptyView;

    @Inject ListingListPresenter presenter;

    private StreamsListAdapter adapter;

    public static Intent getIntent(Context context, String idUser) {
        Intent intent = new Intent(context, ListingActivity.class);
        intent.putExtra(EXTRA_ID_USER, idUser);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_listing;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);

        listingList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StreamsListAdapter(picasso, new OnStreamClickListener() {
            @Override public void onStreamClick(StreamResultModel stream) {
                presenter.selectStream(stream);
            }
        });
        listingList.setAdapter(adapter);
    }

    @Override protected void initializePresenter() {
        Intent intent = getIntent();
        String idUser = intent.getStringExtra(EXTRA_ID_USER);
        presenter.initialize(this, idUser);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    @Override public void renderStreams(List<StreamResultModel> streams) {
        adapter.setStreams(streams);
    }

    @Override public void navigateToStreamTimeline(String idStream, String title) {
        startActivity(StreamTimelineActivity.newIntent(this, idStream, title));
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void showNoStreams() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override public void hideContent() {
        listingList.setVisibility(View.GONE);
    }

    @Override public void showContent() {
        listingList.setVisibility(View.VISIBLE);
    }
}
