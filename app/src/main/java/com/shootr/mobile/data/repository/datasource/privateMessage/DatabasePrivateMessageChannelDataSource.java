package com.shootr.mobile.data.repository.datasource.privateMessage;

import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import com.shootr.mobile.db.manager.PrivateMessageChannelManager;
import java.util.List;
import javax.inject.Inject;

public class DatabasePrivateMessageChannelDataSource implements PrivateMessageChannelDataSource {

  private final PrivateMessageChannelManager privateMessageChannelManager;

  @Inject public DatabasePrivateMessageChannelDataSource(
      PrivateMessageChannelManager privateMessageChannelManager) {
    this.privateMessageChannelManager = privateMessageChannelManager;
  }

  @Override
  public PrivateMessageChannelEntity getPrivateMessageChannelByIdUser(String idTargetUser) {
    return privateMessageChannelManager.getPrivateMessageChannelByIdUser(idTargetUser);
  }

  @Override
  public PrivateMessageChannelEntity getPrivateMessageChannelById(String idPrivateMessageChannel) {
    return privateMessageChannelManager.getPrivateMessageChannelById(idPrivateMessageChannel);
  }

  @Override
  public void putPrivateMessageChannel(PrivateMessageChannelEntity privateMessageChannelEntity) {
    privateMessageChannelManager.savePrivateMessageChannel(privateMessageChannelEntity);
  }

  @Override
  public void putPrivateMessages(List<PrivateMessageChannelEntity> privateMessageChannelEntities) {
    privateMessageChannelManager.savePrivateMessageChannels(privateMessageChannelEntities);
  }

  @Override public void removePrivateMessageChannel(String idPrivateMessageChannel) {
    privateMessageChannelManager.removePrivateMessageChannel(idPrivateMessageChannel);
  }

  @Override public List<PrivateMessageChannelEntity> getPrivateMessageChannels(Long timestamp) {
    return privateMessageChannelManager.getPrivateMessageChannels();
  }

  @Override public void sendRemovedPrivateMessageChannels() {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public List<PrivateMessageChannelEntity> getPrivateMessageChannelsNotSynchronized() {
    return privateMessageChannelManager.getPrivateMessageChannelsNotSynchronized();
  }

  @Override public void markPrivateMessageChannelDeleted(String channelId) {
    privateMessageChannelManager.markPrivateMessageChannelDeleted(channelId);
  }

  @Override public void setPrivateMessageChannelMuted(String idUser) {
    privateMessageChannelManager.setPrivateMessageChannelMuted(idUser);
  }

  @Override public void setPrivateMessageChannelUnMuted(String idUser) {
    privateMessageChannelManager.setPrivateMessageChannelUnMuted(idUser);
  }
}
