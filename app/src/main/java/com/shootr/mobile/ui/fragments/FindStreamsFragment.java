package com.shootr.mobile.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.DiscoverSearchActivity;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.adapters.StreamsListAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.ui.base.BaseSearchFragment;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.presenter.FindStreamsPresenter;
import com.shootr.mobile.ui.views.FindStreamsView;
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

  @Bind(R.id.find_streams_list) RecyclerView streamsList;
  @Bind(R.id.find_streams_empty) View emptyView;
  @Bind(R.id.find_streams_loading) View loadingView;
  @BindString(R.string.added_to_favorites) String addedToFavorites;
  @BindString(R.string.shared_stream_notification) String sharedStream;

  @Inject FindStreamsPresenter findStreamsPresenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject ShareManager shareManager;
  @Inject InitialsLoader initialsLoader;
  @Inject ImageLoader imageLoader;

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
    adapter = new StreamsListAdapter(imageLoader, initialsLoader, new OnStreamClickListener() {
      @Override public void onStreamClick(StreamResultModel stream) {
        findStreamsPresenter.selectStream(stream);
      }

      @Override public boolean onStreamLongClick(StreamResultModel stream) {
        openContextualMenu(stream);
        return true;
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
          }
        }).addAction(R.string.share_via_shootr, new Runnable() {
      @Override public void run() {
        findStreamsPresenter.shareStream(stream);
      }
    }).addAction(R.string.share_via, new Runnable() {
      @Override public void run() {
        shareStream(stream);
      }
    }).show();
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
    ((DiscoverSearchActivity) getActivity()).hideKeyboard();
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
}
