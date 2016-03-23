package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.recyclerview.ContributorsAdapter;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.ContributorsPresenter;
import com.shootr.mobile.ui.views.ContributorsView;
import com.shootr.mobile.util.FeedbackMessage;
import java.util.List;
import javax.inject.Inject;

public class ContributorsActivity extends BaseToolbarDecoratedActivity implements ContributorsView {

    private static final String EXTRA_STREAM = "stream";
    public static final int REQUEST_CAN_CHANGE_DATA = 1;

    private ContributorsAdapter adapter;
    private Snackbar limitContributorsSnackbar;

    @Bind(R.id.contributors_list) ListView contributorsListView;
    @Bind(R.id.contributors_progress) ProgressBar progressBar;
    @Bind(R.id.contributors_empty) TextView emptyTextView;

    @BindString(R.string.contributors_limit) String limitContributorsText;

    @Inject ContributorsPresenter presenter;
    @Inject FeedbackMessage feedbackMessage;


    public static Intent newIntent(Context context, String idStream) {
        Intent intent = new Intent(context, ContributorsActivity.class);
        intent.putExtra(EXTRA_STREAM, idStream);
        return intent;
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
        String idStream = getIntent().getStringExtra(EXTRA_STREAM);
        presenter.initialize(this, idStream);
    }
    //endregion

    private ListAdapter getContributorsAdapter() {
        if (adapter == null) {
            adapter = new ContributorsAdapter(this, imageLoader);
            //TODO adapter.setCallback(this);
        }
        return adapter;
    }

    @OnItemClick(R.id.contributors_list)
    public void onUserClick(int position) {
        if (position == 0) {
            presenter.onAddContributorClick(adapter.getCount());
        } else {
            UserModel user = adapter.getItem(position);
            openUserProfile(user.getIdUser());
        }
    }

    private void openUserProfile(String idUser) {
        startActivityForResult(ProfileContainerActivity.getIntent(this, idUser),
          REQUEST_CAN_CHANGE_DATA);
    }

    //region View methods
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
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
        Intent intent = FindContributorsActivity.newIntent(this);
        startActivity(intent);
    }

    @Override public void showContributorsLimitSnackbar() {
        limitContributorsSnackbar = Snackbar.make(getView(), limitContributorsText, Snackbar.LENGTH_LONG);
        limitContributorsSnackbar.show();
    }
    //endregion
}
