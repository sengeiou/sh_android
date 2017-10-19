package com.shootr.mobile.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.activities.PollVoteActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.adapters.ActivityTimelineAdapter;
import com.shootr.mobile.ui.adapters.listeners.ActivityFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.ActivityFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollQuestionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.presenter.GenericActivityTimelinePresenter;
import com.shootr.mobile.ui.views.ActivityTimelineView;
import com.shootr.mobile.ui.views.nullview.NullActivityTimelineView;
import com.shootr.mobile.ui.widgets.DividerItemDecoration;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import dagger.ObjectGraph;
import java.util.List;
import javax.inject.Inject;

public class ActivityTimelineFragment extends BaseFragment implements ActivityTimelineView {

  //region Fields
  @Inject GenericActivityTimelinePresenter timelinePresenter;

  @Inject ImageLoader imageLoader;
  @Inject AndroidTimeUtils timeUtils;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;

  @BindView(R.id.timeline_activity_list) RecyclerView activityList;
  @BindView(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
  @BindView(R.id.timeline_empty) View emptyView;

  @BindView(R.id.timeline_loading_activity) TextView loadingActivityView;

  @BindString(R.string.analytics_screen_activity) String analyticsScreenActivity;
  @BindString(R.string.analytics_action_follow) String analyticsActionFollow;
  @BindString(R.string.analytics_action_home) String analyticsActionHome;
  @BindString(R.string.analytics_source_activity) String activitySource;
  @BindString(R.string.analytics_action_favorite_stream) String analyticsActionFavoriteStream;
  @BindString(R.string.analytics_label_favorite_stream) String analyticsLabelFavoriteStream;

  private ActivityTimelineAdapter adapter;
  private LinearLayoutManager layoutManager;
  private Unbinder unbinder;
  private boolean hasNewContent = false;
  //endregion

  public static ActivityTimelineFragment newInstance() {
    return new ActivityTimelineFragment();
  }

  //region Lifecycle methods
  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View fragmentView = inflater.inflate(R.layout.timeline_activity, container, false);
    unbinder = ButterKnife.bind(this, fragmentView);
    return fragmentView;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    timelinePresenter.setView(new NullActivityTimelineView());
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initializeViews();
    initializePresenter();
  }

  @Override public void onResume() {
    super.onResume();
    timelinePresenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    timelinePresenter.pause();
    sendHomeAnalythics();
  }

  private void initializePresenter() {
    timelinePresenter.initialize(this, false);
  }
  //endregion

  @Override protected ObjectGraph getObjectGraph() {
    return super.getObjectGraph();
  }

  //region Views manipulation
  private void initializeViews() {
    setupListAdapter();
    setupSwipeRefreshLayout();
    setupListScrollListeners();
  }

  private void setupListAdapter() {
    layoutManager = new LinearLayoutManager(getActivity());
    activityList.setLayoutManager(layoutManager);
    activityList.addItemDecoration(new DividerItemDecoration(getContext(), 65,
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
        timelinePresenter.addFavorite(idStream);
        sendFavoriteAnalytics(idStream, streamTitle, isStrategic);
      }

      @Override public void onRemoveFavoriteClick(String idStream) {
        timelinePresenter.removeFavorite(idStream);
      }
    });
    activityList.setAdapter(adapter);
  }

  private void setupUnFollowDialog(final String idUser, String username) {
    new AlertDialog.Builder(getContext()).setMessage(
        String.format(getString(R.string.unfollow_dialog_message), username))
        .setPositiveButton(getString(R.string.unfollow_dialog_yes),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                timelinePresenter.unFollowUser(idUser);
              }
            })
        .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
        .create()
        .show();
  }

  private void sendHomeAnalythics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionHome);
    builder.setSource(activitySource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setNewContent(hasNewContent);
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  private void sendFollowAnalytics(String idTargetUser, String targetUsername,
      Boolean isStrategic) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
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
    builder.setContext(getContext());
    builder.setActionId(analyticsActionFavoriteStream);
    builder.setLabelId(analyticsLabelFavoriteStream);
    builder.setSource(activitySource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setStreamName(streamTitle);
    builder.setIsStrategic(isStrategic);
    builder.setIdStream(idStream);
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
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
  //endregion

  protected void openProfile(String idUser) {
    Intent profileIntent = ProfileActivity.getIntent(getActivity(), idUser);
    startActivity(profileIntent);
  }

  protected void openStream(String idStream, String streamTitle, String authorId) {
    Intent streamIntent =
        StreamTimelineActivity.newIntent(getActivity(), idStream, streamTitle, authorId);
    startActivity(streamIntent);
  }

  private void openProfileFromUsername(String username) {
    Intent intentForUser = ProfileActivity.getIntentWithUsername(getActivity(), username);
    startActivity(intentForUser);
  }

  private void openShotDetail(ShotModel shot) {
    Intent shotIntent = ShotDetailActivity.getIntentForActivity(getActivity(), shot);
    startActivity(shotIntent);
  }

  private void openPollVote(String idPoll, String streamTitle) {
    Intent pollVoteIntent =
        PollVoteActivity.newIntentWithIdPoll(getActivity(), idPoll, streamTitle);
    startActivity(pollVoteIntent);
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
    this.hasNewContent = true;
  }

  public void scrollListToTop() {
    activityList.scrollToPosition(0);
  }

  //endregion
  @Override public void onStart() {
    super.onStart();
    analyticsTool.analyticsStart(getContext(), analyticsScreenActivity);
  }
}
