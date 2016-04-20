package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.ContributorsListAdapter;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.FindContributorsPresenter;
import com.shootr.mobile.ui.views.FindContributorsView;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import java.util.List;
import javax.inject.Inject;

public class FindContributorsActivity extends BaseToolbarDecoratedActivity
  implements FindContributorsView, ContributorsListAdapter.AddRemoveContributorAdapterCallback {

    public static final String ID_STREAM = "idStream";
    public static final boolean IS_ADDING = true;
    private SearchView searchView;
    private ContributorsListAdapter adapter;

    @Inject ImageLoader imageLoader;
    @Inject FeedbackMessage feedbackMessage;
    @Inject FindContributorsPresenter findContributorsPresenter;

    @Bind(R.id.find_contributors_search_results_list) ListView resultsListView;
    @Bind(R.id.find_contributors_search_results_empty) TextView emptyOrErrorView;
    @Bind(R.id.contributorslist_progress) ProgressBar progressBar;

    public static Intent newIntent(Context context, String idStream) {
        Intent intent = new Intent(context, FindContributorsActivity.class);
        intent.putExtra(ID_STREAM, idStream);
        return intent;
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no - op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_find_contributors;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setupViews();
        setupActionBar();
    }

    private void setupViews() {
        if (adapter == null) {
            adapter = new ContributorsListAdapter(this, imageLoader, true, IS_ADDING);
            adapter.setCallback(this);
        }
        resultsListView.setAdapter(adapter);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    @Override protected void onPause() {
        super.onPause();
        findContributorsPresenter.pause();
    }

    @Override protected void onResume() {
        super.onResume();
        findContributorsPresenter.resume();
    }

    @Override protected void initializePresenter() {
        String idStream = getIntent().getStringExtra(ID_STREAM);
        findContributorsPresenter.initialize(this, idStream);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        createSearchView(searchItem);
        SearchView.SearchAutoComplete searchAutoComplete =
          (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.hint_black));
        return true;
    }

    private void createSearchView(MenuItem searchItem) {
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String queryText) {
                findContributorsPresenter.searchContributors(queryText);
                return true;
            }

            @Override public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setQueryHint(getString(R.string.activity_find_contributors_hint));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override public void renderContributors(List<UserModel> contributors) {
        adapter.setItems(contributors);
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

    @OnItemClick(R.id.find_contributors_search_results_list) public void openUserProfile(int position) {
        UserModel user = adapter.getItem(position);
        startActivityForResult(ProfileContainerActivity.getIntent(this, user.getIdUser()), 666);
    }

    @Override public void add(int position) {
        findContributorsPresenter.addContributor(adapter.getItem(position));
    }

    @Override public void remove(int position) {
        findContributorsPresenter.removeContributor(adapter.getItem(position));
    }
}
