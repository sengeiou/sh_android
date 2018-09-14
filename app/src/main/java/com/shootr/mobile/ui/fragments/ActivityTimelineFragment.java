package com.shootr.mobile.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.eftimoff.androidplayer.Player;
import com.eftimoff.androidplayer.actions.property.PropertyAction;
import com.shootr.mobile.R;
import com.shootr.mobile.data.prefs.ActivityShowcase;
import com.shootr.mobile.data.prefs.BooleanPreference;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.activities.HistoryActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.PollVoteActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.activities.SearchActivity;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.adapters.ActivityTimelineAdapter;
import com.shootr.mobile.ui.adapters.listeners.ActivityFavoriteClickListener;
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
  @Inject @ActivityShowcase BooleanPreference activityShowcasePreference;

  @BindView(R.id.timeline_activity_list) RecyclerView activityList;
  @BindView(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
  @BindView(R.id.timeline_empty) View emptyView;

  @BindView(R.id.timeline_loading_activity) TextView loadingActivityView;
  @BindView(R.id.onboarding_container) RelativeLayout onBoardingContainer;
  @BindView(R.id.container) FrameLayout container;
  @BindView(R.id.onboarding_text) TextView onboardingText;
  @BindView(R.id.onboarding_action) Button onboardingButton;

  @BindString(R.string.analytics_screen_activity) String analyticsScreenActivity;
  @BindString(R.string.analytics_action_follow) String analyticsActionFollow;
  @BindString(R.string.analytics_action_home) String analyticsActionHome;
  @BindString(R.string.analytics_action_historic) String analyticsActionHistoric;
  @BindString(R.string.analytics_source_activity) String activitySource;
  @BindString(R.string.analytics_action_favorite_stream) String analyticsActionFavoriteStream;
  @BindString(R.string.analytics_label_favorite_stream) String analyticsLabelFavoriteStream;
  @BindString(R.string.activity_onboarding_text) String activityOnboardingText;

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
    setHasOptionsMenu(true);
    return fragmentView;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    timelinePresenter.setView(new NullActivityTimelineView());
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (activityShowcasePreference != null && activityShowcasePreference.get()) {
      setupOnboarding();
      activityShowcasePreference.set(false);
    }
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
    timelinePresenter.initialize(this, false, true);
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
    activityList.addItemDecoration(new DividerItemDecoration(getContext(), 68,
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
          public void onStreamTitleClick(String streamId, String streamTitle) {
            openStream(streamId, streamTitle);
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
    }, new ActivityFavoriteClickListener() {
      @Override
      public void onFavoriteClick(String idStream, String streamTitle, boolean isStrategic) {
        sendFavoriteAnalytics(idStream, streamTitle, isStrategic);
        timelinePresenter.followStream(idStream);
      }

      @Override public void onRemoveFavoriteClick(String idStream) {
        timelinePresenter.unFollowStream(idStream);
      }
    });
    activityList.setAdapter(adapter);
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

  private void sendHistoricAnalythics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionHistoric);
    builder.setSource(activitySource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setNewContent(hasNewContent);
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

  protected void openStream(String idStream, String streamTitle) {
    Intent streamIntent =
        StreamTimelineActivity.newIntent(getActivity(), idStream, streamTitle);
    startActivity(streamIntent);
  }

  private void openProfileFromUsername(String username) {
    Intent intentForUser = ProfileActivity.getIntentWithUsername(getActivity(), username);
    startActivity(intentForUser);
  }

  private void openShotDetail(ShotModel shot) {
    Intent shotIntent = ShotDetailActivity.getIntentForActivity(getActivity(), shot.getIdShot());
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

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.activity_menu, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_history:
        navigateToHistory();
        return true;
      case R.id.menu_search:
        navigateToSearch();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void navigateToSearch() {
    Intent intent = new Intent(getContext(), SearchActivity.class);
    startActivity(intent);
    getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
  }

  private void navigateToHistory() {
    sendHistoricAnalythics();
    Intent intent = new Intent(getContext(), HistoryActivity.class);
    startActivity(intent);
  }

  private void setupOnboarding() {
    Spanned text;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      text = Html.fromHtml(
          activityOnboardingText,
          Html.FROM_HTML_MODE_LEGACY);
    } else {
      text = Html.fromHtml(
          activityOnboardingText);
    }

    onboardingText.setText(text);
    onBoardingContainer.setVisibility(View.VISIBLE);
    final PropertyAction onboardingTextAnimation = PropertyAction.newPropertyAction(onboardingText).
        interpolator(new AccelerateDecelerateInterpolator()).
        translationY(-200).
        duration(500).
        alpha(0f).
        build();
    Player.init().animate(onboardingTextAnimation).play();
  }

  @OnClick(R.id.onboarding_action) void onBoardingActionClick() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      int x = onBoardingContainer.getRight();
      int y = onBoardingContainer.getBottom();

      int startRadius =
          (int) Math.hypot(onBoardingContainer.getWidth(), onBoardingContainer.getHeight());
      int endRadius = 0;

      Animator anim =
          ViewAnimationUtils.createCircularReveal(onBoardingContainer, x, y, startRadius,
              endRadius);
      anim.setDuration(400);
      anim.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) {
          super.onAnimationEnd(animation);
          if (onBoardingContainer != null) {
            onBoardingContainer.setVisibility(View.GONE);
          }
        }
      });

      anim.start();
    } else {
      onBoardingContainer.setVisibility(View.GONE);
    }
  }
}
