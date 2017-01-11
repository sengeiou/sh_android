package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.PrivateMessageChannelModel;
import java.util.List;

public interface PrivateMessageChannelListView {

  void showLoading();

  void hideLoading();

  void showEmpty();

  void hideEmpty();

  void renderChannels(List<PrivateMessageChannelModel> privateMessageChannelModels);

  void showError(String errorMessage);

  void navigateToChannelTimeline(String channelId, String targetUserId);

  void updateTitle(int unreads);
}
