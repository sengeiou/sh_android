package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.ParticipantsListAdapter;
import com.shootr.mobile.ui.adapters.UserListAdapter;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.AllParticipantsPresenter;
import com.shootr.mobile.ui.views.AllParticipantsView;
import com.shootr.mobile.ui.widgets.ListViewScrollObserver;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import java.util.List;
import javax.inject.Inject;

public class AllParticipantsActivity extends BaseToolbarDecoratedActivity implements AllParticipantsView,
  UserListAdapter.FollowUnfollowAdapterCallback {

    private static final String EXTRA_STREAM = "stream";

    private ParticipantsListAdapter adapter;
    private Boolean isFooterLoading = false;

    View progressViewContent;
    View progressView;

    @Bind(R.id.userlist_list) ListView userlistListView;
    @Bind(com.shootr.mobile.R.id.userlist_progress) ProgressBar progressBar;
    @Bind(R.id.userlist_empty) TextView emptyTextView;

    @Inject ImageLoader imageLoader;
    @Inject FeedbackMessage feedbackMessage;
    @Inject AllParticipantsPresenter allParticipantsPresenter;

    public static Intent newIntent(Context context, String idStream) {
        Intent intent = new Intent(context, AllParticipantsActivity.class);
        intent.putExtra(EXTRA_STREAM, idStream);
        return intent;
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_all_participants;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        userlistListView.setAdapter(getParticipantsAdapter());

        progressView = getLoadingView();
        progressViewContent = ButterKnife.findById(progressView, com.shootr.mobile.R.id.loading_progress);

        new ListViewScrollObserver(userlistListView).setOnScrollUpAndDownListener(new ListViewScrollObserver.OnListViewScrollListener() {
            @Override public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                /* no-op */
            }

            @Override public void onScrollIdle() {
                int lastVisiblePosition = userlistListView.getLastVisiblePosition();
                int loadingFooterPosition = userlistListView.getAdapter().getCount() - 1;
                boolean shouldStartLoadingMore = lastVisiblePosition >= loadingFooterPosition;
                if (shouldStartLoadingMore && !isFooterLoading) {
                    allParticipantsPresenter.makeNextRemoteSearch(adapter.getItems().get(adapter.getItems().size()-1));
                }
            }
        });

    }

    private View getLoadingView() {
        return LayoutInflater.from(this).inflate(com.shootr.mobile.R.layout.item_list_loading, userlistListView, false);
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
        }else if (item.getItemId() == com.shootr.mobile.R.id.menu_search) {
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
            adapter = new ParticipantsListAdapter(this, imageLoader);
            adapter.setCallback(this);
        }
        return adapter;
    }

    @Override public void showEmpty() {
        emptyTextView.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmpty() {
        emptyTextView.setVisibility(View.GONE);
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

    @Override public void renderAllParticipants(List<UserModel> users) {
        adapter.setItems(users);
        adapter.notifyDataSetChanged();
    }

    @Override public void showAllParticipantsList() {
        userlistListView.setVisibility(View.VISIBLE);
    }

    @Override public void goToSearchParticipants() {
        String idStream = getIntent().getStringExtra(EXTRA_STREAM);
        startActivity(FindParticipantsActivity.newIntent(this, idStream));
        overridePendingTransition(com.shootr.mobile.R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override public void renderParticipantsBelow(List<UserModel> userModels) {
        adapter.addItems(userModels);
        adapter.notifyDataSetChanged();
    }

    @Override public void hideProgressView() {
        isFooterLoading = false;
        userlistListView.removeFooterView(progressView);
    }

    @Override public void showProgressView() {
        isFooterLoading = true;
        userlistListView.addFooterView(progressView, null, false);
    }

    @Override public void follow(int position) {
        allParticipantsPresenter.followUser(adapter.getItem(position));
    }

    @Override public void unFollow(int position) {
        final UserModel userModel = adapter.getItem(position);
        new AlertDialog.Builder(this).setMessage(String.format(getString(com.shootr.mobile.R.string.unfollow_dialog_message), userModel.getUsername()))
          .setPositiveButton(getString(com.shootr.mobile.R.string.unfollow_dialog_yes), new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  allParticipantsPresenter.unfollowUser(userModel);
              }
          })
          .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
          .create()
          .show();
    }

    @OnItemClick(R.id.userlist_list)
    public void openUserProfile(int position) {
        UserModel user = adapter.getItem(position);
        startActivityForResult(ProfileContainerActivity.getIntent(this, user.getIdUser()), 666);
    }
}
