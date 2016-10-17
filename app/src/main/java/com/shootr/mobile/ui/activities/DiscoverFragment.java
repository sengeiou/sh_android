package com.shootr.mobile.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.adapters.DiscoverAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredShotClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredStreamClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.DiscoveredModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.presenter.DiscoverPresenter;
import com.shootr.mobile.ui.views.DiscoverView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.yayandroid.parallaxrecyclerview.ParallaxRecyclerView;
import java.util.List;
import javax.inject.Inject;

public class DiscoverFragment extends BaseFragment implements DiscoverView {

  private static final int COLUMNS_COUNT = 2;

  @BindString(R.string.analytics_action_favorite_stream) String analyticsActionFavoriteStream;
  @BindString(R.string.analytics_label_favorite_stream) String analyticsLabelFavoriteStream;
  @BindString(R.string.analytics_source_discover) String discoverSource;

  @Inject DiscoverPresenter discoverPresenter;
  @Inject ImageLoader imageLoader;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AndroidTimeUtils timeUtils;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;

  @BindView(R.id.discover_recycler) ParallaxRecyclerView discoverList;
  @BindView(R.id.discover_empty) View empty;
  @BindView(R.id.discover_loading) View loading;
  @BindView(R.id.banner) LinearLayout banner;

  private DiscoverAdapter adapter;
  private Unbinder unbinder;

  public static Fragment newInstance() {
    return new DiscoverFragment();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    View fragmentView = inflater.inflate(R.layout.fragment_discover, container, false);
    unbinder = ButterKnife.bind(this, fragmentView);
    return fragmentView;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initializePresenter();
    initializeViews();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_discover, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_search:
        navigateToDiscoverSearch();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void navigateToDiscoverSearch() {
    Intent intent = new Intent(getActivity(), DiscoverSearchActivity.class);
    startActivity(intent);
    getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
  }

  private void initializeViews() {
    setupGridlayout();
    setupAdapter();
    discoverList.setAdapter(adapter);
  }

  private void setupAdapter() {
    adapter = new DiscoverAdapter(imageLoader, new OnDiscoveredStreamClickListener() {
      @Override public void onStreamClick(String streamId) {
        discoverPresenter.streamClicked(streamId);
      }
    }, new OnDiscoveredFavoriteClickListener() {
      @Override public void onFavoriteClick(String idStream, String streamTitle) {
        discoverPresenter.addStreamToFavorites(idStream);
        sendFavoriteAnalytics(idStream, streamTitle);
      }

      @Override public void onRemoveFavoriteClick(String idStream) {
        discoverPresenter.removeFromFavorites(idStream);
      }
    }, new OnDiscoveredShotClickListener() {
      @Override public void onShotClick(ShotModel shotModel) {
        discoverPresenter.shotClicked(shotModel);
      }
    }, new OnAvatarClickListener() {
      @Override public void onAvatarClick(String userId, View avatarView) {
        discoverPresenter.onAvatarClicked(userId);
      }
    }, timeUtils);
  }

  private void sendFavoriteAnalytics(String idStream, String streamTitle) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionFavoriteStream);
    builder.setLabelId(analyticsLabelFavoriteStream);
    builder.setSource(discoverSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setStreamName(streamTitle);
    builder.setIdStream(idStream);
    analyticsTool.analyticsSendAction(builder);
  }

  private void setupGridlayout() {
    GridLayoutManager gridLayoutManager =
        new GridLayoutManager(getContext(), COLUMNS_COUNT, GridLayoutManager.VERTICAL, false);

    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        return (position % 3 == 0 ? 2 : 1);
      }
    });
    discoverList.setLayoutManager(gridLayoutManager);
  }

  private void initializePresenter() {
    discoverPresenter.initialize(this);
  }

  @Override public void renderDiscover(List<DiscoveredModel> discoveredModels) {
    adapter.setItems(discoveredModels);
    adapter.notifyDataSetChanged();
  }

  @Override public void navigateToStreamTimeline(String idStream) {
    startActivity(StreamTimelineActivity.newIntent(getActivity(), idStream));
  }

  @Override public void navigateToShotDetail(ShotModel shotModel) {
    startActivity(ShotDetailActivity.getIntentForActivity(getActivity(), shotModel));
  }

  @Override public void navigateToUserProfile(String userId) {
    startActivity(ProfileActivity.getIntent(getActivity(), userId));
  }

  @Override public void scrollListToTop() {
    if (discoverList != null) {
      discoverList.smoothScrollToPosition(0);
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

  @Override public void showBanner() {
    banner.setVisibility(View.VISIBLE);
  }

  @Override public void hideBanner() {
    banner.setVisibility(View.GONE);
  }

  @Override public void onResume() {
    super.onResume();
    discoverPresenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    discoverPresenter.pause();
  }
}
