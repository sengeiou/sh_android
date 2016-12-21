package com.shootr.mobile.data.repository.datasource.privateMessage;

import com.shootr.mobile.data.api.entity.PrivateMessageTimelineEntity;
import com.shootr.mobile.data.entity.PrivateMessageEntity;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannelTimelineParameters;
import java.util.List;

public interface PrivateMessageDataSource {

  PrivateMessageTimelineEntity getPrivateMessageTimelineByIdUser(String idTargetUser);

  PrivateMessageTimelineEntity refreshPrivateMessageTimeline(String idTargetUser, Long timestamp);

  PrivateMessageTimelineEntity getOlderPrivateMessageTimeline(String idTargetUser, Long maxTimestamp);

  PrivateMessageEntity putPrivateMessage(PrivateMessageEntity privateMessageEntity);

  void putPrivateMessages(List<PrivateMessageEntity> privateMessageEntities);

  List<PrivateMessageEntity> getPrivateMessageTimeline(
      PrivateMessageChannelTimelineParameters parameters);

  List<PrivateMessageEntity> getPrivateMessageTimelineByIdChannel(String idPrivateMessageChannel);
}
