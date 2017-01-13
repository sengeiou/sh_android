package com.shootr.mobile.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;
import javax.inject.Inject;

public class ChannelListFragment extends BaseFragment implements
    PrivateMessageChannelListView {

  private static final int MARGIN_DIVIDER = 80;

  @BindView(R.id.channel_list) RecyclerView listingList;
  @BindView(R.id.channel_loading) View loadingView;
  @BindView(R.id.channel_empty_title) View emptyView;

  @Inject PrivateMessagesChannelListPresenter presenter;
  @Inject InitialsLoader initialsLoader;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AndroidTimeUtils timeUtils;
  @Inject ImageLoader imageLoader;

  private MessageChannelListAdapter adapter;
  private Unbinder unbinder;

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
  }

  private void initializeViews() {
    adapter = new MessageChannelListAdapter(imageLoader, initialsLoader, new ChannelClickListener() {
      @Override public void onChannelClick(String channelId, String targetUserId) {
        navigateToChannelTimeline(channelId, targetUserId);
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
    loadingView.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    loadingView.setVisibility(View.GONE);
  }

  @Override public void showEmpty() {
    emptyView.setVisibility(View.VISIBLE);
  }

  @Override public void hideEmpty() {
    emptyView.setVisibility(View.GONE);
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
    ((ChannelsContainerActivity) getActivity()).setTabTitle(this, unreads);
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
}
