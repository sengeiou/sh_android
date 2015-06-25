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
import butterknife.InjectView;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.EventTimelineActivity;
import com.shootr.android.ui.activities.FindEventsActivity;
import com.shootr.android.ui.activities.NewEventActivity;
import com.shootr.android.ui.adapters.EventsListAdapter;
import com.shootr.android.ui.adapters.listeners.OnEventClickListener;
import com.shootr.android.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.android.ui.adapters.recyclerview.FadeDelayedItemAnimator;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.presenter.EventsListPresenter;
import com.shootr.android.ui.views.EventsListView;
import com.shootr.android.ui.views.nullview.NullEventListView;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class EventsListFragment extends BaseFragment implements EventsListView {

    public static final int REQUEST_NEW_EVENT = 1;

    @InjectView(R.id.events_list) RecyclerView eventsList;
    @InjectView(R.id.events_list_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.events_empty) View emptyView;
    @InjectView(R.id.events_loading) View loadingView;

    @Inject EventsListPresenter presenter;
    @Inject PicassoWrapper picasso;
    @Inject ToolbarDecorator toolbarDecorator;

    private EventsListAdapter adapter;

    public static EventsListFragment newInstance() {
        return new EventsListFragment();
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
        ButterKnife.reset(this);
        presenter.setView(new NullEventListView());
    }
    //endregion

    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.inject(this, getView());
        eventsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventsList.setItemAnimator(new FadeDelayedItemAnimator(50));

        adapter = new EventsListAdapter(picasso, new OnEventClickListener() {
            @Override
            public void onEventClick(EventResultModel event) {
                presenter.selectEvent(event);
            }
        });
        adapter.setOnUnwatchClickListener(new OnUnwatchClickListener() {
            @Override public void onUnwatchClick() {
                presenter.unwatchEvent();
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
        Intent intent = new Intent(getActivity(), FindEventsActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @OnClick(R.id.events_add_event) public void onAddEvent() {
        startActivityForResult(new Intent(getActivity(), NewEventActivity.class), REQUEST_NEW_EVENT);
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
            String eventId = data.getStringExtra(NewEventActivity.KEY_EVENT_ID);
            String title = data.getStringExtra(NewEventActivity.KEY_EVENT_TITLE);
            presenter.eventCreated(eventId, title);
        }
    }

    //region View methods
    @Override public void renderEvents(List<EventResultModel> events) {
        adapter.setEvents(events);
    }

    @Override public void setCurrentCheckedInEventId(String eventId) {
        adapter.setCurrentCheckedInEvent(eventId);
    }

    @Override public void setCurrentWatchingEventId(EventResultModel event) {
        adapter.setCurrentWatchingEvent(event);
    }

    @Override public void showContent() {
        eventsList.setVisibility(View.VISIBLE);
    }

    @Override public void navigateToEventTimeline(String idEvent, String title) {
        startActivity(EventTimelineActivity.newIntent(getActivity(), idEvent, title));
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
