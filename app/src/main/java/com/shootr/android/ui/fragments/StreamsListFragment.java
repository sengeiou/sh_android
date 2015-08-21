package com.shootr.android.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
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
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.FindStreamsActivity;
import com.shootr.android.ui.activities.NewStreamActivity;
import com.shootr.android.ui.activities.StreamDetailActivity;
import com.shootr.android.ui.activities.StreamTimelineActivity;
import com.shootr.android.ui.adapters.StreamsListAdapter;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.android.ui.adapters.recyclerview.FadeDelayedItemAnimator;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.presenter.StreamsListPresenter;
import com.shootr.android.ui.views.StreamsListView;
import com.shootr.android.ui.views.nullview.NullStreamListView;
import com.shootr.android.util.CustomContextMenu;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class StreamsListFragment extends BaseFragment implements StreamsListView {

    public static final int REQUEST_NEW_STREAM = 3;

    @Bind(R.id.streams_list) RecyclerView streamsList;
    @Bind(R.id.streams_list_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.streams_empty) View emptyView;
    @Bind(R.id.streams_loading) View loadingView;

    @Inject StreamsListPresenter presenter;
    @Inject PicassoWrapper picasso;
    @Inject ToolbarDecorator toolbarDecorator;

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
        presenter.setView(new NullStreamListView());
    }
    //endregion

    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this, getView());
        streamsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        streamsList.setItemAnimator(new FadeDelayedItemAnimator(50));

        adapter = new StreamsListAdapter(picasso, new OnStreamClickListener() {
            @Override
            public void onStreamClick(StreamResultModel stream) {
                presenter.selectStream(stream);
            }

            @Override
            public boolean onStreamLongClick(StreamResultModel stream) {
                openContextualMenu(stream);
                return true;
            }
        });
        adapter.setOnUnwatchClickListener(new OnUnwatchClickListener() {
            @Override
            public void onUnwatchClick() {
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
        redrawStreamListWithCurrentValues();
        presenter.resume();
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

    private void openContextualMenu(final StreamResultModel stream) {
        new CustomContextMenu.Builder(getActivity())
          .addAction(getString(R.string.add_to_favorites_menu_title), new Runnable() {
            @Override
            public void run() {
                presenter.addToFavorites(stream);
            }
        })
          .addAction((getActivity().getString(R.string.recomment_via_shootr)), new Runnable() {
              @Override public void run() {
                  presenter.recommendStream(stream);
              }
          })
          .addAction((getActivity().getString(R.string.recommend_via)), new Runnable() {
              @Override public void run() {
                  shareStream(stream);
              }
          }).show();
    }

    private void shareStream(StreamResultModel stream) {
        Intent intent = ShareCompat.IntentBuilder.from(getActivity())
          .setType("text/plain")
          .setText(String.format(getActivity().getString(R.string.recommend_stream_message),
            stream.getStreamModel().getTitle(),
            stream.getStreamModel().getIdStream()))
          .setChooserTitle(getString(R.string.recommend_via))
          .createChooserIntent();
        startActivity(intent);
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

    @Override public void navigateToStreamTimeline(String idStream, String title) {
        startActivity(StreamTimelineActivity.newIntent(getActivity(), idStream, title));
    }

    @Override public void showNotificationsOff() {
        Toast.makeText(getActivity(),getResources().getString(R.string.notifications_off_alert), Toast.LENGTH_SHORT).show();
    }

    @Override public void navigateToCreatedStreamDetail(String streamId) {
        startActivity(StreamDetailActivity.getIntent(getActivity(), streamId));
    }

    @Override
    public void showAddedToFavorites() {
        Toast.makeText(getActivity(), R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
    }

    @Override public void showStreamRecommended() {
        Toast.makeText(getActivity(), getActivity().getString(R.string.stream_recommended_notification), Toast.LENGTH_SHORT).show();
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
