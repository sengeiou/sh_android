package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import com.shootr.mobile.data.entity.SynchroEntity;
import com.shootr.mobile.data.mapper.PrivateMessageChannelEntityMapper;
import com.shootr.mobile.data.repository.datasource.SynchroDataSource;
import com.shootr.mobile.data.repository.datasource.privateMessage.PrivateMessageChannelDataSource;
import com.shootr.mobile.data.repository.datasource.privateMessage.PrivateMessageDataSource;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannel;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.privateMessage.PrivateMessageChannelRepository;
import com.shootr.mobile.util.AndroidTimeUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class RemotePrivateMessageChannelRepository implements PrivateMessageChannelRepository {

  private final PrivateMessageChannelDataSource privateMessageChannelDataSource;
  private final PrivateMessageChannelDataSource remoteMessageChannelDataSource;
  private final PrivateMessageChannelEntityMapper privateMessageChannelEntityMapper;
  private final SynchroDataSource synchroDataSource;
  private final AndroidTimeUtils androidTimeUtils;

  @Inject public RemotePrivateMessageChannelRepository(
      @Remote PrivateMessageDataSource remotePrivateMessageChannelDataSource,
      @Local PrivateMessageChannelDataSource privateMessageChannelDataSource,
      @Remote PrivateMessageChannelDataSource remoteMessageChannelDataSource,
      PrivateMessageChannelEntityMapper privateMessageChannelEntityMapper,
      SynchroDataSource synchroDataSource, AndroidTimeUtils androidTimeUtils) {
    this.privateMessageChannelDataSource = privateMessageChannelDataSource;
    this.privateMessageChannelEntityMapper = privateMessageChannelEntityMapper;
    this.remoteMessageChannelDataSource = remoteMessageChannelDataSource;
    this.synchroDataSource = synchroDataSource;
    this.androidTimeUtils = androidTimeUtils;
  }

  @Override public List<PrivateMessageChannel> getPrivateMessageChannels() {
    ArrayList<PrivateMessageChannel> privateMessageChannels = new ArrayList<>();
    try {
      SynchroEntity synchroEntity = getSynchroEntity(SynchroDataSource.PRIVATE_MESSAGE_CHANNEL);
      Long pmChannelTimestamp = synchroDataSource.getTimestamp(SynchroDataSource.PRIVATE_MESSAGE_CHANNEL);
      List<PrivateMessageChannelEntity> privateMessageChannelEntities =
          remoteMessageChannelDataSource.getPrivateMessageChannels(pmChannelTimestamp);
      privateMessageChannels = new ArrayList<>(
          privateMessageChannelEntityMapper.transform(privateMessageChannelEntities));
      privateMessageChannelDataSource.putPrivateMessages(privateMessageChannelEntities);
      synchroDataSource.putEntity(synchroEntity);
    } catch (ServerCommunicationException e) {
      e.printStackTrace();
    }
    return privateMessageChannels;
  }

  private SynchroEntity getSynchroEntity(String entity) {
    SynchroEntity synchroEntity = new SynchroEntity();
    synchroEntity.setEntity(entity);
    synchroEntity.setTimestamp(androidTimeUtils.getCurrentTime());
    return synchroEntity;
  }

}
