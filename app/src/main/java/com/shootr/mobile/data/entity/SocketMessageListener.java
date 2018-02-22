package com.shootr.mobile.data.entity;

import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.StreamTimeline;

public interface SocketMessageListener {

  void onNewItem(String requestId, PrintableItem printableItem);

  void onTimeline(String requestId, StreamTimeline streamTimeline);

  void onFixedItem(String requestId, DataEntity fixedItems);

  void onPinnedItem(String requestId, DataEntity pinnedItems);
}
