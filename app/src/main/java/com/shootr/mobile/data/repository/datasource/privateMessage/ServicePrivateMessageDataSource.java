package com.shootr.mobile.data.repository.datasource.privateMessage;

import com.shootr.mobile.data.api.entity.PrivateMessageTimelineEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.PrivateMessagesApiService;
import com.shootr.mobile.data.entity.PrivateMessageEntity;
import com.shootr.mobile.domain.exception.NotAllowedToPublishException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.UserBlockedToPrivateMessageException;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannelTimelineParameters;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServicePrivateMessageDataSource implements PrivateMessageDataSource {

  private final PrivateMessagesApiService privateMessagesApiService;

  @Inject
  public ServicePrivateMessageDataSource(PrivateMessagesApiService privateMessagesApiService) {
    this.privateMessagesApiService = privateMessagesApiService;
  }

  @Override
  public PrivateMessageTimelineEntity getPrivateMessageTimelineByIdUser(String idTargetUser) {
    try {
      PrivateMessageTimelineEntity timelineEntity =
          privateMessagesApiService.getTimeline(idTargetUser);
      return timelineEntity;
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public PrivateMessageTimelineEntity refreshPrivateMessageTimeline(String idTargetUser,
      Long timestamp) {
    try {
      return privateMessagesApiService.refreshTimeline(idTargetUser, timestamp);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public PrivateMessageTimelineEntity getOlderPrivateMessageTimeline(String idTargetUser,
      Long maxTimestamp) {
    try {
      return privateMessagesApiService.getOlderTimeline(idTargetUser, maxTimestamp);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override
  public PrivateMessageEntity putPrivateMessage(PrivateMessageEntity privateMessageEntity) {
    try {
      return privateMessagesApiService.putPrivateMesssage(privateMessageEntity);
    } catch (IOException e) {
      throw new ServerCommunicationException(e);
    } catch (ApiException err) {
      if (err.getErrorInfo().code() == 9001) {
        throw new UserBlockedToPrivateMessageException(err);
      } else if (err.getErrorInfo().code() >= 9000 && err.getErrorInfo().code() <= 9999) {
        throw new NotAllowedToPublishException(err);
      } else {
        throw new ServerCommunicationException(err);
      }
    }
  }

  @Override public void putPrivateMessages(List<PrivateMessageEntity> privateMessageEntities) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public List<PrivateMessageEntity> getPrivateMessageTimeline(
      PrivateMessageChannelTimelineParameters parameters) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public List<PrivateMessageEntity> getPrivateMessageTimelineByIdChannel(
      String idPrivateMessageChannel) {
    throw new IllegalArgumentException("method not implemented");
  }
}
