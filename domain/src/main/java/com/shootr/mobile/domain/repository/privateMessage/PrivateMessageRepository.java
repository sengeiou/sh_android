package com.shootr.mobile.domain.repository.privateMessage;

import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageTimeline;

public interface PrivateMessageRepository {

  PrivateMessageTimeline getPrivateMessageTimeline(String idTargetUser);

  PrivateMessageTimeline refreshPrivateMessageTimeline(String idTargetUser, Long timestamp);

  PrivateMessageTimeline getOlderPrivateMessages(String idTargetUser, Long timestamp);

  PrivateMessage putPrivateMessage(PrivateMessage privateMessage);
}
