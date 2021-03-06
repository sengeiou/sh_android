package com.shootr.mobile.data.repository.datasource.privateMessage;

import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import java.util.List;

public interface PrivateMessageChannelDataSource {

  PrivateMessageChannelEntity getPrivateMessageChannelByIdUser(String idTargetUser);

  PrivateMessageChannelEntity getPrivateMessageChannelById(String idPrivateMessageChannel);

  void putPrivateMessageChannel(
      PrivateMessageChannelEntity privateMessageChannelEntity);

  void putPrivateMessages(List<PrivateMessageChannelEntity> privateMessageChannelEntities);

  void removePrivateMessageChannel(String idPrivateMessageChannel);

  List<PrivateMessageChannelEntity> getPrivateMessageChannels(Long timestamp);

  void sendRemovedPrivateMessageChannels();

  List<PrivateMessageChannelEntity> getPrivateMessageChannelsNotSynchronized();

  void markPrivateMessageChannelDeleted(String channelId);

  void setPrivateMessageChannelMuted(String idUser);

  void setPrivateMessageChannelUnMuted(String idUser);
}
