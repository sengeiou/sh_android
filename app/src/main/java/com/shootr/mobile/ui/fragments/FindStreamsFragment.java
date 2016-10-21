package com.shootr.mobile.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.adapters.StreamsListAdapter;
import com.shootr.mobile.ui.adapters.WatchableStreamsAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.mobile.ui.base.BaseSearchFragment;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.presenter.FindStreamsPresenter;
import com.shootr.mobile.ui.views.FindStreamsView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.ShareManager;
import java.util.List;
import javax.inject.Inject;

public class FindStreamsFragment extends BaseSearchFragment implements FindStreamsView {

  private StreamsListAdapter adapter;

  @BindView(R.id.find_streams_list) RecyclerView streamsList;
  @BindView(R.id.find_streams_empty) View emptyView;
  @BindView(R.id.find_streams_loading) View loadingView;
  @BindString(R.string.added_to_favorites) String addedToFavorites;
  @BindString(R.string.shared_stream_notification) String sharedStream;
  @BindString(R.string.analytics_action_external_share_stream) String analyticsActionExternalShare;
  @BindString(R.string.analytics_label_external_share_stream) String analyticsLabelExternalShare;
  @BindString(R.string.analytics_source_discover_stream_search) String discoverSearchSource;
  @BindString(R.string.analytics_action_favorite_stream) String analyticsActionFavoriteStream;
  @BindString(R.string.analytics_label_favorite_stream) String analyticsLabelFavoriteStream;

  @Inject FindStreamsPresenter findStreamsPresenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject ShareManager shareManager;
  @Inject InitialsLoader initialsLoader;
  @Inject ImageLoader imageLoader;
  @Inject SessionRepository sessionRepository;
  @Inject AnalyticsTool analyticsTool;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_find_streams, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ButterKnife.bind(this, getView());
    streamsList.setLayoutManager(new LinearLayoutManager(getContext()));
    setupViews();
    findStreamsPresenter.initialize(this);
  }

  private void initializeStreamListAdapter() {
    adapter = new WatchableStreamsAdapter(imageLoader, initialsLoader, new OnStreamClickListener() {
      @Override public void onStreamClick(StreamResultModel stream) {
        findStreamsPresenter.selectStream(stream);
      }

      @Override public boolean onStreamLongClick(StreamResultModel stream) {
        openContextualMenu(stream);
        return true;
      }
    }, null, false);
    adapter.setOnUnwatchClickListener(new OnUnwatchClickListener() {
      @Override public void onUnwatchClick() {
        findStreamsPresenter.unwatchStream();
      }
    });
    streamsList.setAdapter(adapter);
    streamsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        hideKeyboard();
      }
    });
  }

  private void openContextualMenu(final StreamResultModel stream) {
    new CustomContextMenu.Builder(getContext()).addAction(R.string.add_to_favorites_menu_title,
        new Runnable() {
          @Override public void run() {
            findStreamsPresenter.addToFavorites(stream);
            sendFavoriteAnalytics(stream);
          }
        }).addAction(R.string.share_stream_via_shootr, new Runnable() {
      @Override public void run() {
        findStreamsPresenter.shareStream(stream);
      }
    }).addAction(R.string.share_via, new Runnable() {
      @Override public void run() {
        shareStream(stream);
        sendExternalShareAnalytics(stream);
      }
    }).show();
  }

  private void sendFavoriteAnalytics(StreamResultModel stream) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionFavoriteStream);
    builder.setLabelId(analyticsLabelFavoriteStream);
    builder.setSource(discoverSearchSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setStreamName(stream.getStreamModel().getTitle());
    builder.setIdStream(stream.getStreamModel().getIdStream());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendExternalShareAnalytics(StreamResultModel streamResultModel) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionExternalShare);
    builder.setLabelId(analyticsLabelExternalShare);
    builder.setSource(discoverSearchSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setStreamName(streamResultModel.getStreamModel().getTitle());
    builder.setIdStream(streamResultModel.getStreamModel().getIdStream());
    analyticsTool.analyticsSendAction(builder);
  }

  private void shareStream(StreamResultModel stream) {
    Intent shareIntent = shareManager.shareStreamIntent(getActivity(), stream.getStreamModel());
    Intents.maybeStartActivity(getContext(), shareIntent);
  }

  //region View methods
  @Override public void hideContent() {
    streamsList.setVisibility(View.GONE);
  }

  @Override public void hideKeyboard() {
    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }

  @Override public void showLoading() {
    loadingView.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    loadingView.setVisibility(View.GONE);
  }

  @Override public void showEmpty() {
    emptyView.setVisibility(View.VISIBLE);
  }

  @Override public void showContent() {
    streamsList.setVisibility(View.VISIBLE);
  }

  @Override public void hideEmpty() {
    emptyView.setVisibility(View.GONE);
  }

  @Override public void renderStreams(List<StreamResultModel> streamModels) {
    adapter.setStreams(streamModels);
  }

  @Override public void showError(String errorMessage) {
    feedbackMessage.show(getView(), errorMessage);
  }

  @Override
  public void navigateToStreamTimeline(String idStream, String streamTitle, String authorId) {
    startActivity(StreamTimelineActivity.newIntent(getContext(), idStream, streamTitle, authorId));
  }

  @Override public void showAddedToFavorites() {
    feedbackMessage.show(getView(), addedToFavorites);
  }

  @Override public void showStreamShared() {
    feedbackMessage.show(getView(), sharedStream);
  }

  //endregion

  @Override public void search(String query) {
    findStreamsPresenter.search(query);
  }

  @Override public void searchChanged(String query) {
    findStreamsPresenter.reactiveSearch(query);
  }

  private void setupViews() {
    streamsList.setLayoutManager(new LinearLayoutManager(getContext()));
    initializeStreamListAdapter();
  }

  @Override public void onResume() {
    super.onResume();
    findStreamsPresenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    findStreamsPresenter.pause();
  }
}
