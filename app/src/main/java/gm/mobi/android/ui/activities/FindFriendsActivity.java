package gm.mobi.android.ui.activities;

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
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import dagger.ObjectGraph;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.PaginatedResult;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.follows.SearchPeopleLocalResultEvent;
import gm.mobi.android.task.events.follows.SearchPeopleRemoteResultEvent;
import gm.mobi.android.task.jobs.follows.SearchPeopleLocalJob;
import gm.mobi.android.task.jobs.follows.SearchPeopleRemoteJob;
import gm.mobi.android.ui.adapters.UserListAdapter;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import gm.mobi.android.ui.widgets.ListViewScrollObserver;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class FindFriendsActivity extends BaseSignedInActivity {

    public static final int NO_OFFSET = 0;
    @Inject Picasso picasso;
    @Inject JobManager jobManager;
    @Inject Bus bus;

    private SearchView searchView;

    @InjectView(R.id.search_results_list) ListView resultsListView;
    @InjectView(R.id.search_results_empty) View emptyView;
    View progressViewContent;
    View progressView;

    private UserListAdapter adapter;
    private ObjectGraph objectGraph;

    private String currentSearchQuery;
    private int currentResultOffset;
    private boolean isLoadingRemoteData;
    private boolean hasMoreItemsToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) return;

        setContainerContent(R.layout.activity_search);
        ButterKnife.inject(this);

        setupViews();
        setupActionBar();

        objectGraph = GolesApplication.get(getApplicationContext()).getObjectGraph();
    }

    private void setupViews() {
        adapter = new UserListAdapter(this, picasso, new ArrayList<User>(0));
        resultsListView.setAdapter(adapter);

        progressView = getLoadingView();
        progressViewContent = ButterKnife.findById(progressView, R.id.loading_progress);
        resultsListView.addFooterView(progressView, null, false);

        new ListViewScrollObserver(resultsListView).setOnScrollUpAndDownListener(
          new ListViewScrollObserver.OnListViewScrollListener() {
              @Override public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                /* no-op */
              }

              @Override public void onScrollIdle() {
                  int lastVisiblePosition = resultsListView.getLastVisiblePosition();
                  int loadingFooterPosition = resultsListView.getAdapter().getCount() - 1;
                  boolean shouldStartLoadingMore = lastVisiblePosition >= loadingFooterPosition;
                  if (shouldStartLoadingMore && hasMoreItemsToLoad) {
                      makeNextRemoteSearch();
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
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String queryText) {
                currentSearchQuery = queryText;
                startSearch();
                hideKeyboard();
                return true;
            }

            @Override public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setQueryHint(getString(R.string.activity_find_friends_hint));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        return true;
    }

    @OnItemClick(R.id.search_results_list)
    public void openProfile(int position) {
        User user = (User) resultsListView.getItemAtPosition(position);
        startActivity(ProfileContainerActivity.getIntent(this, user));
    }

    public void startSearch() {
        Timber.d("Searching \"%s\"", currentSearchQuery);
        setLoading(true);
        setEmpty(false);
        clearResults();
        makeLocalSearch();
        makeNewRemoteSearch();
    }

    public void makeLocalSearch() {
        SearchPeopleLocalJob job = objectGraph.get(SearchPeopleLocalJob.class);
        job.init(currentSearchQuery);
        jobManager.addJobInBackground(job);
    }

    public void makeNewRemoteSearch() {
        currentResultOffset = 0;
        hasMoreItemsToLoad = true;
        makeNextRemoteSearch();
    }

    public void makeNextRemoteSearch() {
        if (!isLoadingRemoteData) {
            SearchPeopleRemoteJob remoteSearchJob = objectGraph.get(SearchPeopleRemoteJob.class);
            remoteSearchJob.init(currentSearchQuery, currentResultOffset);
            jobManager.addJobInBackground(remoteSearchJob);
            isLoadingRemoteData = true;
        }
    }

    @Subscribe
    public void receivedRemoteResult(SearchPeopleRemoteResultEvent event) {
        isLoadingRemoteData = false;
        PaginatedResult<List<User>> results = event.getResult();
        List<User> users = results.getResult();
        int usersReturned = users.size();
        if (usersReturned > 0) {
            Timber.d("Received %d remote results", usersReturned);
            setListContent(users, currentResultOffset);
            setEmpty(false);
            incrementOffset(usersReturned);
            hasMoreItemsToLoad = currentResultOffset < results.getTotalItems();
            if (!hasMoreItemsToLoad) {
                setLoading(false);
            }
        } else {
            if (currentResultOffset == 0) {
                Timber.d("No remote results found.");
                setEmpty(true);
            }
        }
    }

    @Subscribe
    public void receivedLocalResult(SearchPeopleLocalResultEvent event) {
        List<User> results = event.getSearchUsers();
        if (results.size() > 0) {
            Timber.d("Received %d local results", results.size());
            setListContent(results, NO_OFFSET);
            setEmpty(false);
        } else {
            Timber.d("No local results. Waiting for remote results");
        }
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        Toast.makeText(this, R.string.connection_lost, Toast.LENGTH_SHORT).show();
        setLoading(false);
        isLoadingRemoteData = false;
    }

    private void incrementOffset(int newItems) {
        currentResultOffset += newItems;
    }

    private void setListContent(List<User> users, int offset) {
        if (offset > NO_OFFSET) {
            adapter.addItems(users);
        } else {
            adapter.setItems(users);
        }
    }

    private void setLoading(boolean loading) {
        progressViewContent.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void setEmpty(boolean empty) {
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        resultsListView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    private void clearResults() {
        adapter.setItems(new ArrayList<User>());

    }
}
