package com.shootr.mobile.domain.repository.privateMessage;

import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannel;
import java.util.List;

public interface PrivateMessageChannelRepository {

  List<PrivateMessageChannel> getPrivateMessageChannels();
}
