package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import com.shootr.mobile.data.mapper.PrivateMessageChannelEntityMapper;
import com.shootr.mobile.data.repository.datasource.SynchroDataSource;
import com.shootr.mobile.data.repository.datasource.privateMessage.PrivateMessageChannelDataSource;
import com.shootr.mobile.data.repository.datasource.privateMessage.PrivateMessageDataSource;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.util.AndroidTimeUtils;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SyncPrivateMessageChannelRepositoryTest {

  public static final String ID_CHANNEL = "channel";
  public static final String ID_USER = "idUser";

  @Mock StreamDataSource localStreamDataSource;
  @Mock PrivateMessageChannelDataSource localPrivateMessageChannelDataSource;
  @Mock PrivateMessageChannelDataSource remotePrivateMessageChannelDataSource;
  @Mock PrivateMessageChannelEntityMapper privateMessageChannelEntityMapper;
  @Mock SynchroDataSource synchroDataSource;
  @Mock AndroidTimeUtils androidTimeUtils;

  private RemotePrivateMessageChannelRepository remotePrivateMessageChannelRepository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    remotePrivateMessageChannelRepository =
        new RemotePrivateMessageChannelRepository(localPrivateMessageChannelDataSource,
            remotePrivateMessageChannelDataSource, privateMessageChannelEntityMapper,
            synchroDataSource, androidTimeUtils);
  }

  @Test public void shouldPutPMChannelsInLocalWhenGetRemotePMChannels() throws Exception {
    when(remotePrivateMessageChannelDataSource.getPrivateMessageChannels(anyLong())).thenReturn(
        Collections.singletonList(privateMessageChannelEntity()));

    remotePrivateMessageChannelRepository.getPrivateMessageChannels();

    verify(localPrivateMessageChannelDataSource).putPrivateMessages(anyList());
  }

  private PrivateMessageChannelEntity privateMessageChannelEntity() {
    PrivateMessageChannelEntity privateMessageChannelEntity = new PrivateMessageChannelEntity();
    privateMessageChannelEntity.setIdTargetUser(ID_USER);
    privateMessageChannelEntity.setIdPrivateMessageChannel(ID_CHANNEL);
    return privateMessageChannelEntity;
  }
}
