package com.shootr.mobile.ui.views;

import com.shootr.mobile.domain.model.SocketMessage;

public interface SocketView {

  void onMessage(SocketMessage socketMessage);

  void stopService();

}
