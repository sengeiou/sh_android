package com.shootr.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.melnykov.fab.FloatingActionButton;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.TimelinePresenter;
import com.shootr.android.ui.views.TimelineView;
import com.shootr.android.ui.widgets.ListViewScrollObserver;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class TimelineFragment extends BaseFragment implements TimelineView {

    //region Fields
    @Inject TimelinePresenter timelinePresenter;
    @Inject PicassoWrapper picasso;
    @Inject AndroidTimeUtils timeUtils;

    @InjectView(R.id.timeline_list) ListView listView;
    @InjectView(R.id.timeline_new) View newShotView;
    @InjectView(R.id.timeline_new_text) TextView newShotTextView;
    @InjectView(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.exit_event_fab) FloatingActionButton exitEventFab;

    @InjectView(R.id.timeline_empty) View emptyView;
    @InjectView(R.id.timeline_drafts) View draftsButton;

    @Deprecated
    private TimelineAdapter adapter;
    private View.OnClickListener avatarClickListener;
    private View.OnClickListener imageClickListener;
    //endregion

    //region Lifecycle methods
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_timeline, container, true);
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
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        timelinePresenter.initialize(this);
    }
    //endregion

    private void initializeViews() {
        adapter = new TimelineAdapter(getActivity(), picasso, avatarClickListener, imageClickListener, timeUtils);
        listView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                timelinePresenter.refresh();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1,
          R.color.refresh_2,
          R.color.refresh_3,
          R.color.refresh_4);

        new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(
          new ListViewScrollObserver.OnListViewScrollListener() {
              @Override
              public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                  if (delta < -10) {
                      exitEventFab.hide();
                  } else if(delta > 10) {
                      exitEventFab.show();
                  }
              }

              @Override
              public void onScrollIdle() {
                  int lastItemPosition = listView.getAdapter().getCount()-1;
                  int lastVisiblePosition = listView.getLastVisiblePosition();
                  if (lastItemPosition == lastVisiblePosition) {
                      timelinePresenter.showingLastShot(adapter.getLastShot());
                  }
              }
          });
    }

    //region View methods
    @Override public void setShots(List<ShotModel> shots) {
        adapter.setShots(shots);
    }

    @Override public void addNewShots(List<ShotModel> newShots) {
        adapter.addShotsAbove(newShots);
    }

    @Override public void addOldShots(List<ShotModel> oldShots) {
        adapter.addShotsBelow(oldShots);
    }

    @Override public void showLoadingOldShots() {
        //TODO
    }

    @Override public void hideLoadingOldShots() {
        //TODO
    }

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
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
    //endregion
}
