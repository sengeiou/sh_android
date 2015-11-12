package com.shootr.mobile.ui.fragments;

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
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.ProfileContainerActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.adapters.ActivityTimelineAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.presenter.ActivityTimelinePresenter;
import com.shootr.mobile.ui.views.ActivityTimelineView;
import com.shootr.mobile.ui.views.nullview.NullActivityTimelineView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import dagger.ObjectGraph;
import java.util.List;
import javax.inject.Inject;

public class ActivityTimelineFragment extends BaseFragment implements ActivityTimelineView {

    //region Fields
    @Inject ActivityTimelinePresenter timelinePresenter;

    @Inject ImageLoader imageLoader;
    @Inject AndroidTimeUtils timeUtils;
    @Inject AnalyticsTool analyticsTool;

    @Bind(com.shootr.mobile.R.id.timeline_activity_list) RecyclerView activityList;
    @Bind(com.shootr.mobile.R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(com.shootr.mobile.R.id.timeline_empty) View emptyView;
    @Bind(com.shootr.mobile.R.id.timeline_loading_activity) TextView loadingActivityView;

    @BindString(R.string.analytics_screen_activity) String analyticsScreenActivity;

    private ActivityTimelineAdapter adapter;
    private LinearLayoutManager layoutManager;
    @Inject FeedbackMessage feedbackMessage;
    //endregion

    public static ActivityTimelineFragment newInstance() {
        return new ActivityTimelineFragment();
    }

    //region Lifecycle methods
    @Override
    public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(com.shootr.mobile.R.layout.timeline_activity, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        analyticsTool.analyticsStop(getContext(), getActivity());
        ButterKnife.unbind(this);
        timelinePresenter.setView(new NullActivityTimelineView());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        analyticsTool.analyticsStart(getContext(), analyticsScreenActivity);
        initializeViews();
        initializePresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        timelinePresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        timelinePresenter.pause();
    }

    private void initializePresenter() {
        timelinePresenter.initialize(this);
    }
    //endregion

    @Override
    protected ObjectGraph getObjectGraph() {
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

        adapter = new ActivityTimelineAdapter(imageLoader, timeUtils, //
          new OnAvatarClickListener() {
              @Override
              public void onAvatarClick(String userId, View avatarView) {
                  openProfile(userId);
              }
          }, //
          new OnUsernameClickListener() {
              @Override
              public void onUsernameClick(String username) {
                  openProfileFromUsername(username);
              }
          }, //
          new OnStreamTitleClickListener() {
              @Override
              public void onStreamTitleClick(String streamId, String streamShortTitle, String authorId) {
                  openStream(streamId, streamShortTitle, authorId);
              }
          }, //
          new OnShotClick() {
              @Override
              public void onShotClick(ShotModel shot) {
                  openShotDetail(shot);
              }
          });
        activityList.setAdapter(adapter);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                timelinePresenter.refresh();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(com.shootr.mobile.R.color.refresh_1,
          com.shootr.mobile.R.color.refresh_2,
          R.color.refresh_3,
          R.color.refresh_4);
    }

    private void setupListScrollListeners() {
        activityList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
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
        Intent profileIntent = ProfileContainerActivity.getIntent(getActivity(), idUser);
        startActivity(profileIntent);
    }

    protected void openStream(String idStream, String streamShortTitle, String authorId) {
        Intent streamIntent = StreamTimelineActivity.newIntent(getActivity(), idStream, streamShortTitle, authorId);
        startActivity(streamIntent);
    }

    private void openProfileFromUsername(String username) {
        Intent intentForUser = ProfileContainerActivity.getIntentWithUsername(getActivity(), username);
        startActivity(intentForUser);
    }

    private void openShotDetail(ShotModel shot) {
        Intent shotIntent = ShotDetailActivity.getIntentForActivity(getActivity(), shot);
        startActivity(shotIntent);
    }

    //region View methods
    @Override
    public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showError(String message) {
        feedbackMessage.show(getView(), message);
    }

    @Override
    public void setActivities(List<ActivityModel> activities, String currentUserId) {
        adapter.setActivities(activities, currentUserId);
    }

    @Override
    public void hideActivities() {
        activityList.setVisibility(View.GONE);
    }

    @Override
    public void showActivities() {
        activityList.setVisibility(View.VISIBLE);
    }

    @Override
    public void addNewActivities(List<ActivityModel> newActivities) {
        adapter.addActivitiesAbove(newActivities);
    }

    @Override
    public void addOldActivities(List<ActivityModel> oldActivities) {
        adapter.addActivitiesBelow(oldActivities);
    }

    @Override
    public void showLoadingOldActivities() {
        adapter.setFooterVisible(true);
    }

    @Override
    public void hideLoadingOldActivities() {
        adapter.setFooterVisible(false);
    }

    @Override public void showLoadingActivity() {
        loadingActivityView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoadingActivity() {
        loadingActivityView.setVisibility(View.GONE);
    }
    //endregion
}
