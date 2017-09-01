package com.shootr.mobile.data.repository.datasource.privateMessage;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.PrivateMessagesApiService;
import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.Local;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServicePrivateMessageChannelDataSource implements PrivateMessageChannelDataSource {

  private final PrivateMessagesApiService privateMessagesApiService;
  private final PrivateMessageChannelDataSource localPrivateMessageChannelDataSource;

  @Inject
  public ServicePrivateMessageChannelDataSource(PrivateMessagesApiService privateMessagesApiService,
      @Local PrivateMessageChannelDataSource localPrivateMessageChannelDataSource) {
    this.privateMessagesApiService = privateMessagesApiService;
    this.localPrivateMessageChannelDataSource = localPrivateMessageChannelDataSource;
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

  @Override
  public List<PrivateMessageChannelEntity> getPrivateMessageChannels(Long modifiedTimestamp) {
    try {
      return privateMessagesApiService.getPrivateMessageChannel(modifiedTimestamp);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void sendRemovedPrivateMessageChannels() {
    List<PrivateMessageChannelEntity> removedPrivateMessageChannels =
        localPrivateMessageChannelDataSource.getPrivateMessageChannelsNotSynchronized();
    for (PrivateMessageChannelEntity privateMessageChannelEntity : removedPrivateMessageChannels) {
      try {
        privateMessagesApiService.removePrivateMessageChannel(
            privateMessageChannelEntity.getIdPrivateMessageChannel());
      } catch (IOException | ApiException e) {
        throw new ServerCommunicationException(e);
      }
    }
  }

  @Override public List<PrivateMessageChannelEntity> getPrivateMessageChannelsNotSynchronized() {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public void markPrivateMessageChannelDeleted(String channelId) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public void setPrivateMessageChannelMuted(String idUser) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public void setPrivateMessageChannelUnMuted(String idUser) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public void removePrivateMessageChannel(String idPrivateMessageChannel) {
    try {
      privateMessagesApiService.removePrivateMessageChannel(idPrivateMessageChannel);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }
}
