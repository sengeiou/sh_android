package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.UserListAdapter;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.FindFriendsPresenter;
import com.shootr.mobile.ui.views.FindFriendsView;
import com.shootr.mobile.ui.widgets.ListViewScrollObserver;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class FindFriendsActivity extends BaseToolbarDecoratedActivity
  implements FindFriendsView, UserListAdapter.FollowUnfollowAdapterCallback {

    public static final String EXTRA_QUERY = "query";
    public static final String EXTRA_RESULTS = "results";
    @Inject ImageLoader imageLoader;
    @Inject FeedbackMessage feedbackMessage;
    @Inject FindFriendsPresenter findFriendsPresenter;

    @Bind(R.id.find_friends_search_results_list) ListView resultsListView;
    @Bind(R.id.find_friends_search_results_empty) TextView emptyOrErrorView;
    @Bind(R.id.userlist_progress) ProgressBar progressBar;

    private View progressViewContent;
    private View progressView;
    private SearchView searchView;
    private UserListAdapter adapter;
    private boolean hasMoreItemsToLoad;
    private Bundle savedInstanceState;
    private String currentSearchQuery;

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_find_friends;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setupViews();
        setupActionBar();
        this.savedInstanceState = savedInstanceState;
    }

    @Override protected void initializePresenter() {
        findFriendsPresenter.initialize(this, false);
    }

    private void setupViews() {
        progressView = getLoadingView();
        progressView.setVisibility(View.GONE);
        progressViewContent = ButterKnife.findById(progressView, R.id.loading_progress);
        resultsListView.addFooterView(progressView, null, false);

        if (adapter == null) {
            adapter = new UserListAdapter(this, imageLoader);
            adapter.setCallback(this);
        }
        resultsListView.setAdapter(adapter);
        new ListViewScrollObserver(resultsListView).setOnScrollUpAndDownListener(new ListViewScrollObserver.OnListViewScrollListener() {
            @Override public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                /* no - op */
            }

            @Override public void onScrollIdle() {
                int lastVisiblePosition = resultsListView.getLastVisiblePosition();
                int loadingFooterPosition = resultsListView.getAdapter().getCount() - 1;
                boolean shouldStartLoadingMore = lastVisiblePosition >= loadingFooterPosition;
                if (shouldStartLoadingMore && hasMoreItemsToLoad) {
                    progressView.setVisibility(View.VISIBLE);
                    findFriendsPresenter.makeNextRemoteSearch();
                }
            }
        });
    }

    private View getLoadingView() {
        return LayoutInflater.from(this).inflate(R.layout.item_list_loading, resultsListView, false);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    @Override protected void onResume() {
        super.onResume();
        findFriendsPresenter.resume();
    }

    @Override protected void onPause() {
        super.onPause();
        findFriendsPresenter.pause();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        createSearchView(searchItem);
        setupQuery();

        SearchView.SearchAutoComplete searchAutoComplete =
          (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.hint_black));

        return true;
    }

    private void setupQuery() {
        if (currentSearchQuery != null) {
            searchView.setQuery(currentSearchQuery, false);
            searchView.clearFocus();
        }
    }

    private void createSearchView(MenuItem searchItem) {
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String queryText) {
                hasMoreItemsToLoad = true;
                findFriendsPresenter.searchFriends(queryText);
                return true;
            }

            @Override public boolean onQueryTextChange(String query) {
                findFriendsPresenter.queryTextChanged(query);
                currentSearchQuery = query;
                return false;
            }
        });
        searchView.setQueryHint(getString(R.string.activity_find_friends_hint));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        if (savedInstanceState != null) {
            searchView.setQuery(savedInstanceState.getCharSequence(EXTRA_QUERY), false);
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override public void renderFriends(List<UserModel> friends) {
        adapter.setItems(friends);
        adapter.notifyDataSetChanged();
    }

    @Override public void setCurrentQuery(String query) {
        searchView.setQuery(query, false);
        searchView.clearFocus();
    }

    @Override public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    @Override public void showContent() {
        resultsListView.setVisibility(View.VISIBLE);
    }

    @Override public void hideContent() {
        resultsListView.setVisibility(View.GONE);
    }

    @Override public void setHasMoreItemsToLoad(Boolean hasMoreItems) {
        this.hasMoreItemsToLoad = hasMoreItems;
    }

    @Override public void addFriends(List<UserModel> friends) {
        adapter.addItems(friends);
        adapter.notifyDataSetChanged();
    }

    @Override public void hideProgress() {
        setLoading(false);
        progressView.setVisibility(View.GONE);
    }

    @Override public void restoreState() {
        findFriendsPresenter.initialize(this, true);
    }

    @Override public void follow(int position) {
        findFriendsPresenter.followUser(adapter.getItem(position));
    }

    @Override public void unFollow(int position) {
        final UserModel userModel = adapter.getItem(position);
        new AlertDialog.Builder(this).setMessage(String.format(getString(R.string.unfollow_dialog_message),
          userModel.getUsername()))
          .setPositiveButton(getString(R.string.unfollow_dialog_yes),
            new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    findFriendsPresenter.unfollowUser(userModel);
                }
            })
          .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
          .create()
          .show();
    }

    @Override public void showEmpty() {
        emptyOrErrorView.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmpty() {
        emptyOrErrorView.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override public void showError(String message) {
        feedbackMessage.show(getView(), message);
    }

    private void setLoading(boolean loading) {
        progressViewContent.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @OnItemClick(R.id.find_friends_search_results_list) public void openUserProfile(int position) {
        UserModel user = adapter.getItem(position);
        startActivityForResult(ProfileContainerActivity.getIntent(this, user.getIdUser()), 666);
    }

    @Override protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(EXTRA_RESULTS, (Serializable) adapter.getItems());
        savedInstanceState.putString(EXTRA_QUERY, currentSearchQuery);
    }

    @Override protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreState();
        currentSearchQuery = savedInstanceState.getString(EXTRA_QUERY);
        List<UserModel> restoredResults = (List<UserModel>) savedInstanceState.getSerializable(EXTRA_RESULTS);
        findFriendsPresenter.restoreFriends(restoredResults);
    }
}
