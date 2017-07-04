package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.UserListAdapter;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.NicersPresenter;
import com.shootr.mobile.ui.views.NicersView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.FeedbackMessage;
import java.util.List;
import javax.inject.Inject;

public class NicersActivity extends BaseToolbarDecoratedActivity
  implements NicersView, UserListAdapter.FollowUnfollowAdapterCallback {

    private static final String EXTRA_ID_SHOT = "idShot";
    @BindView(R.id.nicerslist_list) ListView nicerslistListView;
    @BindView(R.id.nicerslist_progress) ProgressBar progressBar;

    @Inject FeedbackMessage feedbackMessage;
    @Inject NicersPresenter presenter;
    @Inject AnalyticsTool analyticsTool;
    @Inject SessionRepository sessionRepository;

    @BindString(R.string.analytics_action_follow) String analyticsActionFollow;
    @BindString(R.string.analytics_label_follow) String analyticsLabelFollow;
    @BindString(R.string.analytics_source_nicers) String nicersSource;

    private UserListAdapter adapter;

    public static Intent newIntent(Context context, String idShot) {
        Intent intent = new Intent(context, NicersActivity.class);
        intent.putExtra(EXTRA_ID_SHOT, idShot);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_nicers;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        nicerslistListView.setAdapter(getNicersAdapter());
    }

    @Override public void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override public void onPause() {
        super.onPause();
        presenter.pause();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private UserListAdapter getNicersAdapter() {
        if (adapter == null) {
            adapter = new UserListAdapter(this, imageLoader);
            adapter.setCallback(this);
        }
        return adapter;
    }

    @Override protected void initializePresenter() {
        String idShot = getIntent().getStringExtra(EXTRA_ID_SHOT);
        presenter.initialize(this, idShot);
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override public void renderNicers(List<UserModel> users) {
        adapter.setItems(users);
        adapter.notifyDataSetChanged();
    }

    @Override public void showNicersList() {
        nicerslistListView.setVisibility(View.VISIBLE);
    }

    @Override public void showEmpty() {
        /* no-op */
    }

    @Override public void hideEmpty() {
        /* no-op */
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

    @Override public void follow(int position) {
        UserModel user = adapter.getItem(position);
        presenter.followUser(user);
        sendAnalytics(user);
    }

    private void sendAnalytics(UserModel user) {
        AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
        builder.setContext(getBaseContext());
        builder.setActionId(analyticsActionFollow);
        builder.setLabelId(analyticsLabelFollow);
        builder.setSource(nicersSource);
        builder.setUser(sessionRepository.getCurrentUser());
        builder.setIsStrategic(user.isStrategic());
        builder.setIdTargetUser(user.getIdUser());
        builder.setTargetUsername(user.getUsername());
        analyticsTool.analyticsSendAction(builder);
        analyticsTool.appsFlyerSendAction(builder);
    }

    @Override public void unFollow(int position) {
        final UserModel userModel = adapter.getItem(position);
        new AlertDialog.Builder(this).setMessage(String.format(getString(R.string.unfollow_dialog_message),
          userModel.getUsername()))
          .setPositiveButton(getString(R.string.unfollow_dialog_yes), new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  presenter.unfollowUser(userModel);
              }
          })
          .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
          .create()
          .show();
    }

    @OnItemClick(R.id.nicerslist_list) public void openUserProfile(int position) {
        UserModel user = adapter.getItem(position);
        startActivityForResult(ProfileActivity.getIntent(this, user.getIdUser()), 666);
    }
}
