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
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.activities.ChannelsContainerActivity;
import com.shootr.mobile.ui.activities.DiscoverSearchActivity;
import com.shootr.mobile.ui.activities.NewStreamActivity;
import com.shootr.mobile.ui.activities.StreamDetailActivity;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.adapters.StreamsListAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.presenter.StreamsListPresenter;
import com.shootr.mobile.ui.views.StreamsListView;
import com.shootr.mobile.ui.views.nullview.NullStreamListView;
import com.shootr.mobile.ui.widgets.CustomActionItemBadge;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.ShareManager;
import java.util.List;
import javax.inject.Inject;

public class StreamsListFragment extends BaseFragment implements StreamsListView {

  public static final int REQUEST_NEW_STREAM = 3;

  @BindView(R.id.streams_list) RecyclerView streamsList;
  @BindView(R.id.streams_list_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
  @BindView(R.id.streams_empty) View emptyView;
  @BindView(R.id.streams_loading) View loadingView;
  @BindString(R.string.added_to_favorites) String addedToFavorites;
  @BindString(R.string.removed_from_favorites) String removedFromFavorites;
  @BindString(R.string.shared_stream_notification) String sharedStream;
  @BindString(R.string.analytics_screen_stream_list) String analyticsScreenStreamList;
  @BindString(R.string.analytics_action_favorite_stream) String analyticsActionFavoriteStream;
  @BindString(R.string.analytics_action_external_share_stream) String
      analyticsActionExternalShareStream;
  @BindString(R.string.analytics_label_external_share_stream) String
      analyticsLabelExternalShareStream;
  @BindString(R.string.analytics_label_favorite_stream) String analyticsLabelFavoriteStream;
  @BindString(R.string.analytics_action_inbox) String analyticsActionInbox;
  @BindString(R.string.analytics_label_inbox) String analyticsLabelInbox;
  @BindString(R.string.analytics_source_streams) String streamsSource;

  @Inject StreamsListPresenter presenter;
  @Inject ImageLoader imageLoader;
  @Inject ShareManager shareManager;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AnalyticsTool analyticsTool;
  @Inject InitialsLoader initialsLoader;
  @Inject SessionRepository sessionRepository;

  private StreamsListAdapter adapter;
  private Unbinder unbinder;
  private Menu menu;

  public static StreamsListFragment newInstance() {
    return new StreamsListFragment();
  }

  //region Lifecycle
  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.activity_streams_list, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
    initializePresenter();
    initializeViews(savedInstanceState);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    presenter.setView(new NullStreamListView());
  }
  //endregion

  protected void initializeViews(Bundle savedInstanceState) {
    unbinder = ButterKnife.bind(this, getView());
    streamsList.setLayoutManager(new LinearLayoutManager(getActivity()));

    adapter = new StreamsListAdapter(imageLoader, initialsLoader, new OnStreamClickListener() {
      @Override public void onStreamClick(StreamResultModel stream) {
        presenter.selectStream(stream);
      }

      @Override public boolean onStreamLongClick(StreamResultModel stream) {
        presenter.onStreamLongClicked(stream);
        return true;
      }
    }, new OnFavoriteClickListener() {
      @Override public void onFavoriteClick(StreamResultModel stream) {
        presenter.addToFavorites(stream, false);
        sendFavoriteAnalytics(stream);
      }

      @Override public void onRemoveFavoriteClick(StreamResultModel stream) {
        presenter.removeFromFavorites(stream, false);
      }
    }, true);
    adapter.setOnUnwatchClickListener(new OnUnwatchClickListener() {
      @Override public void onUnwatchClick() {
        presenter.unwatchStream();
      }
    });
    streamsList.setAdapter(adapter);

    swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1, R.color.refresh_2,
        R.color.refresh_3, R.color.refresh_4);
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        presenter.refresh();
      }
    });
  }

  protected void initializePresenter() {
    presenter.initialize(this);
  }

  private void navigateToDiscoverSearch() {
    Intent intent = new Intent(getActivity(), DiscoverSearchActivity.class);
    startActivity(intent);
    getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
  }

  //region Activity methods
  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.streams_list, menu);
    this.menu = menu;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_search:
        navigateToDiscoverSearch();
        return true;
      case R.id.menu_channel:
        navigateToChannelsList();
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void navigateToChannelsList() {
    sendToMixPanel();
    Intent intent = new Intent(getContext(), ChannelsContainerActivity.class);
    startActivity(intent);
  }

  private void sendToMixPanel() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionInbox);
    builder.setLabelId(analyticsLabelInbox);
    builder.setSource(streamsSource);
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
  }

  @Override public void onResume() {
    super.onResume();
    presenter.resume();
    redrawStreamListWithCurrentValues();
  }

  private void redrawStreamListWithCurrentValues() {
    adapter.notifyDataSetChanged();
  }

  @Override public void onPause() {
    super.onPause();
    presenter.pause();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_NEW_STREAM && resultCode == Activity.RESULT_OK) {
      String streamId = data.getStringExtra(NewStreamActivity.KEY_STREAM_ID);
      presenter.streamCreated(streamId);
    }
  }

  private CustomContextMenu.Builder baseContextualMenuWithFavorite(final StreamResultModel stream) {
    return new CustomContextMenu.Builder(getActivity()).addAction(
        R.string.add_to_favorites_menu_title, new Runnable() {
          @Override public void run() {
            presenter.addToFavorites(stream, true);
            sendFavoriteAnalytics(stream);
          }
        }).addAction(R.string.share_stream_via_shootr, new Runnable() {
      @Override public void run() {
        presenter.shareStream(stream);
      }
    }).addAction(R.string.share_via, new Runnable() {
      @Override public void run() {
        shareStream(stream);
        sendExternalShareAnalytics(stream);
      }
    });
  }

  private CustomContextMenu.Builder baseContextualMenuWithoutFavorite(
      final StreamResultModel stream) {
    return new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_remove_favorite,
        new Runnable() {
          @Override public void run() {
            presenter.removeFromFavorites(stream, true);
          }
        }).addAction(R.string.share_stream_via_shootr, new Runnable() {
      @Override public void run() {
        presenter.shareStream(stream);
      }
    }).addAction(R.string.share_via, new Runnable() {
      @Override public void run() {
        shareStream(stream);
        sendExternalShareAnalytics(stream);
      }
    });
  }

  private void sendExternalShareAnalytics(StreamResultModel streamResultModel) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionExternalShareStream);
    builder.setLabelId(analyticsLabelExternalShareStream);
    builder.setSource(streamsSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setStreamName(streamResultModel.getStreamModel().getTitle());
    builder.setIdStream(streamResultModel.getStreamModel().getIdStream());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendFavoriteAnalytics(StreamResultModel streamResultModel) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionFavoriteStream);
    builder.setLabelId(analyticsLabelFavoriteStream);
    builder.setSource(streamsSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setStreamName(streamResultModel.getStreamModel().getTitle());
    builder.setIdStream(streamResultModel.getStreamModel().getIdStream());
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  private void shareStream(StreamResultModel stream) {
    Intent shareIntent = shareManager.shareStreamIntent(getActivity(), stream.getStreamModel());
    Intents.maybeStartActivity(getActivity(), shareIntent);
  }

  //region View methods
  @Override public void renderStream(List<StreamResultModel> streams) {
    adapter.setStreams(streams);
  }

  @Override public void setCurrentWatchingStreamId(StreamResultModel streamId) {
    /* no-op */
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

  @Override public void showAddedToFavorites() {
    feedbackMessage.show(getView(), addedToFavorites);
  }

  @Override public void showRemovedFromFavorites() {
    feedbackMessage.show(getView(), removedFromFavorites);
  }

  @Override public void showStreamShared() {
    feedbackMessage.show(getView(), sharedStream);
  }

  @Override public void showContextMenuWithMute(final StreamResultModel stream) {
    if (stream.isFavorited()) {
      baseContextualMenuWithoutFavorite(stream).addAction(R.string.mute, new Runnable() {
        @Override public void run() {
          presenter.onMuteClicked(stream);
        }
      }).show();
    } else {
      baseContextualMenuWithFavorite(stream).addAction(R.string.mute, new Runnable() {
        @Override public void run() {
          presenter.onMuteClicked(stream);
        }
      }).show();
    }
  }

  @Override public void showContextMenuWithUnmute(final StreamResultModel stream) {
    if (stream.isFavorited()) {
      baseContextualMenuWithoutFavorite(stream).addAction(R.string.unmute, new Runnable() {
        @Override public void run() {
          presenter.onUnmuteClicked(stream);
        }
      }).show();
    } else {
      baseContextualMenuWithFavorite(stream).addAction(R.string.unmute, new Runnable() {
        @Override public void run() {
          presenter.onUnmuteClicked(stream);
        }
      }).show();
    }
  }

  @Override public void setMutedStreamIds(List<String> mutedStreamIds) {
    adapter.setMutedStreamIds(mutedStreamIds);
  }

  @Override public void scrollListToTop() {
    if (streamsList != null) {
      streamsList.scrollToPosition(0);
    }
  }

  @Override public void updateChannelBadge(int unreadChannels, boolean isFollowingChannels) {
    if (menu != null) {
      if (unreadChannels > 0) {
        CustomActionItemBadge.update(getActivity(), menu.findItem(R.id.menu_channel),
            menu.findItem(R.id.menu_channel).getIcon(), isFollowingChannels, unreadChannels);
      } else {
        ActionItemBadge.update(getActivity(), menu.findItem(R.id.menu_channel),
            menu.findItem(R.id.menu_channel).getIcon(), ActionItemBadge.BadgeStyles.RED, null);
      }
    }
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

  @Override public void onStart() {
    super.onStart();
    analyticsTool.analyticsStart(getContext(), analyticsScreenStreamList);
  }
}
