package com.shootr.android.ui.activities;

import android.content.Context;
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
import com.shootr.android.R;
import com.shootr.android.ShootrApplication;
import com.shootr.android.ui.adapters.EventsListAdapter;
import com.shootr.android.ui.adapters.listeners.OnEventClickListener;
import com.shootr.android.ui.adapters.recyclerview.FadeDelayedItemAnimator;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.presenter.FindEventsPresenter;
import com.shootr.android.ui.views.FindEventsView;
import com.shootr.android.util.PicassoWrapper;
import dagger.ObjectGraph;
import java.util.List;
import javax.inject.Inject;

public class FindEventsActivity extends BaseSignedInActivity implements FindEventsView {

    private SearchView searchView;
    private String currentSearchQuery;
    private ObjectGraph objectGraph;
    private EventsListAdapter adapter;

    @InjectView(R.id.find_events_list) RecyclerView eventsList;
    @InjectView(R.id.find_events_empty) View emptyView;
    @InjectView(R.id.find_events_loading) View loadingView;

    @Inject FindEventsPresenter findEventsPresenter;
    @Inject PicassoWrapper picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()){
            return;
        }
        setContainerContent(R.layout.activity_find_events);

        ButterKnife.inject(this);
        eventsList.setLayoutManager(new LinearLayoutManager(this));
        eventsList.setItemAnimator(new FadeDelayedItemAnimator(50));

        setupViews();
        setupActionBar();
        initializePresenter();

        objectGraph = ShootrApplication.get(getApplicationContext()).getObjectGraph();
    }

    private void initializePresenter() {
        findEventsPresenter.initialize(this);
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
        adapter = new EventsListAdapter(picasso, new OnEventClickListener() {
            @Override
            public void onEventClick(EventResultModel event) {
                findEventsPresenter.selectEvent(event);
            }
        });
        eventsList.setAdapter(adapter);
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

    //region View methods
    @Override public void hideContent() {
        eventsList.setVisibility(View.GONE);
    }

    @Override public void hideKeyboard() {
        // TODO full implementation of this method
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    @Override public void setCurrentWatchingEventId(EventResultModel currentVisibleEvent) {
        adapter.setCurrentWatchingEvent(currentVisibleEvent);
    }

    @Override public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override public void showContent() {
        eventsList.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override public void renderEvents(List<EventResultModel> eventModels) {
        adapter.setEvents(eventModels);
    }

    @Override public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override public void navigateToEventTimeline(String idEvent, String eventTitle) {
        startActivity(EventTimelineActivity.newIntent(this , idEvent, eventTitle));
    }

    public void startSearch() {
        searchEvents();
    }

    private void searchEvents() {
        findEventsPresenter.search(currentSearchQuery);
    }

    //endregion

}
