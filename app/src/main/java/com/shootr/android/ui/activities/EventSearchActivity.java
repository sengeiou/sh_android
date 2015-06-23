package com.shootr.android.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ShootrApplication;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.views.EventSearchView;
import dagger.ObjectGraph;

public class EventSearchActivity extends BaseSignedInActivity implements EventSearchView {

    private SearchView searchView;
    private String currentSearchQuery;
    private ObjectGraph objectGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()){
            return;
        }
        setContainerContent(R.layout.activity_event_search);
        ButterKnife.inject(this);

        setupViews();
        setupActionBar();

        objectGraph = ShootrApplication.get(getApplicationContext()).getObjectGraph();
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

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    private void setupViews() {
        //TODO here I will have to setup an adapter
    }

    private void setupQuery() {
        if (currentSearchQuery != null) {
            searchView.setQuery(currentSearchQuery, false);
            searchView.clearFocus();
        }
    }

    private void setupQueryTextListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String queryText) {
                if (!queryText.equals(currentSearchQuery)) {
                    currentSearchQuery = queryText;
                    startSearch();
                    hideKeyboard();
                    return true;
                } else {
                    return false;
                }
            }

            @Override public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void setupSearchView() {
        searchView.setQueryHint(getString(R.string.activity_find_events_hint));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);

        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.hint_black));
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    public void startSearch() {
        searchEvents();
    }

    private void searchEvents() {
        //TODO it will need a new presenter
    }


}
