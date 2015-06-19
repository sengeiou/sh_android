package com.shootr.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.domain.interactor.user.GetUserByUsernameInteractor;
import com.shootr.android.ui.activities.EventDetailActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.adapters.ActivityTimelineAdapter;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.presenter.ActivityTimelinePresenter;
import com.shootr.android.ui.views.ActivityTimelineView;
import com.shootr.android.ui.views.nullview.NullActivityTimelineView;
import com.shootr.android.ui.widgets.ListViewScrollObserver;
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

    @InjectView(R.id.timeline_activity_list) ListView listView;
    @InjectView(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.timeline_empty) View emptyView;
    @InjectView(R.id.timeline_empty_title) TextView emptyViewTitle;

    @Inject
    GetUserByUsernameInteractor getUserByUsernameInteractor;
    @Inject
    UserModelMapper userModelMapper;

    @Deprecated private ActivityTimelineAdapter adapter;
    private View.OnClickListener avatarClickListener;
    private View.OnClickListener imageClickListener;
    private TimelineAdapter.VideoClickListener videoClickListener;
    private UsernameClickListener usernameClickListener;

    private View footerProgress;
    //endregion

    public static ActivityTimelineFragment newInstance() {
        return new ActivityTimelineFragment();
    }

    //region Lifecycle methods
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.timeline_activity, container, false);
        ButterKnife.inject(this, fragmentView);
        return fragmentView;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
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
        avatarClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = ((ActivityTimelineAdapter.ViewHolder) v.getTag()).position;
                openProfile(position);
            }
        };

        usernameClickListener =  new UsernameClickListener() {
            @Override
            public void onClick(String username) {
                goToUserProfile(username);
            }
        };

        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_loading, listView, false);
        footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);

        listView.addFooterView(footerView, null, false);

        adapter = new ActivityTimelineAdapter(getActivity(), picasso, avatarClickListener, usernameClickListener,
          timeUtils);
        listView.setAdapter(adapter);
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
        new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(new ListViewScrollObserver.OnListViewScrollListener() {
            @Override public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                if (delta < -10) {
                    // going down
                } else if (delta > 10) {
                    // going up
                }
            }

            @Override public void onScrollIdle() {
                checkIfEndOfListVisible();
            }
        });
    }

    private void checkIfEndOfListVisible() {
        int lastItemPosition = listView.getAdapter().getCount() - 1;
        int lastVisiblePosition = listView.getLastVisiblePosition();
        if (lastItemPosition == lastVisiblePosition) {
            timelinePresenter.showingLastActivity(adapter.getLastActivity());
        }
    }
    //endregion

    public void openProfile(int position) {
        ActivityModel activityVO = adapter.getItem(position);
        Intent profileIntent = ProfileContainerActivity.getIntent(getActivity(), activityVO.getIdUser());
        startActivity(profileIntent);
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
        listView.setVisibility(View.GONE);
    }

    @Override public void showActivities() {
        listView.setVisibility(View.VISIBLE);
    }

    @Override public void addNewActivities(List<ActivityModel> newActivities) {
        adapter.addActivitiesAbove(newActivities);
    }

    @Override public void addOldActivities(List<ActivityModel> oldActivities) {
        adapter.addActivitiesBelow(oldActivities);
    }

    @Override public void showLoadingOldActivities() {
        footerProgress.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoadingOldActivities() {
        footerProgress.setVisibility(View.GONE);
    }
    //endregion
}
