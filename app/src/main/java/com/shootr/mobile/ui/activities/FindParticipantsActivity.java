package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.shootr.mobile.ui.adapters.UserListAdapter;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.FindParticipantsPresenter;
import com.shootr.mobile.ui.views.FindParticipantsView;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import java.util.List;
import javax.inject.Inject;

public class FindParticipantsActivity extends BaseToolbarDecoratedActivity implements FindParticipantsView,
  UserListAdapter.FollowUnfollowAdapterCallback {

    private static final String EXTRA_STREAM = "stream";

    private SearchView searchView;
    private UserListAdapter adapter;

    @Inject ImageLoader imageLoader;
    @Inject FeedbackMessage feedbackMessage;
    @Inject FindParticipantsPresenter findParticipantsPresenter;

    @Bind(R.id.find_participants_search_results_list) ListView resultsListView;
    @Bind(R.id.find_participants_search_results_empty) TextView emptyOrErrorView;
    @Bind(R.id.userlist_progress) ProgressBar progressBar;

    public static Intent newIntent(Context context, String idStream) {
        Intent intent = new Intent(context, FindParticipantsActivity.class);
        intent.putExtra(EXTRA_STREAM, idStream);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_find_participants;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setupViews();
        setupActionBar();
    }

    @Override protected void initializePresenter() {
        String idStream = getIntent().getStringExtra(EXTRA_STREAM);
        findParticipantsPresenter.initialize(this, idStream);
    }

    private void setupViews() {
        if (adapter == null) {
            adapter = new UserListAdapter(this, imageLoader);
            adapter.setCallback(this);
        }
        resultsListView.setAdapter(adapter);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
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
        findParticipantsPresenter.followUser(adapter.getItem(position));
    }

    @Override public void unFollow(int position) {
        final UserModel userModel = adapter.getItem(position);
        new AlertDialog.Builder(this).setMessage(String.format(getString(R.string.unfollow_dialog_message), userModel.getUsername()))
          .setPositiveButton(getString(R.string.unfollow_dialog_yes), new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  findParticipantsPresenter.unfollowUser(userModel);
              }
          })
          .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
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
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override public void showError(String message) {
        feedbackMessage.show(getView(), message);
    }

    @Override public void renderParticipants(List<UserModel> participants) {
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

    @Override public void showContent() {
        resultsListView.setVisibility(View.VISIBLE);
    }

    @Override public void hideContent() {
        resultsListView.setVisibility(View.GONE);
    }

    @OnItemClick(R.id.find_participants_search_results_list)
    public void openUserProfile(int position) {
        UserModel user = adapter.getItem(position);
        startActivityForResult(ProfileContainerActivity.getIntent(this, user.getIdUser()), 666);
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op*/
    }
}
