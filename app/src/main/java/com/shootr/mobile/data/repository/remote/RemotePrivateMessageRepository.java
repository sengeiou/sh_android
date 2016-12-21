package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.api.entity.PrivateMessageTimelineEntity;
import com.shootr.mobile.data.entity.PrivateMessageEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.PrivateMessageChannelEntityMapper;
import com.shootr.mobile.data.mapper.PrivateMessageEntityMapper;
import com.shootr.mobile.data.mapper.PrivateMessageTimelineEntityMapper;
import com.shootr.mobile.data.repository.datasource.privateMessage.PrivateMessageChannelDataSource;
import com.shootr.mobile.data.repository.datasource.privateMessage.PrivateMessageDataSource;
import com.shootr.mobile.data.repository.datasource.user.UserDataSource;
import com.shootr.mobile.domain.exception.NotAllowedToPublishException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageTimeline;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.privateMessage.PrivateMessageRepository;
import javax.inject.Inject;

public class RemotePrivateMessageRepository implements PrivateMessageRepository {

  private final PrivateMessageDataSource remotePrivateMessageDataSource;
  private final PrivateMessageDataSource localPrivateMessageDataSource;
  private final PrivateMessageChannelDataSource localPrivateMessageChannelDatasource;
  private final PrivateMessageEntityMapper privateMessageEntityMapper;
  private final PrivateMessageTimelineEntityMapper privateMessageTimelineEntityMapper;
  private final PrivateMessageChannelEntityMapper privateMessageChannelEntityMapper;
  private final UserDataSource userDataSource;

  @Inject
  public RemotePrivateMessageRepository(@Remote PrivateMessageDataSource privateMessageDataSource,
      @Local PrivateMessageDataSource localPrivateMessageDataSource,
      @Local PrivateMessageChannelDataSource localPrivateMessageChannelDatasource,
      PrivateMessageEntityMapper privateMessageEntityMapper,
      PrivateMessageTimelineEntityMapper privateMessageTimelineEntityMapper,
      PrivateMessageChannelEntityMapper privateMessageChannelEntityMapper,
      @Local UserDataSource userDataSource) {
    this.remotePrivateMessageDataSource = privateMessageDataSource;
    this.localPrivateMessageDataSource = localPrivateMessageDataSource;
    this.localPrivateMessageChannelDatasource = localPrivateMessageChannelDatasource;
    this.privateMessageEntityMapper = privateMessageEntityMapper;
    this.privateMessageTimelineEntityMapper = privateMessageTimelineEntityMapper;
    this.privateMessageChannelEntityMapper = privateMessageChannelEntityMapper;
    this.userDataSource = userDataSource;
  }

  @Override public PrivateMessageTimeline getPrivateMessageTimeline(String idTargetUser) {
    try {
      PrivateMessageTimelineEntity timelineEntity =
          remotePrivateMessageDataSource.getPrivateMessageTimelineByIdUser(idTargetUser);

      localPrivateMessageChannelDatasource.putPrivateMessageChannel(timelineEntity.getChannel());
      localPrivateMessageDataSource.putPrivateMessages(timelineEntity.getMessages());

      return privateMessageTimelineEntityMapper.transform(timelineEntity);
    } catch (ServerCommunicationException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public PrivateMessageTimeline refreshPrivateMessageTimeline(String idTargetUser, Long timestamp) {
    PrivateMessageTimeline timeline = new PrivateMessageTimeline();
    try {
      PrivateMessageTimelineEntity timelineEntity =
          remotePrivateMessageDataSource.refreshPrivateMessageTimeline(idTargetUser, timestamp);
      timeline.setPrivateMessages(
          privateMessageEntityMapper.transform(timelineEntity.getMessages()));
      localPrivateMessageDataSource.putPrivateMessages(timelineEntity.getMessages());
    } catch (ServerCommunicationException e) {
      e.printStackTrace();
    }
    return timeline;
  }

  @Override
  public PrivateMessageTimeline getOlderPrivateMessages(String idTargetUser, Long timestamp) {
    PrivateMessageTimeline timeline = new PrivateMessageTimeline();
    try {
      PrivateMessageTimelineEntity timelineEntity =
          remotePrivateMessageDataSource.getOlderPrivateMessageTimeline(idTargetUser, timestamp);
      timeline.setPrivateMessages(
          privateMessageEntityMapper.transform(timelineEntity.getMessages()));
      localPrivateMessageDataSource.putPrivateMessages(timelineEntity.getMessages());
    } catch (ServerCommunicationException e) {
      e.printStackTrace();
    }
    return timeline;
  }

  @Override public PrivateMessage putPrivateMessage(PrivateMessage privateMessage) {
    try {
      PrivateMessageEntity privateMessageEntity =
          privateMessageEntityMapper.transform(privateMessage);
      PrivateMessageEntity responseMessageEntity =
          remotePrivateMessageDataSource.putPrivateMessage(privateMessageEntity);
      UserEntity userEntity = userDataSource.getUser(responseMessageEntity.getIdUser());
      responseMessageEntity.setUsername(userEntity.getUserName());
      localPrivateMessageDataSource.putPrivateMessage(responseMessageEntity);
      return privateMessageEntityMapper.transform(responseMessageEntity);
    } catch (NotAllowedToPublishException e) {
      throw e;
    }
  }
}
