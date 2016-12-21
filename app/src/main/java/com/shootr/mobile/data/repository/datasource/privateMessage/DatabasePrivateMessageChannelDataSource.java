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

  @Override public List<PrivateMessageChannelEntity> getPrivateMessageChannels(Long timestamp) {
    return privateMessageChannelManager.getPrivateMessageChannels();
  }
}
