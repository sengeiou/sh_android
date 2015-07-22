package com.shootr.android.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.StreamTimelineActivity;
import com.shootr.android.ui.activities.FindStreamsActivity;
import com.shootr.android.ui.activities.NewStreamActivity;
import com.shootr.android.ui.adapters.EventsListAdapter;
import com.shootr.android.ui.adapters.listeners.OnEventClickListener;
import com.shootr.android.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.android.ui.adapters.recyclerview.FadeDelayedItemAnimator;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.presenter.StreamsListPresenter;
import com.shootr.android.ui.views.StreamsListView;
import com.shootr.android.ui.views.nullview.NullEventListView;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class StreamsListFragment extends BaseFragment implements StreamsListView {

    public static final int REQUEST_NEW_EVENT = 1;

    @Bind(R.id.events_list) RecyclerView eventsList;
    @Bind(R.id.events_list_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.events_empty) View emptyView;
    @Bind(R.id.events_loading) View loadingView;

    @Inject StreamsListPresenter presenter;
    @Inject PicassoWrapper picasso;
    @Inject ToolbarDecorator toolbarDecorator;

    private EventsListAdapter adapter;

    public static StreamsListFragment newInstance() {
        return new StreamsListFragment();
    }

    //region Lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_events_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        initializePresenter();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        presenter.setView(new NullEventListView());
    }
    //endregion

    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this, getView());
        eventsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventsList.setItemAnimator(new FadeDelayedItemAnimator(50));

        adapter = new EventsListAdapter(picasso, new OnEventClickListener() {
            @Override
            public void onEventClick(StreamResultModel event) {
                presenter.selectStream(event);
            }
        });
        adapter.setOnUnwatchClickListener(new OnUnwatchClickListener() {
            @Override public void onUnwatchClick() {
                presenter.unwatchStream();
            }
        });
        eventsList.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1,
          R.color.refresh_2,
          R.color.refresh_3,
          R.color.refresh_4);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                presenter.refresh();
            }
        });
    }

    protected void initializePresenter() {
        presenter.initialize(this);
    }

    private void navigateToFindEvents() {
        Intent intent = new Intent(getActivity(), FindStreamsActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @OnClick(R.id.events_add_event) public void onAddEvent() {
        startActivityForResult(new Intent(getActivity(), NewStreamActivity.class), REQUEST_NEW_EVENT);
    }

    //region Activity methods
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.events_list, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                navigateToFindEvents();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        redrawEventListWithCurrentValues();
        presenter.resume();
    }

    private void redrawEventListWithCurrentValues() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NEW_EVENT && resultCode == Activity.RESULT_OK) {
            String eventId = data.getStringExtra(NewStreamActivity.KEY_STREAM_ID);
            String title = data.getStringExtra(NewStreamActivity.KEY_STREAM_TITLE);
            presenter.streamCreated(eventId, title);
        }
    }

    //region View methods
    @Override public void renderStream(List<StreamResultModel> streams) {
        adapter.setEvents(streams);
    }

    @Override public void setCurrentWatchingStreamId(StreamResultModel streamId) {
        adapter.setCurrentWatchingEvent(streamId);
    }

    @Override public void showContent() {
        eventsList.setVisibility(View.VISIBLE);
    }

    @Override public void navigateToStreamTimeline(String idStream, String title) {
        startActivity(StreamTimelineActivity.newIntent(getActivity(), idStream, title));
    }

    @Override public void showNotificationsOff() {
        Toast.makeText(getActivity(),getResources().getString(R.string.notifications_off_alert), Toast.LENGTH_SHORT).show();
    }

    @Override public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    //endregion
}
