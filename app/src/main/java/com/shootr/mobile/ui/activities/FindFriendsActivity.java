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
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.task.events.CommunicationErrorEvent;
import com.shootr.mobile.task.events.ConnectionNotAvailableEvent;
import com.shootr.mobile.task.events.follows.SearchPeopleLocalResultEvent;
import com.shootr.mobile.task.events.follows.SearchPeopleRemoteResultEvent;
import com.shootr.mobile.task.jobs.follows.SearchPeopleLocalJob;
import com.shootr.mobile.task.jobs.follows.SearchPeopleRemoteJob;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.UserListAdapter;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.widgets.ListViewScrollObserver;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import dagger.ObjectGraph;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class FindFriendsActivity extends BaseToolbarDecoratedActivity implements
  UserListAdapter.FollowUnfollowAdapterCallback {

    public static final int FIRST_PAGE = 0;
    private static final String EXTRA_RESULTS = "results";
    private static final String EXTRA_SEARCH_TEXT = "search";
    private static final String EXTRA_SEARCH_HAS_MORE_ITEMS = "moreitems";
    private static final String EXTRA_SEARCH_IS_LOADING_REMOTE = "loadingremote";
    private static final String EXTRA_SEARCH_PAGE = "currentPage";

    @Inject ImageLoader imageLoader;
    @Inject InteractorHandler interactorHandler;
    @Inject FeedbackMessage feedbackMessage;
    @Inject @Main Bus bus;
    @Inject FollowInteractor followInteractor;
    @Inject UnfollowInteractor unfollowInteractor;

    private SearchView searchView;

    @Bind(com.shootr.mobile.R.id.find_friends_search_results_list) ListView resultsListView;
    @Bind(R.id.find_friends_search_results_empty) TextView emptyOrErrorView;
    @BindString(R.string.connection_lost) String connectionLost;
    View progressViewContent;
    View progressView;

    private UserListAdapter adapter;
    private ObjectGraph objectGraph;

    private String currentSearchQuery;
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
        progressView.setVisibility(View.GONE);
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
                      progressView.setVisibility(View.VISIBLE);
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
        searchView.setQueryHint(getString(com.shootr.mobile.R.string.activity_find_friends_hint));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);

        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(com.shootr.mobile.R.color.hint_black));

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
        overridePendingTransition(com.shootr.mobile.R.anim.abc_fade_in, com.shootr.mobile.R.anim.abc_fade_out);
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
        if (isAlphaNumeric(currentSearchQuery)) {
            makeLocalSearch();
            makeNewRemoteSearch();
        } else {
            setLoading(false);
            setEmpty(true);
        }
    }

    private boolean isAlphaNumeric(String s){
        String pattern= "^[a-zA-Z0-9]*$";
        if(s.matches(pattern)){
            return true;
        }
        return false;
    }

    public void makeLocalSearch() {
        SearchPeopleLocalJob job = objectGraph.get(SearchPeopleLocalJob.class);
        job.init(currentSearchQuery);
        interactorHandler.execute(job);
    }

    public void makeNewRemoteSearch() {
        currentPage = 0;
        hasMoreItemsToLoad = true;
        makeNextRemoteSearch();
    }

    public void makeNextRemoteSearch() {
        if (!isLoadingRemoteData) {
            SearchPeopleRemoteJob remoteSearchJob = objectGraph.get(SearchPeopleRemoteJob.class);
            remoteSearchJob.init(currentSearchQuery, currentPage);
            interactorHandler.execute(remoteSearchJob);
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
            setListContent(users, currentPage);
            setEmpty(false);
            incrementPage();
        } else {
            hasMoreItemsToLoad = false;
            setLoading(false);
            progressView.setVisibility(View.GONE);
            if (adapter.getCount() == 0) {
                Timber.d("No remote results found.");
                setEmpty(true);
            }
        }
    }

    @Subscribe
    public void receivedLocalResult(SearchPeopleLocalResultEvent event) {
        List<UserModel> results = event.getResult();
        Timber.d("Received %d local results", results.size());
        setListContent(results, FIRST_PAGE);
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

    private void incrementPage() {
        currentPage++;
    }

    private void setListContent(List<UserModel> users, int page) {
        if (page > FIRST_PAGE) {
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
        outState.putInt(EXTRA_SEARCH_PAGE, currentPage);
        outState.putBoolean(EXTRA_SEARCH_HAS_MORE_ITEMS, hasMoreItemsToLoad);
        outState.putBoolean(EXTRA_SEARCH_IS_LOADING_REMOTE, isLoadingRemoteData);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentSearchQuery = savedInstanceState.getString(EXTRA_SEARCH_TEXT);
        currentPage = savedInstanceState.getInt(EXTRA_SEARCH_PAGE, 0);
        hasMoreItemsToLoad = savedInstanceState.getBoolean(EXTRA_SEARCH_HAS_MORE_ITEMS, false);
        isLoadingRemoteData = savedInstanceState.getBoolean(EXTRA_SEARCH_IS_LOADING_REMOTE, false);
        List<UserModel> restoredResults = (List<UserModel>) savedInstanceState.getSerializable(EXTRA_RESULTS);

        if (restoredResults != null && !restoredResults.isEmpty()) {
            setListContent(restoredResults, currentPage);
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

    public void followUser(final UserModel user){
        followInteractor.follow(user.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                onUserFollowUpdated(user.getIdUser(), true);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                feedbackMessage.showLong(getView(), R.string.error_following_user_blocked);
            }
        });
    }

    public void unfollowUser(final UserModel user){
        new AlertDialog.Builder(this).setMessage("Unfollow "+user.getUsername()+"?")
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  unfollowInteractor.unfollow(user.getIdUser(), new Interactor.CompletedCallback() {
                      @Override
                      public void onCompleted() {
                          onUserFollowUpdated(user.getIdUser(), false);
                      }
                  });
              }
          })
          .setNegativeButton("No", null)
          .create()
          .show();
    }

    protected void onUserFollowUpdated(String idUser, Boolean following) {
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
