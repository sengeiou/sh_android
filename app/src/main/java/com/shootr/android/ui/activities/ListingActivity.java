package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.ListingStreamsAdapter;
import com.shootr.android.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnRemoveFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.presenter.ListingListPresenter;
import com.shootr.android.ui.views.ListingView;
import java.util.List;
import javax.inject.Inject;

public class ListingActivity extends BaseToolbarDecoratedActivity implements ListingView {

    private static final String EXTRA_ID_USER = "idUser";
    public static final String ADD_TO_FAVORITES_MENU_TITLE = "Add to Favorites";

    @Bind(R.id.listing_list) RecyclerView listingList;
    @Bind(R.id.listing_loading) View loadingView;

    @Inject ListingListPresenter presenter;

    private ListingStreamsAdapter adapter;

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

    @Override public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(ADD_TO_FAVORITES_MENU_TITLE)) {
            StreamResultModel streamResultModel = adapter.getItem(item.getOrder());
            presenter.addToFavorite(streamResultModel);
            initializePresenter();
        }
        return true;
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

    @Override public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override public void setFavoriteStreams(List<StreamResultModel> favoriteStreams, List<StreamResultModel> listingStreams) {
        adapter = new ListingStreamsAdapter(picasso, new OnStreamClickListener() {
            @Override public void onStreamClick(StreamResultModel stream) {
                presenter.selectStream(stream);
            }
        }, new OnFavoriteClickListener() {
            @Override public void onFavoriteClick(StreamResultModel streamResultModel) {
                presenter.addToFavorite(streamResultModel);
            }
        }, new OnRemoveFavoriteClickListener() {
            @Override public void onRemoveFavoriteClick(StreamResultModel stream) {
                presenter.removeFromFavorites(stream);
            }
        }, favoriteStreams);

        adapter.setStreams(listingStreams);
        listingList.setAdapter(adapter);

        adapter.setFavoriteStreams(favoriteStreams);
    }

    @Override public void showContent() {
        listingList.setVisibility(View.VISIBLE);
    }
}
