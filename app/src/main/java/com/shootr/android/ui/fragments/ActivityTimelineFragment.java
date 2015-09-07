package com.shootr.android.ui.fragments;

import android.content.Intent;
import android.net.Uri;
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
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.activities.PhotoViewActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.activities.ShotDetailActivity;
import com.shootr.android.ui.activities.StreamTimelineActivity;
import com.shootr.android.ui.adapters.ActivityTimelineAdapter;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnImageClickListener;
import com.shootr.android.ui.adapters.listeners.OnShotClick;
import com.shootr.android.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.ActivityTimelinePresenter;
import com.shootr.android.ui.views.ActivityTimelineView;
import com.shootr.android.ui.views.nullview.NullActivityTimelineView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.FeedbackLoader;
import com.shootr.android.util.ImageLoader;
import dagger.ObjectGraph;
import java.util.List;
import javax.inject.Inject;

public class ActivityTimelineFragment extends BaseFragment implements ActivityTimelineView {

    //region Fields
    @Inject ActivityTimelinePresenter timelinePresenter;

    @Inject ImageLoader imageLoader;
    @Inject AndroidTimeUtils timeUtils;

    @Bind(R.id.timeline_activity_list) RecyclerView activityList;
    @Bind(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.timeline_empty) View emptyView;
    @Bind(R.id.timeline_loading_activity) TextView loadingActivityView;

    private ActivityTimelineAdapter adapter;
    private LinearLayoutManager layoutManager;
    private FeedbackLoader feedbackLoader;
    //endregion

    public static ActivityTimelineFragment newInstance() {
        return new ActivityTimelineFragment();
    }

    //region Lifecycle methods
    @Override
    public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.timeline_activity, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        timelinePresenter.setView(new NullActivityTimelineView());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
              public void onStreamTitleClick(String streamId, String streamTitle) {
                  openStream(streamId, streamTitle);
              }
          }, //
          new OnImageClickListener() {
              @Override
              public void onImageClick(String url) {
                  openImage(url);
              }
          }, //
          new OnVideoClickListener() {
              @Override
              public void onVideoClick(String url) {
                  openVideo(url);
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
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1,
          R.color.refresh_2,
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
        if (lastItemPosition == lastVisiblePosition) {
            timelinePresenter.showingLastActivity(adapter.getLastActivity());
        }
    }
    //endregion

    protected void openProfile(String idUser) {
        Intent profileIntent = ProfileContainerActivity.getIntent(getActivity(), idUser);
        startActivity(profileIntent);
    }

    protected void openStream(String idStream, String streamTitle) {
        Intent streamIntent = StreamTimelineActivity.newIntent(getActivity(), idStream, streamTitle);
        startActivity(streamIntent);
    }

    private void openVideo(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void openImage(String url) {
        Intent imageIntent = PhotoViewActivity.getIntentForActivity(getActivity(), url);
        startActivity(imageIntent);
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
        feedbackLoader.showShortFeedback(getActivity(), message);
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
