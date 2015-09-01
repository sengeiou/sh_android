package com.shootr.android.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ShootrApplication;
import com.shootr.android.ui.adapters.UserListAdapter;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.util.ImageLoader;
import dagger.ObjectGraph;
import javax.inject.Inject;

public class FindParticipantsActivity extends BaseSignedInActivity implements UserListAdapter.FollowUnfollowAdapterCallback {

    private SearchView searchView;
    private String currentSearchQuery;
    private ObjectGraph objectGraph;
    private UserListAdapter adapter;
    private View progressViewContent;
    private View progressView;

    @Inject ImageLoader imageLoader;

    @Bind(R.id.find_participants_search_results_list) ListView resultsListView;
    @Bind(R.id.find_participants_search_results_empty) TextView emptyOrErrorView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContainerContent(R.layout.activity_find_participants);
        ButterKnife.bind(this);

        setupViews();
        setupActionBar();

        objectGraph = ShootrApplication.get(getApplicationContext()).getObjectGraph();
    }

    private void setupViews() {
        if (adapter == null) {
            adapter = new UserListAdapter(this, imageLoader);
            adapter.setCallback(this);
        }
        resultsListView.setAdapter(adapter);

        progressView = getLoadingView();
        progressViewContent = ButterKnife.findById(progressView, R.id.loading_progress);
        resultsListView.addFooterView(progressView, null, false);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    private View getLoadingView() {
        return LayoutInflater.from(this).inflate(R.layout.item_list_loading, resultsListView, false);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String queryText) {
                currentSearchQuery = queryText;
                //TODO startSearch();
                hideKeyboard();
                return true;
            }

            @Override public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setQueryHint(getString(R.string.activity_find_participants_hint));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);

        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.hint_black));

        if (currentSearchQuery != null) {
            searchView.setQuery(currentSearchQuery, false);
            searchView.clearFocus();
        }
        return true;
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

    @Override public void follow(int position) {

    }

    @Override public void unFollow(int position) {

    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

}
