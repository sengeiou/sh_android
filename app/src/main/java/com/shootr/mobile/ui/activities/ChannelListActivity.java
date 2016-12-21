package com.shootr.mobile.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.MessageChannelListAdapter;
import com.shootr.mobile.ui.adapters.listeners.ChannelClickListener;
import com.shootr.mobile.ui.model.PrivateMessageChannelModel;
import com.shootr.mobile.ui.presenter.PrivateMessagesChannelListPresenter;
import com.shootr.mobile.ui.views.PrivateMessageChannelListView;
import com.shootr.mobile.ui.widgets.DividerItemDecoration;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;
import javax.inject.Inject;

public class ChannelListActivity extends BaseToolbarDecoratedActivity implements
    PrivateMessageChannelListView {

  private static final int MARGIN_DIVIDER = 80;

  @BindView(R.id.channel_list) RecyclerView listingList;
  @BindView(R.id.channel_loading) View loadingView;
  @BindView(R.id.channel_empty_title) View emptyView;

  @BindString(R.string.analytics_screen_channel_list) String analyticsChannelList;

  @Inject PrivateMessagesChannelListPresenter presenter;
  @Inject InitialsLoader initialsLoader;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AnalyticsTool analyticsTool;
  @Inject AndroidTimeUtils timeUtils;

  private MessageChannelListAdapter adapter;

  @Override protected int getLayoutResource() {
    return R.layout.activity_channel_list;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    adapter = new MessageChannelListAdapter(imageLoader, initialsLoader, new ChannelClickListener() {
      @Override public void onChannelClick(String channelId, String targetUserId) {
        navigateToChannelTimeline(channelId, targetUserId);
      }
    }, timeUtils);
    listingList.setAdapter(adapter);
    listingList.setLayoutManager(new LinearLayoutManager(this));
    listingList.addItemDecoration(
        new DividerItemDecoration(this, MARGIN_DIVIDER, getResources().getDrawable(R.drawable.line_divider), false,
            false));
  }

  @Override protected void initializePresenter() {
    presenter.initialize(this);
    analyticsTool.analyticsStart(getBaseContext(), analyticsChannelList);
  }

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    /* no-op */
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
    startActivity(PrivateMessageTimelineActivity.newIntent(this, channelId, idTargetUser));
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.resume();
  }

  @Override protected void onPause() {
    super.onPause();
    presenter.pause();
    analyticsTool.analyticsStop(getBaseContext(), this);
  }
}
