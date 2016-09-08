package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import butterknife.BindView;
import butterknife.BindString;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.StreamsListAdapter;
import com.shootr.mobile.ui.adapters.WatchableStreamsAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.presenter.FindStreamsPresenter;
import com.shootr.mobile.ui.views.FindStreamsView;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.ShareManager;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;

public class FindStreamsActivity extends BaseToolbarDecoratedActivity implements FindStreamsView {

    private static final String EXTRA_RESULTS = "results";
    private static final String EXTRA_SEARCH_TEXT = "search";

    private SearchView searchView;
    private String currentSearchQuery;
    private StreamsListAdapter adapter;

    @BindView(R.id.find_streams_list) RecyclerView streamsList;
    @BindView(R.id.find_streams_empty) View emptyView;
    @BindView(R.id.find_streams_loading) View loadingView;
    @BindString(R.string.added_to_favorites) String addedToFavorites;
    @BindString(R.string.shared_stream_notification) String sharedStream;

    @Inject FindStreamsPresenter findStreamsPresenter;
    @Inject FeedbackMessage feedbackMessage;
    @Inject ShareManager shareManager;
    @Inject InitialsLoader initialsLoader;

    private void setupQuery() {
        if (currentSearchQuery != null) {
            searchView.setQuery(currentSearchQuery, false);
            searchView.clearFocus();
        }
    }

    private void setupQueryTextListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String queryText) {
                currentSearchQuery = queryText;
                searchStreams();
                hideKeyboard();
                return true;
            }

            @Override public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void setupSearchView() {
        searchView.setQueryHint(getString(R.string.activity_find_streams_hint));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);

        SearchView.SearchAutoComplete searchAutoComplete =
          (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.hint_black));
    }

    private void initializeStreamListAdapter() {
        adapter = new WatchableStreamsAdapter(imageLoader, initialsLoader, new OnStreamClickListener() {
            @Override public void onStreamClick(StreamResultModel stream) {
                findStreamsPresenter.selectStream(stream);
            }

            @Override public boolean onStreamLongClick(StreamResultModel stream) {
                openContextualMenu(stream);
                return true;
            }
        });
        adapter.setOnUnwatchClickListener(new OnUnwatchClickListener() {
            @Override public void onUnwatchClick() {
                findStreamsPresenter.unwatchStream();
            }
        });
        streamsList.setAdapter(adapter);
    }

    private void openContextualMenu(final StreamResultModel stream) {
        new CustomContextMenu.Builder(this).addAction(R.string.add_to_favorites_menu_title, new Runnable() {
            @Override public void run() {
                findStreamsPresenter.addToFavorites(stream);
            }
        }).addAction(R.string.share_via_shootr, new Runnable() {
            @Override public void run() {
                findStreamsPresenter.shareStream(stream);
            }
        }).addAction(R.string.share_via, new Runnable() {
            @Override public void run() {
                shareStream(stream);
            }
        }).show();
    }

    private void shareStream(StreamResultModel stream) {
        Intent shareIntent = shareManager.shareStreamIntent(this, stream.getStreamModel());
        Intents.maybeStartActivity(this, shareIntent);
    }

    private void searchStreams() {
        findStreamsPresenter.search(currentSearchQuery);
    }

    //region Lifecycle methods
    @Override protected int getLayoutResource() {
        return R.layout.activity_find_streams;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        streamsList.setLayoutManager(new LinearLayoutManager(this));
        initializeStreamListAdapter();
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        toolbarDecorator.getActionBar().setDisplayShowHomeEnabled(false);
        toolbarDecorator.getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchItem.getActionView();

        setupQueryTextListener();
        setupSearchView();
        setupQuery();

        return true;
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_RESULTS, (Serializable) adapter.getItems());
        outState.putString(EXTRA_SEARCH_TEXT, currentSearchQuery);
    }

    @Override protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        initializePresenter();
        currentSearchQuery = savedInstanceState.getString(EXTRA_SEARCH_TEXT);
        List<StreamResultModel> restoredResults =
          (List<StreamResultModel>) savedInstanceState.getSerializable(EXTRA_RESULTS);
        findStreamsPresenter.restoreStreams(restoredResults);
    }

    @Override public void initializePresenter() {
        findStreamsPresenter.initialize(this);
    }

    @Override protected void onResume() {
        super.onResume();
        findStreamsPresenter.resume();
    }

    @Override protected void onPause() {
        super.onPause();
        findStreamsPresenter.pause();
    }

    @Override public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }
    //endregion

    //region View methods
    @Override public void hideContent() {
        streamsList.setVisibility(View.GONE);
    }

    @Override public void hideKeyboard() {
        if (searchView != null) {
            InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    @Override public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override public void showContent() {
        streamsList.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override public void renderStreams(List<StreamResultModel> streamModels) {
        adapter.setStreams(streamModels);
    }

    @Override public void showError(String errorMessage) {
        feedbackMessage.show(getView(), errorMessage);
    }

    @Override public void navigateToStreamTimeline(String idStream, String streamTitle, String authorId) {
        startActivity(StreamTimelineActivity.newIntent(this, idStream, streamTitle, authorId));
    }

    @Override public void showAddedToFavorites() {
        feedbackMessage.show(getView(), addedToFavorites);
    }

    @Override public void showStreamShared() {
        feedbackMessage.show(getView(), sharedStream);
    }

    //endregion
}
