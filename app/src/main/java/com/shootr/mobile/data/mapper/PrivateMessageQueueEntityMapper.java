package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.PrivateMessageQueueEntity;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PrivateMessageQueueEntityMapper {

  @Inject public PrivateMessageQueueEntityMapper() {
  }

  public PrivateMessageQueueEntity transformPrivateMessageQueue(QueuedShot queuedShot) {
    if (queuedShot == null) {
      return null;
    }
    PrivateMessageQueueEntity entity = new PrivateMessageQueueEntity();
    entity.setIdQueue(queuedShot.getIdQueue());
    entity.setFailed(queuedShot.isFailed() ? 1 : 0);
    File imageFile = queuedShot.getImageFile();
    if (imageFile != null) {
      entity.setImageFile(imageFile.getAbsolutePath());
    }

    PrivateMessage privateMessage = (PrivateMessage) queuedShot.getBaseMessage();
    entity.setComment(privateMessage.getComment());
    entity.setImage(privateMessage.getImage());
    entity.setBirth(privateMessage.getPublishDate());

    entity.setIdUser(privateMessage.getUserInfo().getIdUser());
    entity.setUsername(privateMessage.getUserInfo().getUsername());

    entity.setVideoUrl(privateMessage.getVideoUrl());
    entity.setVideoTitle(privateMessage.getVideoTitle());
    entity.setVideoDuration(privateMessage.getVideoDuration());

    entity.setIdTargetUser(privateMessage.getIdTargetUser());
    return entity;
  }

  public QueuedShot transformPrivateMessageQueue(PrivateMessageQueueEntity entity) {
    if (entity == null) {
      return null;
    }
    QueuedShot queuedShot = new QueuedShot();
    queuedShot.setIdQueue(entity.getIdQueue());
    queuedShot.setFailed(entity.getFailed() == 1);
    String imageFile = entity.getImageFile();
    if (imageFile != null) {
      queuedShot.setImageFile(new File(imageFile));
    }

    PrivateMessage privateMessage = new PrivateMessage();
    privateMessage.setComment(entity.getComment());
    privateMessage.setImage(entity.getImage());
    privateMessage.setPublishDate(entity.getBirth());

    privateMessage.setIdQueue(entity.getIdQueue());

    BaseMessage.BaseMessageUserInfo userInfo = new BaseMessage.BaseMessageUserInfo();
    userInfo.setIdUser(entity.getIdUser());
    userInfo.setUsername(entity.getUsername());
    privateMessage.setUserInfo(userInfo);

    privateMessage.setVideoUrl(entity.getVideoUrl());
    privateMessage.setVideoTitle(entity.getVideoTitle());
    privateMessage.setVideoDuration(entity.getVideoDuration());

    privateMessage.setIdTargetUser(entity.getIdTargetUser());

    queuedShot.setBaseMessage(privateMessage);
    return queuedShot;
  }

  public List<QueuedShot> transformPrivateMessageQueue(
      List<PrivateMessageQueueEntity> privateMessageQueueEntities) {
    List<QueuedShot> results = new ArrayList<>(privateMessageQueueEntities.size());
    for (PrivateMessageQueueEntity privateMessageQueueEntity : privateMessageQueueEntities) {
      results.add(transformPrivateMessageQueue(privateMessageQueueEntity));
    }
    return results;
  }
}
