package com.shootr.mobile.data.repository.datasource.privateMessage;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.PrivateMessagesApiService;
import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServicePrivateMessageChannelDataSource implements PrivateMessageChannelDataSource {

  private final PrivateMessagesApiService privateMessagesApiService;

  @Inject
  public ServicePrivateMessageChannelDataSource(PrivateMessagesApiService privateMessagesApiService) {
    this.privateMessagesApiService = privateMessagesApiService;
  }

  @Override
  public PrivateMessageChannelEntity getPrivateMessageChannelByIdUser(String idTargetUser) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override
  public PrivateMessageChannelEntity getPrivateMessageChannelById(String idPrivateMessageChannel) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override
  public void putPrivateMessageChannel(PrivateMessageChannelEntity privateMessageChannelEntity) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override
  public void putPrivateMessages(List<PrivateMessageChannelEntity> privateMessageChannelEntities) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public List<PrivateMessageChannelEntity> getPrivateMessageChannels(Long modifiedTimestamp) {
    try {
      return privateMessagesApiService.getPrivateMessageChannel(modifiedTimestamp);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }
}
