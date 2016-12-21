package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.PrivateMessageModel;
import java.util.List;

public interface PrivateMessageChannelTimelineView extends TimelineView {

  void setMessages(List<PrivateMessageModel> privateMessageModels);

  void addAbove(List<PrivateMessageModel> privateMessageModels);

  void updateMessagesInfo(List<PrivateMessageModel> privateMessageModels);

  void addMessages(List<PrivateMessageModel> privateMessageModels);

  void addOldMessages(List<PrivateMessageModel> oldMessages);
}
