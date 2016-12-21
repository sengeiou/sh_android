package com.shootr.mobile.data.repository.datasource.privateMessage;

import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import java.util.List;

public interface PrivateMessageChannelDataSource {

  PrivateMessageChannelEntity getPrivateMessageChannelByIdUser(String idTargetUser);

  PrivateMessageChannelEntity getPrivateMessageChannelById(String idPrivateMessageChannel);

  void putPrivateMessageChannel(
      PrivateMessageChannelEntity privateMessageChannelEntity);

  void putPrivateMessages(List<PrivateMessageChannelEntity> privateMessageChannelEntities);

  List<PrivateMessageChannelEntity> getPrivateMessageChannels(Long timestamp);

}
