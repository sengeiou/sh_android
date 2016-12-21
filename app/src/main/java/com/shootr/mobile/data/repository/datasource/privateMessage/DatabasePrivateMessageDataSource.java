package com.shootr.mobile.data.repository.datasource.privateMessage;

import com.shootr.mobile.data.api.entity.PrivateMessageTimelineEntity;
import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import com.shootr.mobile.data.entity.PrivateMessageEntity;
import com.shootr.mobile.db.manager.PrivateMessageChannelManager;
import com.shootr.mobile.db.manager.PrivateMessageManager;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannelTimelineParameters;
import java.util.List;
import javax.inject.Inject;

public class DatabasePrivateMessageDataSource implements PrivateMessageDataSource {

  private final PrivateMessageManager privateMessageManager;
  private final PrivateMessageChannelManager privateMessageChannelManager;

  @Inject public DatabasePrivateMessageDataSource(PrivateMessageManager privateMessageManager,
      PrivateMessageChannelManager privateMessageChannelManager) {
    this.privateMessageManager = privateMessageManager;
    this.privateMessageChannelManager = privateMessageChannelManager;
  }

  @Override
  public PrivateMessageEntity putPrivateMessage(PrivateMessageEntity privateMessageEntity) {
    privateMessageManager.savePrivateMessage(privateMessageEntity);
    return privateMessageEntity;
  }

  @Override public void putPrivateMessages(List<PrivateMessageEntity> privateMessageEntities) {
    privateMessageManager.savePrivateMessages(privateMessageEntities);
  }

  @Override public List<PrivateMessageEntity> getPrivateMessageTimeline(
      PrivateMessageChannelTimelineParameters parameters) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public List<PrivateMessageEntity> getPrivateMessageTimelineByIdChannel(
      String idPrivateMessageChannel) {
    return privateMessageManager.getPrivateMessageByIdChannel(idPrivateMessageChannel);
  }

  @Override public PrivateMessageTimelineEntity getPrivateMessageTimelineByIdUser(String idTargetUser) {

    PrivateMessageChannelEntity privateMessageChannelEntity =
        privateMessageChannelManager.getPrivateMessageChannelByIdUser(idTargetUser);

    if (privateMessageChannelEntity != null) {
      PrivateMessageTimelineEntity timelineEntity = new PrivateMessageTimelineEntity();
      timelineEntity.setChannel(privateMessageChannelEntity);
      List<PrivateMessageEntity> privateMessageEntities =
          privateMessageManager.getPrivateMessageByIdChannel(
              privateMessageChannelEntity.getIdPrivateMessageChannel());
      timelineEntity.setMessages(privateMessageEntities);
      return timelineEntity;
    } else {
      return null;
    }
  }

  @Override public PrivateMessageTimelineEntity refreshPrivateMessageTimeline(String idTargetUser,
      Long timestamp) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public PrivateMessageTimelineEntity getOlderPrivateMessageTimeline(String idTargetUser,
      Long maxTimestamp) {
    throw new IllegalArgumentException("method not implemented");
  }
}
