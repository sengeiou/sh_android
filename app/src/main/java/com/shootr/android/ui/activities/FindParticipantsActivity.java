package com.shootr.android.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.android.R;
import com.shootr.android.ShootrApplication;
import com.shootr.android.ui.adapters.UserListAdapter;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.presenter.FindParticipantsPresenter;
import com.shootr.android.ui.views.FindParticipantsView;
import com.shootr.android.util.ImageLoader;
import dagger.ObjectGraph;
import java.util.List;
import javax.inject.Inject;

public class FindParticipantsActivity extends BaseSignedInActivity implements FindParticipantsView, UserListAdapter.FollowUnfollowAdapterCallback {

    private static final String EXTRA_STREAM = "stream";

    private SearchView searchView;
    private UserListAdapter adapter;
    private View progressView;
    private ObjectGraph objectGraph;

    @Inject ImageLoader imageLoader;
    @Inject FindParticipantsPresenter findParticipantsPresenter;

    @Bind(R.id.find_participants_search_results_list) ListView resultsListView;
    @Bind(R.id.find_participants_search_results_empty) TextView emptyOrErrorView;

    public static Intent newIntent(Context context, String idStream) {
        Intent intent = new Intent(context, FindParticipantsActivity.class);
        intent.putExtra(EXTRA_STREAM, idStream);
        return intent;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContainerContent(R.layout.activity_find_participants);
        ButterKnife.bind(this);

        setupViews();
        setupActionBar();

        objectGraph = ShootrApplication.get(getApplicationContext()).getObjectGraph();

        String idStream = getIntent().getStringExtra(EXTRA_STREAM);
        findParticipantsPresenter.initialize(this, idStream);
    }

    private void setupViews() {
        if (adapter == null) {
            adapter = new UserListAdapter(this, imageLoader);
            adapter.setCallback(this);
        }
        resultsListView.setAdapter(adapter);

        progressView = getLoadingView();
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
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        createSearchView(searchItem);
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.hint_black));
        return true;
    }

    private void createSearchView(MenuItem searchItem) {
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String queryText) {
                findParticipantsPresenter.searchParticipants(queryText);
                return true;
            }

            @Override public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setQueryHint(getString(R.string.activity_find_participants_hint));
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

    @Override public void onResume() {
        super.onResume();
        findParticipantsPresenter.resume();
    }

    @Override public void onPause() {
        super.onPause();
        findParticipantsPresenter.pause();
    }

    @Override public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override public void follow(int position) {
        findParticipantsPresenter.followUser(adapter.getItem(position), this);
    }

    @Override public void unFollow(int position) {
        final UserModel userModel = adapter.getItem(position);
        final Context context = this;
        new AlertDialog.Builder(this).setMessage("Unfollow "+userModel.getUsername() + "?")
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  findParticipantsPresenter.unfollowUser(userModel, context);
              }
          })
          .setNegativeButton("No", null)
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
        progressView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        progressView.setVisibility(View.GONE);
    }

    @Override public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override public void renderParticipants(List<UserModel> participants) {
        resultsListView.setVisibility(View.VISIBLE);
        adapter.setItems(participants);
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

    @Override public void refreshParticipants(List<UserModel> participants) {
        adapter.setItems(participants);
        adapter.notifyDataSetChanged();
    }

    @OnItemClick(R.id.find_participants_search_results_list)
    public void openUserProfile(int position) {
        UserModel user = adapter.getItem(position);
        startActivityForResult(ProfileContainerActivity.getIntent(this, user.getIdUser()), 666);
    }
}
