package com.shootr.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.shootr.android.R;
import com.shootr.android.ui.activities.EventDetailActivity;
import com.shootr.android.ui.activities.EventTimelineActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.adapters.ActivityTimelineAdapter;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnEventTitleClickListener;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.presenter.ActivityTimelinePresenter;
import com.shootr.android.ui.views.ActivityTimelineView;
import com.shootr.android.ui.views.nullview.NullActivityTimelineView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.UsernameClickListener;
import dagger.ObjectGraph;
import java.util.List;
import javax.inject.Inject;

public class ActivityTimelineFragment extends BaseFragment implements ActivityTimelineView {

    //region Fields
    @Inject ActivityTimelinePresenter timelinePresenter;

    @Inject PicassoWrapper picasso;
    @Inject AndroidTimeUtils timeUtils;

    @Bind(R.id.timeline_activity_list) RecyclerView activityList;
    @Bind(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.timeline_empty) View emptyView;
    @Bind(R.id.timeline_empty_title) TextView emptyViewTitle;

    private ActivityTimelineAdapter adapter;
    private LinearLayoutManager layoutManager;
    //endregion

    public static ActivityTimelineFragment newInstance() {
        return new ActivityTimelineFragment();
    }

    //region Lifecycle methods
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.timeline_activity, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        timelinePresenter.setView(new NullActivityTimelineView());
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializePresenter();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                startActivity(new Intent(getActivity(), EventDetailActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onResume() {
        super.onResume();
        timelinePresenter.resume();
    }

    @Override public void onPause() {
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

        adapter = new ActivityTimelineAdapter(picasso, timeUtils, new OnAvatarClickListener() {
            @Override
            public void onClick(String userId, View avatarView) {
                openProfile(userId);
            }
        }, new UsernameClickListener() {
            @Override
            public void onClick(String username) {
                goToUserProfile(username);
            }
        }, new OnEventTitleClickListener() {
            @Override
            public void onClick(String eventId, String eventTitle) {
                openEvent(eventId, eventTitle);
            }
        });

        activityList.setAdapter(adapter);
    }

    private void startProfileContainerActivity(String username) {
        Intent intentForUser = ProfileContainerActivity.getIntentWithUsername(getActivity(), username);
        startActivity(intentForUser);
    }

    private void goToUserProfile(String username) {
        startProfileContainerActivity(username);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
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
        int lastItemPosition = activityList.getAdapter().getItemCount()-1;
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

    protected void openEvent(String idEvent, String eventTitle) {
        Intent eventIntent = EventTimelineActivity.newIntent(getActivity(), idEvent, eventTitle);
        startActivity(eventIntent);
    }

    //region View methods

    @Override public void showEmpty() {
        emptyViewTitle.setText(R.string.activity_empty);
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
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override public void setActivities(List<ActivityModel> activities) {
        adapter.setActivities(activities);
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
    //endregion
}
