package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.melnykov.fab.FloatingActionButton;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.EventsListAdapter;
import com.shootr.android.ui.adapters.recyclerview.FadeDelayedItemAnimator;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.presenter.EventsListPresenter;
import com.shootr.android.ui.views.EventsListView;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class EventsListActivity extends BaseSignedInActivity implements EventsListView {

    public static final String KEY_EVENT_ID = "event";
    private static final String KEY_SEARCH_QUERY = "search";

    @InjectView(R.id.events_list) RecyclerView eventsList;
    @InjectView(R.id.events_add_event) FloatingActionButton addEventButton;
    @InjectView(R.id.events_empty) View emptyView;
    @InjectView(R.id.events_loading) View loadingView;

    @Inject EventsListPresenter presenter;
    @Inject PicassoWrapper picasso;

    private EventsListAdapter adapter;
    private SearchView searchView;
    private String restoredQuery;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            return;
        }
        setContainerContent(R.layout.activity_events_list);
        setupActionbar();
        initializeViews();

        initializePresenter(savedInstanceState);
    }

    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    private void initializeViews() {
        ButterKnife.inject(this);
        eventsList.setLayoutManager(new LinearLayoutManager(this));
        eventsList.setItemAnimator(new FadeDelayedItemAnimator(50));

        adapter = new EventsListAdapter(picasso, getResources());
        eventsList.setAdapter(adapter);

        adapter.setOnEventClickListener(new EventsListAdapter.OnEventClickListener() {
            @Override public void onEventClick(EventModel event) {
                presenter.selectEvent(event);
            }
        });
    }

    private void initializePresenter(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            restoredQuery = savedInstanceState.getString(KEY_SEARCH_QUERY);
            if (restoredQuery != null) {
                presenter.initialize(this, restoredQuery);
                return;
            }
        }
        presenter.initialize(this);
    }

    @OnClick(R.id.events_add_event)
    public void onAddEvent() {
        startActivity(new Intent(this, NewEventActivity.class));
    }

    //region Activity methods
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.events_list, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchItem.getActionView();
        setupSearchView();
        return true;
    }

    private void setupSearchView() {
        searchView.setQueryHint(getString(R.string.menu_search_events));

        SearchView.SearchAutoComplete searchAutoComplete =
          (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.white_disabled));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String queryText) {
                presenter.search(queryText);
                return true;
            }

            @Override public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        if (restoredQuery != null) {
            searchView.setIconified(false);
            searchView.setQuery(restoredQuery, true);
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SEARCH_QUERY, String.valueOf(searchView.getQuery()));
    }
    //endregion

    //region View methods
    @Override public void renderEvents(List<EventResultModel> events) {
        adapter.setEvents(events);
    }

    @Override public void setCurrentVisibleEventId(Long eventId) {
        adapter.setCurrentVisibleEvent(eventId);
    }

    @Override public void showContent() {
        eventsList.setVisibility(View.VISIBLE);
    }

    @Override public void hideContent() {
        eventsList.setVisibility(View.GONE);
    }

    @Override public void closeScrenWithEventResult(Long idEvent) {
        Intent resultIntent = getIntent();
        resultIntent.putExtra(KEY_EVENT_ID, idEvent);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    @Override public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    @Override public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    //endregion
}
