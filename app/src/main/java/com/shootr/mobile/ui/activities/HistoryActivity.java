package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.ActivityTimelineAdapter;
import com.shootr.mobile.ui.adapters.listeners.ActivityFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.ActivityFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollQuestionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.presenter.GenericActivityTimelinePresenter;
import com.shootr.mobile.ui.views.MeActivityTimelineView;
import com.shootr.mobile.ui.widgets.DividerItemDecoration;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import java.util.List;
import javax.inject.Inject;

public class HistoryActivity extends BaseToolbarDecoratedActivity implements
    MeActivityTimelineView {

  @Inject GenericActivityTimelinePresenter timelinePresenter;

  @Inject ImageLoader imageLoader;
  @Inject AndroidTimeUtils timeUtils;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;

  @BindView(R.id.timeline_me_activity_list) RecyclerView activityList;
  @BindView(R.id.timeline_me_activity_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
  @BindView(R.id.me_activity_timeline_empty) View emptyView;
  @BindView(R.id.me_activity_timeline_loading_activity) TextView loadingActivityView;
  @BindString(R.string.analytics_action_follow) String analyticsActionFollow;
  @BindString(R.string.analytics_source_activity) String activitySource;
  @BindString(R.string.analytics_action_favorite_stream) String analyticsActionFavoriteStream;
  @BindString(R.string.analytics_label_favorite_stream) String analyticsLabelFavoriteStream;

  private ActivityTimelineAdapter adapter;
  private LinearLayoutManager layoutManager;
  private Unbinder unbinder;
  @Inject FeedbackMessage feedbackMessage;

  public static HistoryActivity newInstance() {
    return new HistoryActivity();
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_history;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    setupListAdapter();
    setupSwipeRefreshLayout();
    setupListScrollListeners();
    setupActionBar();
  }

  private void setupActionBar() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowHomeEnabled(false);
    }
  }

  @Override protected void initializePresenter() {
    timelinePresenter.initialize(this, true, false);
  }

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    /* no-op */
  }

  private void setupSwipeRefreshLayout() {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        timelinePresenter.refresh();
      }
    });
    swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1, R.color.refresh_2,
        R.color.refresh_3, R.color.refresh_4);
  }

  private void setupListScrollListeners() {
    activityList.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        //TODO: fix that ñapa. It can't be good enough to check this shit
        if (newState == RecyclerView.SCROLL_STATE_IDLE && activityList != null) {
          checkIfEndOfListVisible();
        }
      }
    });
  }

  private void checkIfEndOfListVisible() {
    int lastItemPosition = activityList.getAdapter().getItemCount() - 1;
    int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
    if (lastItemPosition == lastVisiblePosition && lastItemPosition >= 1) {
      timelinePresenter.showingLastActivity(adapter.getLastActivity());
    }
  }

  private void setupListAdapter() {
    layoutManager = new LinearLayoutManager(this);
    activityList.setLayoutManager(layoutManager);
    activityList.addItemDecoration(new DividerItemDecoration(this, 68,
        getResources().getDrawable(R.drawable.line_divider), false, false));

    adapter = new ActivityTimelineAdapter(imageLoader, timeUtils, //
        new OnAvatarClickListener() {
          @Override public void onAvatarClick(String userId, View avatarView) {
            openProfile(userId);
          }
        }, //
        new OnUsernameClickListener() {
          @Override public void onUsernameClick(String username) {
            openProfileFromUsername(username);
          }
        }, //
        new OnStreamTitleClickListener() {
          @Override
          public void onStreamTitleClick(String streamId, String streamTitle, String authorId) {
            openStream(streamId, streamTitle, authorId);
          }
        }, //
        new OnShotClick() {
          @Override public void onShotClick(ShotModel shot) {
            openShotDetail(shot);
          }
        }, new OnPollQuestionClickListener() {
      @Override public void onPollQuestionClick(String idPoll, String streamTitle) {
        openPollVote(idPoll, streamTitle);
      }
    }, new ActivityFollowUnfollowListener() {
      @Override public void onFollow(String idUser, String username, Boolean isStrategic) {
        timelinePresenter.followUser(idUser);
        sendFollowAnalytics(idUser, username, isStrategic);
      }

      @Override public void onUnfollow(String idUser, String username) {
        setupUnFollowDialog(idUser, username);
      }
    }, new ActivityFavoriteClickListener() {
      @Override
      public void onFavoriteClick(String idStream, String streamTitle, boolean isStrategic) {
        sendFavoriteAnalytics(idStream, streamTitle, isStrategic);
      }

      @Override public void onRemoveFavoriteClick(String idStream) {
        /* no-op */
      }
    });
    activityList.setAdapter(adapter);
  }

  private void setupUnFollowDialog(final String idUser, String username) {
    new AlertDialog.Builder(this).setMessage(
        String.format(getString(R.string.unfollow_dialog_message), username))
        .setPositiveButton(getString(R.string.unfollow_dialog_yes),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                /* no-op */
              }
            })
        .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
        .create()
        .show();
  }

  private void sendFollowAnalytics(String idTargetUser, String targetUsername,
      Boolean isStrategic) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(this);
    builder.setActionId(analyticsActionFollow);
    builder.setSource(activitySource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(idTargetUser);
    builder.setTargetUsername(targetUsername);
    builder.setIsStrategic(isStrategic);
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  private void sendFavoriteAnalytics(String idStream, String streamTitle, boolean isStrategic) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(this);
    builder.setActionId(analyticsActionFavoriteStream);
    builder.setLabelId(analyticsLabelFavoriteStream);
    builder.setSource(activitySource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setStreamName(streamTitle);
    builder.setIdStream(idStream);
    builder.setIsStrategic(isStrategic);
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  private void openPollVote(String idPoll, String streamTitle) {
    Intent pollVoteIntent =
        PollVoteActivity.newIntentWithIdPoll(this, idPoll, streamTitle);
    startActivity(pollVoteIntent);
  }

  protected void openProfile(String idUser) {
    Intent profileIntent = ProfileActivity.getIntent(this, idUser);
    startActivity(profileIntent);
  }

  protected void openStream(String idStream, String streamTitle, String authorId) {
    Intent streamIntent =
        StreamTimelineActivity.newIntent(this, idStream, streamTitle, authorId);
    startActivity(streamIntent);
  }

  private void openProfileFromUsername(String username) {
    Intent intentForUser = ProfileActivity.getIntentWithUsername(this, username);
    startActivity(intentForUser);
  }

  private void openShotDetail(ShotModel shot) {
    Intent shotIntent = ShotDetailActivity.getIntentForActivity(this, shot);
    startActivity(shotIntent);
  }

  //region View methods
  @Override public void showEmpty() {
    emptyView.setVisibility(View.VISIBLE);
  }

  @Override public void hideEmpty() {
    emptyView.setVisibility(View.GONE);
  }

  @Override public void showLoading() {
    swipeRefreshLayout.setRefreshing(true);
  }

  @Override public void hideLoading() {
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override public void showError(String message) {
    feedbackMessage.show(getView(), message);
  }

  @Override public void setActivities(List<ActivityModel> activities, String currentUserId) {
    adapter.setActivities(activities, currentUserId);
  }

  @Override public void hideActivities() {
    activityList.setVisibility(View.GONE);
  }

  @Override public void showActivities() {
    activityList.setVisibility(View.VISIBLE);
  }

  @Override public void addNewActivities(List<ActivityModel> newActivities) {
    adapter.addActivitiesAbove(newActivities);
  }

  @Override public void addOldActivities(List<ActivityModel> oldActivities) {
    adapter.addActivitiesBelow(oldActivities);
  }

  @Override public void showLoadingOldActivities() {
    adapter.setFooterVisible(true);
  }

  @Override public void hideLoadingOldActivities() {
    adapter.setFooterVisible(false);
  }

  @Override public void showLoadingActivity() {
    loadingActivityView.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoadingActivity() {
    loadingActivityView.setVisibility(View.GONE);
  }

  @Override public void setNewContentArrived() {
        /* no-op */
  }

  public void scrollListToTop() {
    activityList.scrollToPosition(0);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }
}
