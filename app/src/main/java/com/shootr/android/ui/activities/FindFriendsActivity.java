package com.shootr.android.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.R;
import com.shootr.android.ShootrApplication;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.follows.FollowUnFollowResultEvent;
import com.shootr.android.task.events.follows.SearchPeopleLocalResultEvent;
import com.shootr.android.task.events.follows.SearchPeopleRemoteResultEvent;
import com.shootr.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import com.shootr.android.task.jobs.follows.GetFollowUnfollowUserOnlineJob;
import com.shootr.android.task.jobs.follows.SearchPeopleLocalJob;
import com.shootr.android.task.jobs.follows.SearchPeopleRemoteJob;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.UserListAdapter;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.widgets.ListViewScrollObserver;
import com.shootr.android.util.FeedbackMessage;
import com.shootr.android.util.ImageLoader;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import dagger.ObjectGraph;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class FindFriendsActivity extends BaseToolbarDecoratedActivity implements UserListAdapter.FollowUnfollowAdapterCallback {

    public static final int NO_OFFSET = 0;
    private static final String EXTRA_RESULTS = "results";
    private static final String EXTRA_SEARCH_TEXT = "search";
    private static final String EXTRA_SEARCH_OFFSET = "offset";
    private static final String EXTRA_SEARCH_HAS_MORE_ITEMS = "moreitems";
    private static final String EXTRA_SEARCH_IS_LOADING_REMOTE = "loadingremote";

    @Inject ImageLoader imageLoader;
    @Inject JobManager jobManager;
    @Inject FeedbackMessage feedbackMessage;
    @Inject @Main Bus bus;

    private SearchView searchView;

    @Bind(R.id.find_friends_search_results_list) ListView resultsListView;
    @Bind(R.id.find_friends_search_results_empty) TextView emptyOrErrorView;
    @BindString(R.string.connection_lost) String connectionLost;
    View progressViewContent;
    View progressView;

    private UserListAdapter adapter;
    private ObjectGraph objectGraph;

    private String currentSearchQuery;
    private int currentResultOffset;
    private int currentPage;
    private boolean hasMoreItemsToLoad;
    private boolean isLoadingRemoteData;

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_find_friends;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        objectGraph = ShootrApplication.get(getApplicationContext()).getObjectGraph();
        ButterKnife.bind(this);
        setupViews();
        setupActionBar();
    }

    @Override protected void initializePresenter() {
        /* no-op */
    }

    private void setupViews() {
        currentPage = 0;
        if (adapter == null) {
            adapter = new UserListAdapter(this, imageLoader);
            adapter.setCallback(this);
        }
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

    @OnItemClick(R.id.find_friends_search_results_list)
    public void openProfile(int position) {
        UserModel user = (UserModel) resultsListView.getItemAtPosition(position);
        startActivity(ProfileContainerActivity.getIntent(this, user.getIdUser()));
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
        currentPage++;
    }

    public void makeNextRemoteSearch() {
        if (!isLoadingRemoteData) {
            SearchPeopleRemoteJob remoteSearchJob = objectGraph.get(SearchPeopleRemoteJob.class);
            remoteSearchJob.init(currentSearchQuery, currentPage);
            jobManager.addJobInBackground(remoteSearchJob);
            isLoadingRemoteData = true;
        }
    }

    @Subscribe
    public void receivedRemoteResult(SearchPeopleRemoteResultEvent event) {
        isLoadingRemoteData = false;
        List<UserModel> users = event.getResult();
        int usersReturned = users.size();
        if (usersReturned > 0) {
            Timber.d("Received %d remote results", usersReturned);
            setListContent(users, currentResultOffset);
            setEmpty(false);
            incrementOffset(usersReturned);
        } else {
            if (adapter.getCount() == 0) {
                Timber.d("No remote results found.");
                setEmpty(true);
            } else {
                resultsListView.removeFooterView(progressView);
            }
        }
    }

    @Subscribe
    public void receivedLocalResult(SearchPeopleLocalResultEvent event) {
        List<UserModel> results = event.getResult();
        Timber.d("Received %d local results", results.size());
        setListContent(results, NO_OFFSET);
        setEmpty(false);

    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        feedbackMessage.show(getView(), connectionLost);
        setLoading(false);
        isLoadingRemoteData = false;
        if (adapter.getCount() == 0) {
            setCantLoadMessage();
        }
    }

    @Subscribe
    public void onCommunicationError(CommunicationErrorEvent event) {
        Timber.d("No local results. Waiting for remote results");
    }

    private void incrementOffset(int newItems) {
        currentResultOffset += newItems;
    }

    private void setListContent(List<UserModel> users, int offset) {
        if (offset > NO_OFFSET) {
            adapter.addItems(users);
        } else {
            adapter.setItems(users);
        }
        adapter.notifyDataSetChanged();
    }

    private void setLoading(boolean loading) {
        progressViewContent.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void setEmpty(boolean empty) {
        emptyOrErrorView.setVisibility(empty ? View.VISIBLE : View.GONE);
        resultsListView.setVisibility(empty ? View.GONE : View.VISIBLE);
        if (empty) {
            emptyOrErrorView.setText(R.string.activity_find_friends_empty_message);
        }
    }

    private void setCantLoadMessage() {
        emptyOrErrorView.setText(R.string.activity_find_friends_cant_load_message);
        emptyOrErrorView.setVisibility(View.VISIBLE);
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    private void clearResults() {
        adapter.setItems(new ArrayList<UserModel>(0));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_RESULTS, (Serializable) adapter.getItems());
        outState.putString(EXTRA_SEARCH_TEXT, currentSearchQuery);
        outState.putInt(EXTRA_SEARCH_OFFSET, currentResultOffset);
        outState.putBoolean(EXTRA_SEARCH_HAS_MORE_ITEMS, hasMoreItemsToLoad);
        outState.putBoolean(EXTRA_SEARCH_IS_LOADING_REMOTE, isLoadingRemoteData);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentSearchQuery = savedInstanceState.getString(EXTRA_SEARCH_TEXT);
        currentResultOffset = savedInstanceState.getInt(EXTRA_SEARCH_OFFSET, 0);
        hasMoreItemsToLoad = savedInstanceState.getBoolean(EXTRA_SEARCH_HAS_MORE_ITEMS, false);
        isLoadingRemoteData = savedInstanceState.getBoolean(EXTRA_SEARCH_IS_LOADING_REMOTE, false);
        List<UserModel> restoredResults = (List<UserModel>) savedInstanceState.getSerializable(EXTRA_RESULTS);

        if (restoredResults != null && !restoredResults.isEmpty()) {
            setListContent(restoredResults, currentResultOffset);
            setEmpty(false);
            setLoading(hasMoreItemsToLoad);
        }
    }

    @Override public void follow(int position) {
        UserModel user = adapter.getItem(position);
        followUser(user);
    }

    @Override public void unFollow(int position) {
        UserModel user = adapter.getItem(position);
        unfollowUser(user);
    }

    public void startFollowUnfollowUserJob(UserModel userVO, Context context, int followType){
        GetFollowUnFollowUserOfflineJob jobOffline = ShootrApplication.get(context).getObjectGraph().get(GetFollowUnFollowUserOfflineJob.class);
        jobOffline.init(userVO.getIdUser(), followType);
        jobManager.addJobInBackground(jobOffline);

        GetFollowUnfollowUserOnlineJob
          jobOnline = ShootrApplication.get(context).getObjectGraph().get(GetFollowUnfollowUserOnlineJob.class);
        jobManager.addJobInBackground(jobOnline);
    }

    public void followUser(UserModel user){
        startFollowUnfollowUserJob(user, this, UserDtoFactory.FOLLOW_TYPE);
    }

    public void unfollowUser(final UserModel user){
        new AlertDialog.Builder(this).setMessage("Unfollow "+user.getUsername()+"?")
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  startFollowUnfollowUserJob(user, getApplicationContext(), UserDtoFactory.UNFOLLOW_TYPE);
              }
          })
          .setNegativeButton("No", null)
          .create()
          .show();
    }

    @Subscribe
    public void onFollowUnfollowReceived(FollowUnFollowResultEvent event) {
        Pair<String, Boolean> result = event.getResult();
        String idUser = result.first;
        Boolean following = result.second;

        List<UserModel> usersInList = adapter.getItems();
        for (int i = 0; i < usersInList.size(); i++) {
            UserModel userModel = usersInList.get(i);
            if (userModel.getIdUser().equals(idUser)) {
                userModel.setRelationship(following? FollowEntity.RELATIONSHIP_FOLLOWING : FollowEntity.RELATIONSHIP_NONE);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }
}
