package com.shootr.android.ui.activities;

import android.app.Activity;
import android.content.Context;
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
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.MatchSearchAdapter;
import com.shootr.android.ui.base.BaseActivity;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.presenter.AddMatchPresenter;
import com.shootr.android.ui.views.AddMatchView;
import java.util.List;

public class AddMatchActivity extends BaseActivity implements AddMatchView {

    private SearchView searchView;

    @InjectView(R.id.search_results_list) ListView resultsListView;
    @InjectView(R.id.search_results_empty) TextView emptyOrErrorView;
    @InjectView(R.id.search_loading) ProgressBar loadingView;

    private AddMatchPresenter addMatchPresenter;
    private MatchSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_add_match);
        ButterKnife.inject(this);
        adapter = new MatchSearchAdapter(this);
        resultsListView.setAdapter(adapter);
        initializePresenter();
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    private void initializePresenter() {
        addMatchPresenter = getObjectGraph().get(AddMatchPresenter.class);
        addMatchPresenter.initialize(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint(getString(R.string.activity_add_match_hint));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);

        SearchView.SearchAutoComplete searchAutoComplete =
          (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.white_disabled));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String queryText) {
                    addMatchPresenter.search(queryText);
                    return true;
            }

            @Override public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    @Override public void renderResults(List<MatchModel> matches) {
        adapter.setItems(matches);
        resultsListView.setVisibility(View.VISIBLE);
    }

    @Override public void hideResults() {
        resultsListView.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    @Override public void showEmpty() {
        emptyOrErrorView.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmpty() {
        emptyOrErrorView.setVisibility(View.GONE);
    }
}
