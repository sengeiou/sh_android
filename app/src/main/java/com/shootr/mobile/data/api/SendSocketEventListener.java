package com.shootr.mobile.data.api;

import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;

/**
 * Created by miniserver on 17/5/18.
 */

public interface SendSocketEventListener {

  void sendEvent(SocketMessageApiEntity event);

  void onRestoreLastTimeline(String idStream, String filter, PaginationEntity paginationEntity);
}
