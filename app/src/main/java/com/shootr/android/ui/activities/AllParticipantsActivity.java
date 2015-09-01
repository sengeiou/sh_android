package com.shootr.android.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.UserListAdapter;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.presenter.AllParticipantsPresenter;
import com.shootr.android.ui.views.AllParticipantsView;
import com.shootr.android.util.ImageLoader;
import java.util.List;
import javax.inject.Inject;

public class AllParticipantsActivity extends BaseToolbarDecoratedActivity implements AllParticipantsView, UserListAdapter.FollowUnfollowAdapterCallback {

    private static final String EXTRA_STREAM = "stream";

    private UserListAdapter adapter;

    @Bind(R.id.userlist_list) ListView userlistListView;
    @Bind(R.id.userlist_progress) ProgressBar progressBar;
    @Bind(R.id.userlist_empty) TextView emptyTextView;

    @Inject ImageLoader imageLoader;
    @Inject AllParticipantsPresenter allParticipantsPresenter;

    public static Intent newIntent(Context context, String idStream) {
        Intent intent = new Intent(context, AllParticipantsActivity.class);
        intent.putExtra(EXTRA_STREAM, idStream);
        return intent;
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        //TODO
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_all_participants;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        userlistListView.setAdapter(getParticipantsAdapter());
    }

    @Override protected void initializePresenter() {
        String idStream = getIntent().getStringExtra(EXTRA_STREAM);
        allParticipantsPresenter.initialize(this, idStream);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else if (item.getItemId() == R.id.menu_search) {
            allParticipantsPresenter.searchClicked();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onResume() {
        super.onResume();
        allParticipantsPresenter.resume();
    }

    @Override public void onPause() {
        super.onPause();
        allParticipantsPresenter.pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_participants, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private ListAdapter getParticipantsAdapter() {
        if (adapter == null) {
            adapter = new UserListAdapter(this, imageLoader);
            adapter.setCallback(this);
        }
        return adapter;
    }

    @Override public void showEmpty() {

    }

    @Override public void hideEmpty() {

    }

    @Override public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override public void renderAllParticipants(List<UserModel> users) {
        adapter.setItems(users);
        adapter.notifyDataSetChanged();
    }

    @Override public void showAllParticipantsList() {
        userlistListView.setVisibility(View.VISIBLE);
    }

    @Override public void refreshParticipants(List<UserModel> participants) {
        adapter.setItems(participants);
        adapter.notifyDataSetChanged();
    }

    @Override public void goToSearchParticipants() {
        //TODO go to search participants activity
    }

    @Override public void follow(int position) {
        allParticipantsPresenter.followUser(adapter.getItem(position), this);
    }

    @Override public void unFollow(int position) {
        final UserModel userModel = adapter.getItem(position);
        final Context context = this;
        new AlertDialog.Builder(this).setMessage("Unfollow "+userModel.getUsername() + "?")
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  allParticipantsPresenter.unfollowUser(userModel, context);
              }
          })
          .setNegativeButton("No", null)
          .create()
          .show();
    }

    @OnItemClick(R.id.userlist_list)
    public void openUserProfile(int position) {
        UserModel user = adapter.getItem(position);
        startActivityForResult(ProfileContainerActivity.getIntent(this, user.getIdUser()), 666);
    }
}
