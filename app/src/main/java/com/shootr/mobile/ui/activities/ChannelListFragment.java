package com.shootr.mobile.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.MessageChannelListAdapter;
import com.shootr.mobile.ui.adapters.listeners.ChannelClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.PrivateMessageChannelModel;
import com.shootr.mobile.ui.presenter.PrivateMessagesChannelListPresenter;
import com.shootr.mobile.ui.views.PrivateMessageChannelListView;
import com.shootr.mobile.ui.widgets.DividerItemDecoration;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.MenuItemValueHolder;
import java.util.List;
import javax.inject.Inject;

public class ChannelListFragment extends BaseFragment implements PrivateMessageChannelListView {

  private static final int MARGIN_DIVIDER = 80;

  @BindView(R.id.channel_list) RecyclerView listingList;
  @BindView(R.id.channel_loading) View loadingView;
  @BindView(R.id.channel_empty_title) View emptyView;

  @Inject PrivateMessagesChannelListPresenter presenter;
  @Inject InitialsLoader initialsLoader;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AndroidTimeUtils timeUtils;
  @Inject ImageLoader imageLoader;
  @Inject CrashReportTool crashReportTool;


  private MessageChannelListAdapter adapter;
  private Unbinder unbinder;
  private MenuItemValueHolder activeFilter = new MenuItemValueHolder();
  private MenuItemValueHolder removeFilter = new MenuItemValueHolder();

  public static ChannelListFragment newInstance() {
    return new ChannelListFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View fragmentView = inflater.inflate(R.layout.activity_channel_list, container, false);
    unbinder = ButterKnife.bind(this, fragmentView);
    return fragmentView;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initializeViews();
    initializePresenter();
    setHasOptionsMenu(true);
  }

  private void initializeViews() {
    adapter =
        new MessageChannelListAdapter(imageLoader, initialsLoader, new ChannelClickListener() {
          @Override public void onChannelClick(String channelId, String targetUserId) {
            navigateToChannelTimeline(channelId, targetUserId);
          }

          @Override public void onChannelLongClick(String channelId) {
            buildContextualMenu(channelId).show();
          }
        }, timeUtils);
    listingList.setAdapter(adapter);
    listingList.setLayoutManager(new LinearLayoutManager(getContext()));
    listingList.addItemDecoration(new DividerItemDecoration(getContext(), MARGIN_DIVIDER,
        getResources().getDrawable(R.drawable.line_divider), false, false));
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  public void initializePresenter() {
    presenter.initialize(this);
  }

  @Override public void showLoading() {
    if (loadingView != null) {
      loadingView.setVisibility(View.VISIBLE);
    }
  }

  @Override public void hideLoading() {
    if (loadingView != null) {
      loadingView.setVisibility(View.GONE);
    }
  }

  @Override public void showEmpty() {
    if (emptyView != null) {
      emptyView.setVisibility(View.VISIBLE);
    }
  }

  @Override public void hideEmpty() {
    if (emptyView != null) {
      emptyView.setVisibility(View.GONE);
    }
  }

  @Override
  public void renderChannels(List<PrivateMessageChannelModel> privateMessageChannelModels) {
    adapter.setPrivateMessageChannelModels(privateMessageChannelModels);
    adapter.notifyDataSetChanged();
  }

  @Override public void showError(String errorMessage) {
    feedbackMessage.show(getView(), errorMessage);
  }

  @Override public void navigateToChannelTimeline(String channelId, String idTargetUser) {
    startActivity(PrivateMessageTimelineActivity.newIntent(getContext(), channelId, idTargetUser));
  }

  @Override public void updateTitle(int unreads) {
    /* no- op */
  }

  @Override public void clearChannels() {
    adapter.clear();
  }

  @Override public void showRemoveFilter() {
    removeFilter.setVisible(true);
    activeFilter.setVisible(false);
  }

  @Override public void showActiveFilter() {
    removeFilter.setVisible(false);
    activeFilter.setVisible(true);
  }

  @Override public void onResume() {
    super.onResume();
    if (presenter != null) {
      presenter.resume();
    }
  }

  @Override public void onPause() {
    super.onPause();
    if (presenter != null) {
      presenter.pause();
    }
  }

  private CustomContextMenu.Builder buildContextualMenu(final String privateMessageChannelId) {
    return new CustomContextMenu.Builder(getActivity()).addAction(
        R.string.remove_private_message_channel, new Runnable() {
          @Override public void run() {
            presenter.removePrivateMessageChannel(privateMessageChannelId);
          }
        });
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.private_messages_menu, menu);
    activeFilter.bindRealMenuItem(menu.findItem(R.id.menu_active_filter));
    removeFilter.bindRealMenuItem(menu.findItem(R.id.menu_remove_filter));
    removeFilter.setVisible(false);

  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_active_filter:
        presenter.onFilterActivated();
        return true;
      case R.id.menu_remove_filter:
        presenter.onRemoveFilter();
        return true;
      case R.id.menu_search:
        navigateToSearch();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void navigateToSearch() {
    Intent intent = new Intent(getContext(), SearchActivity.class);
    startActivity(intent);
    getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
  }
}
