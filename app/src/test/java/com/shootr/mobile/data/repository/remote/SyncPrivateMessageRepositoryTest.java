package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.api.entity.PrivateMessageTimelineEntity;
import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import com.shootr.mobile.data.entity.PrivateMessageEntity;
import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.PrivateMessageChannelEntityMapper;
import com.shootr.mobile.data.mapper.PrivateMessageEntityMapper;
import com.shootr.mobile.data.mapper.PrivateMessageTimelineEntityMapper;
import com.shootr.mobile.data.repository.datasource.privateMessage.PrivateMessageChannelDataSource;
import com.shootr.mobile.data.repository.datasource.privateMessage.PrivateMessageDataSource;
import com.shootr.mobile.data.repository.datasource.user.UserDataSource;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SyncPrivateMessageRepositoryTest {

  private static final String ID_PRIVATE_MESSAGE = "idPrivateMessage";
  private static final String PHOTO = "photo";
  private static final String ID_USER = "idUser";
  private static final String CHANNEL = "channel";

  @Mock PrivateMessageDataSource remotePrivateMessageDataSource;
  @Mock PrivateMessageDataSource localPrivateMessageDataSource;
  @Mock PrivateMessageChannelDataSource localPrivateMessageChannelDataSource;
  @Mock PrivateMessageEntityMapper privateMessageEntityMapper;
  @Mock UserDataSource userDataSource;
  @Mock PrivateMessageTimelineEntityMapper privateMessageTimelineEntityMapper;
  @Mock PrivateMessageChannelEntityMapper privateMessageChannelEntityMapper;

  private RemotePrivateMessageRepository remotePrivateMessageRepository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    remotePrivateMessageRepository =
        new RemotePrivateMessageRepository(remotePrivateMessageDataSource,
            localPrivateMessageDataSource, localPrivateMessageChannelDataSource,
            privateMessageEntityMapper, privateMessageTimelineEntityMapper,
            privateMessageChannelEntityMapper, userDataSource);
  }

  @Test public void shouldPutPrivateMessageInRemoteWhenDataSourceWhenCallPutPM() throws Exception {
    when(userDataSource.getUser(ID_USER)).thenReturn(userEntity());
    when(remotePrivateMessageDataSource.putPrivateMessage(
        any(PrivateMessageEntity.class))).thenReturn(privateMessageEntity());

    remotePrivateMessageRepository.putPrivateMessage(privateMessage());

    verify(remotePrivateMessageDataSource).putPrivateMessage(any(PrivateMessageEntity.class));
  }

  @Test public void shouldPutPrivateMessagesInLocalWhenGetPMsForStreamChannel() throws Exception {
    when(remotePrivateMessageDataSource.getPrivateMessageTimelineByIdUser(ID_USER)).thenReturn(
        privateMessageTimelineEntity());

    remotePrivateMessageRepository.getPrivateMessageTimeline(ID_USER);

    verify(localPrivateMessageDataSource).putPrivateMessages(anyList());
  }

  private UserEntity userEntity() {
    UserEntity userEntity = new UserEntity();
    userEntity.setIdUser(ID_USER);
    userEntity.setPhoto(PHOTO);
    return userEntity;
  }

  private PrivateMessageEntity privateMessageEntity() {
    PrivateMessageEntity privateMessageEntity = new PrivateMessageEntity();
    privateMessageEntity.setIdPrivateMessage(ID_PRIVATE_MESSAGE);
    privateMessageEntity.setIdUser(ID_USER);
    privateMessageEntity.setSynchronizedStatus(Synchronized.SYNC_NEW);
    return privateMessageEntity;
  }

  private PrivateMessage privateMessage() {
    return new PrivateMessage();
  }

  private PrivateMessageChannelEntity privateMessageChannelEntity() {
    PrivateMessageChannelEntity privateMessageChannelEntity = new PrivateMessageChannelEntity();
    privateMessageChannelEntity.setTitle(CHANNEL);
    return privateMessageChannelEntity;
  }

  private PrivateMessageTimelineEntity privateMessageTimelineEntity() {
    PrivateMessageTimelineEntity privateMessageTimelineEntity = new PrivateMessageTimelineEntity();
    privateMessageTimelineEntity.setChannel(privateMessageChannelEntity());
    return privateMessageTimelineEntity;
  }
}
