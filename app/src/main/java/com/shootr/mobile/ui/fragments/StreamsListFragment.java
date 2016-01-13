package com.shootr.mobile.ui.fragments;

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
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.FindStreamsActivity;
import com.shootr.mobile.ui.activities.NewStreamActivity;
import com.shootr.mobile.ui.activities.StreamDetailActivity;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.adapters.StreamsListAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.mobile.ui.adapters.recyclerview.FadeDelayedItemAnimator;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.presenter.StreamsListPresenter;
import com.shootr.mobile.ui.views.StreamsListView;
import com.shootr.mobile.ui.views.nullview.NullStreamListView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import java.util.List;
import javax.inject.Inject;

public class StreamsListFragment extends BaseFragment implements StreamsListView {

    public static final int REQUEST_NEW_STREAM = 3;

    @Bind(R.id.streams_list) RecyclerView streamsList;
    @Bind(R.id.streams_list_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.streams_empty) View emptyView;
    @Bind(R.id.streams_loading) View loadingView;
    @BindString(R.string.added_to_favorites) String addedToFavorites;
    @BindString(R.string.shared_stream_notification) String sharedStream;
    @BindString(R.string.analytics_screen_stream_list) String analyticsScreenStreamList;

    @Inject StreamsListPresenter presenter;
    @Inject ImageLoader imageLoader;
    @Inject IntentFactory intentFactory;
    @Inject FeedbackMessage feedbackMessage;
    @Inject AnalyticsTool analyticsTool;

    private StreamsListAdapter adapter;

    public static StreamsListFragment newInstance() {
        return new StreamsListFragment();
    }

    //region Lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_streams_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        initializePresenter();
        initializeViews(savedInstanceState);
        analyticsTool.analyticsStart(getContext(), analyticsScreenStreamList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        analyticsTool.analyticsStop(getContext(), getActivity());
        ButterKnife.unbind(this);
        presenter.setView(new NullStreamListView());
    }
    //endregion

    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this, getView());
        streamsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        streamsList.setItemAnimator(new FadeDelayedItemAnimator(50));

        adapter = new StreamsListAdapter(imageLoader, new OnStreamClickListener() {
            @Override
            public void onStreamClick(StreamResultModel stream) {
                presenter.selectStream(stream);
            }

            @Override
            public boolean onStreamLongClick(StreamResultModel stream) {
                presenter.onStreamLongClicked(stream);
                return true;
            }
        });
        adapter.setOnUnwatchClickListener(new OnUnwatchClickListener() {
            @Override public void onUnwatchClick() {
                presenter.unwatchStream();
            }
        });
        streamsList.setAdapter(adapter);

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

    private void navigateToFindstreams() {
        Intent intent = new Intent(getActivity(), FindStreamsActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @OnClick(R.id.streams_add_stream) public void onAddStream() {
        startActivityForResult(new Intent(getActivity(), NewStreamActivity.class), REQUEST_NEW_STREAM);
    }

    //region Activity methods
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.streams_list, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                navigateToFindstreams();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
        redrawStreamListWithCurrentValues();
    }

    private void redrawStreamListWithCurrentValues() {
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
        if (requestCode == REQUEST_NEW_STREAM && resultCode == Activity.RESULT_OK) {
            String streamId = data.getStringExtra(NewStreamActivity.KEY_STREAM_ID);
            presenter.streamCreated(streamId);
        }
    }

    private CustomContextMenu.Builder baseContextualMenu(final StreamResultModel stream) {
        return new CustomContextMenu.Builder(getActivity())
          .addAction(R.string.add_to_favorites_menu_title, new Runnable() {
            @Override
            public void run() {
                presenter.addToFavorites(stream);
            }
        })
          .addAction(R.string.share_via_shootr, new Runnable() {
              @Override public void run() {
                  presenter.shareStream(stream);
              }
          })
          .addAction(R.string.share_via, new Runnable() {
              @Override public void run() {
                  shareStream(stream);
              }
          });
    }

    private void shareStream(StreamResultModel stream) {
        Intent shareIntent = intentFactory.shareStreamIntent(getActivity(), stream.getStreamModel());
        Intents.maybeStartActivity(getActivity(), shareIntent);
    }
    
    //region View methods
    @Override public void renderStream(List<StreamResultModel> streams) {
        adapter.setStreams(streams);
    }

    @Override public void setCurrentWatchingStreamId(StreamResultModel streamId) {
        adapter.setCurrentWatchingStream(streamId);
    }

    @Override public void showContent() {
        streamsList.setVisibility(View.VISIBLE);
    }

    @Override public void navigateToStreamTimeline(String idStream, String tag, String authorId) {
        startActivity(StreamTimelineActivity.newIntent(getActivity(), idStream, tag, authorId));
    }

    @Override public void navigateToCreatedStreamDetail(String streamId) {
        startActivity(StreamDetailActivity.getIntent(getActivity(), streamId));
    }

    @Override
    public void showAddedToFavorites() {
        feedbackMessage.show(getView(), addedToFavorites);
    }

    @Override public void showStreamShared() {
        feedbackMessage.show(getView(), sharedStream);
    }

    @Override public void showContextMenuWithMute(final StreamResultModel stream) {
        baseContextualMenu(stream).addAction(R.string.mute, new Runnable() {
            @Override public void run() {
                presenter.onMuteClicked(stream);
            }
        }).show();
    }

    @Override public void showContextMenuWithUnmute(final StreamResultModel stream) {
        baseContextualMenu(stream).addAction(R.string.unmute, new Runnable() {
            @Override public void run() {
                presenter.onUnmuteClicked(stream);
            }
        }).show();
    }

    @Override public void setMutedStreamIds(List<String> mutedStreamIds) {
        adapter.setMutedStreamIds(mutedStreamIds);
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
        feedbackMessage.show(getView(), message);
    }

    //endregion
}
