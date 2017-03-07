package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.ContributorsListAdapter;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.ContributorsPresenter;
import com.shootr.mobile.ui.views.ContributorsView;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.MenuItemValueHolder;
import java.util.List;
import javax.inject.Inject;

public class ContributorsActivity extends BaseToolbarDecoratedActivity
  implements ContributorsView, ContributorsListAdapter.AddRemoveContributorAdapterCallback {

    private static final String EXTRA_STREAM = "stream";
    public static final int REQUEST_CAN_CHANGE_DATA = 1;
    public static final String IS_HOLDER = "isFromHolder";
    public static final boolean IS_NOT_ADDING = false;

    private ContributorsListAdapter adapter;
    private Snackbar limitContributorsSnackbar;

    @BindView(R.id.contributors_list) ListView contributorsListView;
    @BindView(R.id.contributors_progress) ProgressBar progressBar;
    @BindView(R.id.contributors_empty) TextView emptyTextView;
    @BindView(R.id.add_contributor_text) TextView addContributorText;

    @BindString(R.string.error_adding_contributor) String limitContributorsText;

    @Inject ContributorsPresenter presenter;
    @Inject FeedbackMessage feedbackMessage;

    private MenuItemValueHolder addContributorMenu = new MenuItemValueHolder();

    public static Intent newIntent(Context context, String idStream, Boolean isHolder) {
        Intent intent = new Intent(context, ContributorsActivity.class);
        intent.putExtra(EXTRA_STREAM, idStream);
        intent.putExtra(IS_HOLDER, isHolder);
        return intent;
    }

    @Override protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    //region Lifecycle
    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_contributors;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        contributorsListView.setAdapter(getContributorsAdapter());
    }

    @Override protected void initializePresenter() {
        Boolean isHolder = getIntent().getBooleanExtra(IS_HOLDER, false);
        String idStream = getIntent().getStringExtra(EXTRA_STREAM);
        presenter.initialize(this, idStream, isHolder);
    }
    //endregion

    private ListAdapter getContributorsAdapter() {
        if (adapter == null) {
            adapter = new ContributorsListAdapter(this, imageLoader, false, IS_NOT_ADDING);
            adapter.setCallback(this);
        }
        return adapter;
    }

    @OnItemClick(R.id.contributors_list) public void onUserClick(int position) {
        UserModel user = adapter.getItem(position);
        openUserProfile(user.getIdUser());
    }

    @OnItemLongClick(R.id.contributors_list) public boolean onUserLongClick(int position) {
        presenter.onLongContributorClick(adapter.getItem(position));
        return true;
    }

    private void openUserProfile(String idUser) {
        startActivityForResult(ProfileActivity.getIntent(this, idUser), REQUEST_CAN_CHANGE_DATA);
    }

    //region View methods

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contributors, menu);
        addContributorMenu.bindRealMenuItem(menu.findItem(R.id.menu_add_contributor));
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_add_contributor) {
            presenter.onAddContributorClick(adapter.getCount());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override public void showEmpty() {
        adapter.removeItems();
        adapter.notifyDataSetChanged();
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

    @Override public void renderAllContributors(List<UserModel> contributors) {
        adapter.setItems(contributors);
        adapter.notifyDataSetChanged();
    }

    @Override public void showAllContributors() {
        contributorsListView.setVisibility(View.VISIBLE);
    }

    @Override public void goToSearchContributors() {
        String idStream = getIntent().getStringExtra(EXTRA_STREAM);
        Intent intent = FindContributorsActivity.newIntent(this, idStream);
        startActivity(intent);
    }

    @Override public void showContributorsLimitSnackbar() {
        limitContributorsSnackbar = Snackbar.make(getView(), limitContributorsText, Snackbar.LENGTH_LONG);
        limitContributorsSnackbar.show();
    }

    @Override public void hideAddContributorsButton() {
        addContributorMenu.setVisible(false);
    }

    @Override public void hideAddContributorsText() {
        addContributorText.setVisibility(View.GONE);
    }

    @Override public void removeContributorFromList(UserModel userModel) {
        adapter.removeUserFromList(userModel);
    }

    @Override public void showContextMenu(final UserModel userModel) {
        new CustomContextMenu.Builder(this).addAction(R.string.remove, new Runnable() {
            @Override public void run() {
                presenter.removeContributor(userModel);
            }
        }).show();
    }

    @Override public void renderFollow(UserModel userModel) {
        adapter.followUser(userModel);
    }

    @Override public void renderUnfollow(UserModel userModel) {
        adapter.unfollowUser(userModel);
    }

    @Override public void add(int position) {
        presenter.addContributor(adapter.getItem(position));
    }

    @Override public void remove(int position) {
        presenter.removeContributor(adapter.getItem(position));
    }

    @Override public void follow(int position) {
        presenter.followContributor(adapter.getItem(position));
    }

    @Override public void unFollow(int position) {
        presenter.unfollowContributor(adapter.getItem(position));
    }
    //endregion
}
