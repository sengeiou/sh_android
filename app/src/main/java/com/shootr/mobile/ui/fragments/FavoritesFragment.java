package com.shootr.mobile.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.adapters.FavoriteStreamsAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.presenter.FavoritesListPresenter;
import com.shootr.mobile.ui.views.FavoritesListView;
import com.shootr.mobile.ui.views.nullview.NullFavoritesListView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import java.util.List;
import javax.inject.Inject;

public class FavoritesFragment extends BaseFragment implements FavoritesListView {

  @Inject FavoritesListPresenter favoritesListPresenter;
  @Inject ImageLoader imageLoader;
  @Inject IntentFactory intentFactory;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AnalyticsTool analyticsTool;
  @Inject InitialsLoader initialsLoader;

  @Bind(R.id.favorites_list) RecyclerView favoritesList;
  @Bind(R.id.favorites_empty) View empty;
  @Bind(R.id.favorites_loading) View loading;
  @BindString(R.string.shared_stream_notification) String sharedStream;
  @BindString(R.string.analytics_screen_favorites) String analyticsScreenFavorites;

  private FavoriteStreamsAdapter adapter;

  public static Fragment newInstance() {
    return new FavoritesFragment();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View fragmentView = inflater.inflate(R.layout.fragment_favorites, container, false);
    ButterKnife.bind(this, fragmentView);
    return fragmentView;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    analyticsTool.analyticsStop(getContext(), getActivity());
    ButterKnife.unbind(this);
    favoritesListPresenter.setView(new NullFavoritesListView());
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    analyticsTool.analyticsStart(getContext(), analyticsScreenFavorites);
    initializePresenter();
    initializeViews();
  }

  @Override public void onResume() {
    super.onResume();
    favoritesListPresenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    favoritesListPresenter.pause();
  }

  private void initializeViews() {
    favoritesList.setLayoutManager(new LinearLayoutManager(getActivity()));
    adapter = new FavoriteStreamsAdapter(imageLoader, initialsLoader, new OnStreamClickListener() {
      @Override public void onStreamClick(StreamResultModel stream) {
        favoritesListPresenter.selectStream(stream);
      }

      @Override public boolean onStreamLongClick(StreamResultModel stream) {
        favoritesListPresenter.onFavoriteLongClicked(stream);
        return true;
      }
    });
    adapter.setOnUnwatchClickListener(new OnUnwatchClickListener() {
      @Override public void onUnwatchClick() {
        favoritesListPresenter.unwatchStream();
      }
    });
    favoritesList.setAdapter(adapter);
  }

  private void initializePresenter() {
    favoritesListPresenter.initialize(this);
  }

  private CustomContextMenu.Builder baseContextualMenu(final StreamResultModel stream) {
    return new CustomContextMenu.Builder(getActivity()) //
        .addAction(R.string.menu_remove_favorite, new Runnable() {
          @Override public void run() {
            favoritesListPresenter.removeFromFavorites(stream);
          }
        }).addAction(R.string.share_via_shootr, new Runnable() {
          @Override public void run() {
            favoritesListPresenter.shareStream(stream);
          }
        }).addAction(R.string.share_via, new Runnable() {
          @Override public void run() {
            shareStream(stream);
          }
        });
  }

  private void shareStream(StreamResultModel stream) {
    Intent shareIntent = intentFactory.shareStreamIntent(getActivity(), stream.getStreamModel());
    Intents.maybeStartActivity(getActivity(), shareIntent);
  }

  @Override public void renderFavorites(List<StreamResultModel> streamModels) {
    adapter.setStreams(streamModels);
    adapter.notifyDataSetChanged();
  }

  @Override public void showContent() {
    favoritesList.setVisibility(View.VISIBLE);
  }

  @Override public void hideContent() {
    favoritesList.setVisibility(View.GONE);
  }

  @Override public void navigateToStreamTimeline(String idStream, String title, String authorId) {
    startActivity(StreamTimelineActivity.newIntent(getActivity(), idStream, title, authorId));
    getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
  }

  @Override public void showStreamShared() {
    feedbackMessage.show(getView(), sharedStream);
  }

  @Override public void setMutedStreamIds(List<String> mutedStreamIds) {
    adapter.setMutedStreamIds(mutedStreamIds);
  }

  @Override public void showContextMenuWithUnmute(final StreamResultModel stream) {
    baseContextualMenu(stream).addAction(R.string.unmute, new Runnable() {
      @Override public void run() {
        favoritesListPresenter.onUnmuteClicked(stream);
      }
    }).show();
  }

  @Override public void showContextMenuWithMute(final StreamResultModel stream) {
    baseContextualMenu(stream).addAction(R.string.mute, new Runnable() {
      @Override public void run() {
        favoritesListPresenter.onMuteClicked(stream);
      }
    }).show();
  }

  @Override public void scrollListToTop() {
    if (favoritesList != null) {
      favoritesList.scrollToPosition(0);
    }
  }

  @Override public void showEmpty() {
    empty.setVisibility(View.VISIBLE);
  }

  @Override public void hideEmpty() {
    empty.setVisibility(View.GONE);
  }

  @Override public void showLoading() {
    loading.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    loading.setVisibility(View.GONE);
  }

  @Override public void showError(String message) {
    feedbackMessage.show(getView(), message);
  }
}
