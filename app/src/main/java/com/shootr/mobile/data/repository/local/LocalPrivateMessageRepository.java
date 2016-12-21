package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.mapper.PrivateMessageTimelineEntityMapper;
import com.shootr.mobile.data.repository.datasource.privateMessage.PrivateMessageDataSource;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageTimeline;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.privateMessage.PrivateMessageRepository;
import javax.inject.Inject;

public class LocalPrivateMessageRepository implements PrivateMessageRepository {

  private final PrivateMessageDataSource localPrivateMessageDataSource;
  private final PrivateMessageTimelineEntityMapper privateMessageTimelineEntityMapper;

  @Inject public LocalPrivateMessageRepository(
      @Local PrivateMessageDataSource localPrivateMessageDataSource,
      PrivateMessageTimelineEntityMapper privateMessageTimelineEntityMapper) {
    this.localPrivateMessageDataSource = localPrivateMessageDataSource;
    this.privateMessageTimelineEntityMapper = privateMessageTimelineEntityMapper;
  }

  @Override public PrivateMessageTimeline getPrivateMessageTimeline(String idTargetUser) {
    return privateMessageTimelineEntityMapper.transform(
        localPrivateMessageDataSource.getPrivateMessageTimelineByIdUser(idTargetUser));
  }

  @Override public PrivateMessageTimeline refreshPrivateMessageTimeline(String idTargetUser,
      Long timestamp) {
    throw new IllegalStateException("Refresh from local is not implemented");
  }

  @Override
  public PrivateMessageTimeline getOlderPrivateMessages(String idTargetUser, Long timestamp) {
    throw new IllegalStateException("method from local is not implemented");
  }

  @Override public PrivateMessage putPrivateMessage(PrivateMessage privateMessage) {
    return null;
  }
}
