package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.mapper.PrivateMessageChannelEntityMapper;
import com.shootr.mobile.data.repository.datasource.privateMessage.PrivateMessageChannelDataSource;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannel;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.privateMessage.PrivateMessageChannelRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalPrivateMessageChannelRepository implements PrivateMessageChannelRepository {

  private final PrivateMessageChannelDataSource localPrivateMessageChannelDatasource;
  private final PrivateMessageChannelEntityMapper privateMessageChannelEntityMapper;

  @Inject public LocalPrivateMessageChannelRepository(
      @Local PrivateMessageChannelDataSource localPrivateMessageChannelDatasource,
      PrivateMessageChannelEntityMapper privateMessageChannelEntityMapper) {
    this.localPrivateMessageChannelDatasource = localPrivateMessageChannelDatasource;
    this.privateMessageChannelEntityMapper = privateMessageChannelEntityMapper;
  }

  @Override public List<PrivateMessageChannel> getPrivateMessageChannels() {
    return privateMessageChannelEntityMapper.transform(
        localPrivateMessageChannelDatasource.getPrivateMessageChannels(0L));
  }
}
