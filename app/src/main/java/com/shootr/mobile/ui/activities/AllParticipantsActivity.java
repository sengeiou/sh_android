package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.ParticipantAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.AllParticipantsPresenter;
import com.shootr.mobile.ui.views.AllParticipantsView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;
import javax.inject.Inject;

public class AllParticipantsActivity extends BaseToolbarDecoratedActivity
    implements AllParticipantsView {

  private static final String EXTRA_STREAM = "stream";

  private ParticipantAdapter adapter;
  private Boolean isFooterLoading = false;

  View progressViewContent;
  View progressView;

  @BindView(R.id.userlist_list) RecyclerView userlistListView;
  @BindView(R.id.userlist_progress) ProgressBar progressBar;
  @BindView(R.id.userlist_empty) TextView emptyTextView;
  @BindString(R.string.analytics_screen_all_participants) String analyticsScreenAllParticipants;
  @BindString(R.string.analytics_action_follow) String analyticsActionFollow;
  @BindString(R.string.analytics_label_follow) String analyticsLabelFollow;
  @BindString(R.string.analytics_source_all_participants) String allParticipantsSource;

  @Inject ImageLoader imageLoader;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AllParticipantsPresenter allParticipantsPresenter;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;
  @Inject InitialsLoader initialsLoader;

  private LinearLayoutManager layoutManager;

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
    layoutManager = new LinearLayoutManager(this);
    userlistListView.setLayoutManager(layoutManager);

    userlistListView.setAdapter(getParticipantsAdapter());
    progressView = getLoadingView();
    progressViewContent = ButterKnife.findById(progressView, R.id.loading_progress);
    setupListScrollListeners();
  }

  private void setupListScrollListeners() {
    userlistListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (userlistListView != null) {

          checkIfEndOfListVisible();
        }
      }
    });
  }

  private void checkIfEndOfListVisible() {
    int lastItemPosition = userlistListView.getAdapter().getItemCount() - 1;
    int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
    if (lastItemPosition == lastVisiblePosition && lastItemPosition >= 0) {
      allParticipantsPresenter.makeNextRemoteSearch(
          adapter.getItems().get(adapter.getItems().size() - 1));
    }
  }

  private View getLoadingView() {
    return LayoutInflater.from(this).inflate(R.layout.item_list_loading, userlistListView, false);
  }

  @Override protected void initializePresenter() {
    String idStream = getIntent().getStringExtra(EXTRA_STREAM);
    allParticipantsPresenter.initialize(this, idStream);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } else if (item.getItemId() == R.id.menu_search) {
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

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.all_participants, menu);
    return super.onCreateOptionsMenu(menu);
  }

  private ParticipantAdapter getParticipantsAdapter() {
    if (adapter == null) {
      adapter = new ParticipantAdapter(imageLoader, initialsLoader, new OnFollowUnfollowListener() {
        @Override public void onFollow(UserModel user) {
          follow(user);
        }

        @Override public void onUnfollow(UserModel user) {
          unFollow(user);
        }
      }, new OnUserClickListener() {
        @Override public void onUserClick(String idUser) {
          openUserProfile(idUser);
        }
      });
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
    adapter.setUsers(users);
    adapter.notifyDataSetChanged();
  }

  @Override public void showAllParticipantsList() {
    userlistListView.setVisibility(View.VISIBLE);
  }

  @Override public void goToSearchParticipants() {
    String idStream = getIntent().getStringExtra(EXTRA_STREAM);
    startActivity(FindParticipantsActivity.newIntent(this, idStream));
    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
  }

  @Override public void renderParticipantsBelow(List<UserModel> userModels) {
    adapter.addItems(userModels);
    adapter.notifyDataSetChanged();
  }

  @Override public void hideProgressView() {
    /* no-op */
  }

  @Override public void showProgressView() {
    /* no-op */
  }

  private void follow(UserModel user) {
    allParticipantsPresenter.followUser(user);
    sendAnalytics(user);
  }

  private void sendAnalytics(UserModel user) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionFollow);
    builder.setLabelId(analyticsLabelFollow);
    builder.setSource(allParticipantsSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(user.getIdUser());
    builder.setTargetUsername(user.getUsername());
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  private void unFollow(final UserModel userModel) {
    new AlertDialog.Builder(this).setMessage(
        String.format(getString(R.string.unfollow_dialog_message), userModel.getUsername()))
        .setPositiveButton(getString(R.string.unfollow_dialog_yes),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                allParticipantsPresenter.unfollowUser(userModel);
              }
            })
        .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
        .create()
        .show();
  }

  private void openUserProfile(String idUser) {
    startActivityForResult(ProfileActivity.getIntent(this, idUser), 666);
  }

  @Override public void onStart() {
    super.onStart();
    analyticsTool.analyticsStart(getBaseContext(), analyticsScreenAllParticipants);
  }
}
