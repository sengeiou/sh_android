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
import com.shootr.mobile.ui.adapters.SearchAdapter;
import com.shootr.mobile.ui.adapters.listeners.FavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnSearchStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.SearchableModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.SearchItemsPresenter;
import com.shootr.mobile.ui.views.SearchStreamView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShareManager;
import java.util.List;
import javax.inject.Inject;

public class FindStreamsFragment extends BaseFragment implements SearchStreamView, SearchFragment {

  private SearchAdapter adapter;

  @BindView(R.id.find_streams_list) RecyclerView streamsList;
  @BindView(R.id.find_streams_empty) View emptyView;
  @BindView(R.id.find_streams_loading) View loadingView;
  @BindString(R.string.added_to_favorites) String addedToFavorites;
  @BindString(R.string.removed_from_favorites) String removedFromFavorites;
  @BindString(R.string.shared_stream_notification) String sharedStream;
  @BindString(R.string.analytics_action_external_share_stream) String analyticsActionExternalShare;
  @BindString(R.string.analytics_label_external_share_stream) String analyticsLabelExternalShare;
  @BindString(R.string.analytics_source_stream_search) String discoverSearchSource;
  @BindString(R.string.analytics_action_favorite_stream) String analyticsActionFavoriteStream;
  @BindString(R.string.analytics_label_favorite_stream) String analyticsLabelFavoriteStream;

  @Inject SearchItemsPresenter searchItemsPresenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject ShareManager shareManager;
  @Inject InitialsLoader initialsLoader;
  @Inject ImageLoader imageLoader;
  @Inject NumberFormatUtil numberFormatUtil;
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
    searchItemsPresenter.initialize(null, this);
  }

  private void initializeStreamListAdapter() {

    adapter = new SearchAdapter(imageLoader, numberFormatUtil, initialsLoader, new OnFollowUnfollowListener() {
      @Override public void onFollow(UserModel user) {
        /* no-op */
      }

      @Override public void onUnfollow(UserModel user) {
        /* no-op */
      }
    }, new OnUserClickListener() {
      @Override public void onUserClick(String idUser) {
        /* no-op */
      }
    }, new OnSearchStreamClickListener() {
      @Override public void onStreamClick(StreamModel stream) {
        navigateToStreamTimeline(stream.getIdStream(), stream.getTitle());
      }

      @Override public void onStreamLongClick(StreamModel stream) {
        searchItemsPresenter.openContextualMenu(stream);
      }
    }, new FavoriteClickListener() {
      @Override public void onFavoriteClick(StreamModel stream) {
        searchItemsPresenter.addToFavorites(stream);
        adapter.markFavorite(stream);
        sendFavoriteAnalytics(stream);
      }

      @Override public void onRemoveFavoriteClick(StreamModel stream) {
        searchItemsPresenter.removeFromFavorites(stream);
        adapter.unmarkFavorite(stream);
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

  private void sendFavoriteAnalytics(StreamModel stream) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionFavoriteStream);
    builder.setLabelId(analyticsLabelFavoriteStream);
    builder.setSource(discoverSearchSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setStreamName(stream.getTitle());
    builder.setIsStrategic(stream.isStrategic());
    builder.setIdStream(stream.getIdStream());
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  private void sendExternalShareAnalytics(StreamModel streamResultModel) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionExternalShare);
    builder.setLabelId(analyticsLabelExternalShare);
    builder.setSource(discoverSearchSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setStreamName(streamResultModel.getTitle());
    builder.setIdStream(streamResultModel.getIdStream());
    analyticsTool.analyticsSendAction(builder);
  }

  private void shareStream(StreamModel stream) {
    Intent shareIntent = shareManager.shareStreamIntent(getActivity(), stream);
    Intents.maybeStartActivity(getContext(), shareIntent);
  }

  @Override public void hideKeyboard() {
    final InputMethodManager imm =
        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }

  @Override public void showError(String errorMessage) {
    feedbackMessage.show(getView(), errorMessage);
  }

  @Override
  public void navigateToStreamTimeline(String idStream, String streamTitle) {
    startActivity(StreamTimelineActivity.newIntent(getContext(), idStream, streamTitle));
  }

  @Override public void showAddedToFavorites(StreamModel streamModel) {
    /* no-op */
  }

  @Override public void showRemovedFromFavorites(StreamModel streamModel) {
    /* no-op */
  }

  @Override public void showStreamShared() {

  }

  @Override public void openContextualMenuWithAddFavorite(final StreamModel stream) {
    new CustomContextMenu.Builder(getContext()).addAction(R.string.add_to_favorites_menu_title,
        new Runnable() {
          @Override public void run() {
            searchItemsPresenter.addToFavorites(stream);
            adapter.markFavorite(stream);
            sendFavoriteAnalytics(stream);
          }
        }).addAction(R.string.share_stream_via_shootr, new Runnable() {
      @Override public void run() {
        searchItemsPresenter.shareStream(stream);
      }
    }).addAction(R.string.share_via, new Runnable() {
      @Override public void run() {
        shareStream(stream);
        sendExternalShareAnalytics(stream);
      }
    }).show();
  }

  @Override public void openContextualMenuWithUnmarkFavorite(final StreamModel stream) {
    new CustomContextMenu.Builder(getContext()).addAction(R.string.menu_remove_favorite,
        new Runnable() {
          @Override public void run() {
            searchItemsPresenter.removeFromFavorites(stream);
            adapter.unmarkFavorite(stream);
          }
        }).addAction(R.string.share_stream_via_shootr, new Runnable() {
      @Override public void run() {
        searchItemsPresenter.shareStream(stream);
      }
    }).addAction(R.string.share_via, new Runnable() {
      @Override public void run() {
        shareStream(stream);
        sendExternalShareAnalytics(stream);
      }
    }).show();
  }

  private void setupViews() {
    streamsList.setLayoutManager(new LinearLayoutManager(getContext()));
    initializeStreamListAdapter();
  }

  @Override public void onResume() {
    super.onResume();
    searchItemsPresenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    searchItemsPresenter.pause();
  }

  @Override public void renderSearchItems(List<SearchableModel> searchableModels) {
    adapter.setItems(searchableModels);
    adapter.notifyDataSetChanged();
  }
}
