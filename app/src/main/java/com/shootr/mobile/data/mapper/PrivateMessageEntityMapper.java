package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.BaseMessagePollEntity;
import com.shootr.mobile.data.entity.EntitiesEntity;
import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.PrivateMessageEntity;
import com.shootr.mobile.data.entity.StreamIndexEntity;
import com.shootr.mobile.data.entity.UrlEntity;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Entities;
import com.shootr.mobile.domain.model.shot.Poll;
import com.shootr.mobile.domain.model.shot.StreamIndex;
import com.shootr.mobile.domain.model.shot.Url;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PrivateMessageEntityMapper {

  private final MetadataMapper metadataMapper;

  @Inject public PrivateMessageEntityMapper(MetadataMapper metadataMapper) {
    this.metadataMapper = metadataMapper;
  }

  public PrivateMessage transform(PrivateMessageEntity privateMessageEntity) {
    if (privateMessageEntity == null) {
      return null;
    }
    PrivateMessage privateMessage = new PrivateMessage();
    privateMessage.setIdPrivateMessage(privateMessageEntity.getIdPrivateMessage());
    privateMessage.setComment(privateMessageEntity.getComment());
    privateMessage.setImage(privateMessageEntity.getImage());
    privateMessage.setImageHeight(privateMessageEntity.getImageHeight());
    privateMessage.setImageWidth(privateMessageEntity.getImageWidth());
    privateMessage.setPublishDate(privateMessageEntity.getBirth());
    privateMessage.setIdPrivateMessageChannel(privateMessageEntity.getIdPrivateMessageChannel());

    BaseMessage.BaseMessageUserInfo userInfo = new BaseMessage.BaseMessageUserInfo();
    userInfo.setIdUser(privateMessageEntity.getIdUser());
    userInfo.setUsername(privateMessageEntity.getUsername());
    userInfo.setVerifiedUser(privateMessageEntity.getVerifiedUser());
    privateMessage.setUserInfo(userInfo);
    privateMessage.setVideoUrl(privateMessageEntity.getVideoUrl());
    privateMessage.setVideoTitle(privateMessageEntity.getVideoTitle());
    privateMessage.setVideoDuration(privateMessageEntity.getVideoDuration());
    privateMessage.setMetadata(metadataMapper.metadataFromEntity(privateMessageEntity));
    setupEntities(privateMessageEntity, privateMessage);
    privateMessage.setImageIdMedia(privateMessageEntity.getImageIdMedia());

    return privateMessage;
  }

  private void setupEntities(PrivateMessageEntity privateMessageEntity,
      PrivateMessage privateMessage) {
    if (privateMessageEntity.getEntities() != null) {
      Entities entities = new Entities();
      setupUrls(privateMessageEntity, entities);
      setupPolls(privateMessageEntity, entities);
      setupStreams(privateMessageEntity, entities);
      privateMessage.setEntities(entities);
    }
  }

  private void setupPolls(PrivateMessageEntity privateMessageEntity, Entities entities) {
    ArrayList<Poll> polls = new ArrayList<>();
    for (BaseMessagePollEntity baseMessagePollEntity : privateMessageEntity.getEntities()
        .getPolls()) {
      Poll poll = new Poll();
      poll.setPollQuestion(baseMessagePollEntity.getPollQuestion());
      poll.setIndices(baseMessagePollEntity.getIndices());
      poll.setIdPoll(baseMessagePollEntity.getIdPoll());
      polls.add(poll);
    }
    entities.setPolls(polls);
  }

  private void setupStreams(PrivateMessageEntity privateMessageEntity, Entities entities) {
    ArrayList<StreamIndex> streams = new ArrayList<>();
    for (StreamIndexEntity streamIndexEntity : privateMessageEntity.getEntities().getStreams()) {
      StreamIndex streamIndex = new StreamIndex();
      streamIndex.setStreamTitle(streamIndexEntity.getStreamTitle());
      streamIndex.setIndices(streamIndexEntity.getIndices());
      streamIndex.setIdStream(streamIndexEntity.getIdStream());
      streams.add(streamIndex);
    }
    entities.setStreams(streams);
  }

  private void setupUrls(PrivateMessageEntity privateMessageEntity, Entities entities) {
    ArrayList<Url> urls = new ArrayList<>();
    for (UrlEntity urlApiEntity : privateMessageEntity.getEntities().getUrls()) {
      Url url = new Url();
      url.setDisplayUrl(urlApiEntity.getDisplayUrl());
      url.setUrl(urlApiEntity.getUrl());
      url.setIndices(urlApiEntity.getIndices());
      urls.add(url);
    }

    entities.setUrls(urls);
  }

  public List<PrivateMessage> transform(List<PrivateMessageEntity> privateMessageEntities) {
    List<PrivateMessage> privateMessages = new ArrayList<>(privateMessageEntities.size());
    for (PrivateMessageEntity privateMessageEntity : privateMessageEntities) {
      PrivateMessage privateMessage = transform(privateMessageEntity);
      if (privateMessage != null) {
        privateMessages.add(privateMessage);
      }
    }
    return privateMessages;
  }

  public PrivateMessageEntity transform(PrivateMessage privateMessage) {
    if (privateMessage == null) {
      throw new IllegalArgumentException("PrivateMessage can't be null");
    }
    PrivateMessageEntity privateMessageEntity = new PrivateMessageEntity();
    privateMessageEntity.setIdPrivateMessage(privateMessage.getIdPrivateMessage());
    privateMessageEntity.setComment(privateMessage.getComment());
    privateMessageEntity.setImage(privateMessage.getImage());
    privateMessageEntity.setImageWidth(privateMessage.getImageWidth());
    privateMessageEntity.setImageHeight(privateMessage.getImageHeight());
    String idUser = privateMessage.getUserInfo().getIdUser();
    String username = privateMessage.getUserInfo().getUsername();
    String avatar = privateMessage.getUserInfo().getAvatar();
    Long verifiedUser = privateMessage.getUserInfo().getVerifiedUser();
    privateMessageEntity.setIdUser(idUser);
    privateMessageEntity.setUsername(username);
    privateMessageEntity.setVerifiedUser(verifiedUser);

    if (privateMessage.getIdTargetUser() != null) {
      privateMessageEntity.setIdTargetUser(privateMessage.getIdTargetUser());
    }
    privateMessageEntity.setUsername(privateMessage.getUserInfo().getUsername());
    privateMessageEntity.setBirth(privateMessage.getPublishDate());
    privateMessageEntity.setVideoUrl(privateMessage.getVideoUrl());
    privateMessageEntity.setVideoTitle(privateMessage.getVideoTitle());
    privateMessageEntity.setVideoDuration(privateMessage.getVideoDuration());
    privateMessageEntity.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
    metadataMapper.fillEntityWithMetadata(privateMessageEntity, privateMessage.getMetadata());
    setupEntities(privateMessage, privateMessageEntity);
    privateMessageEntity.setImageIdMedia(privateMessage.getImageIdMedia());

    return privateMessageEntity;
  }

  private void setupEntities(PrivateMessage privateMessage,
      PrivateMessageEntity privateMessageEntity) {
    if (privateMessage.getEntities() != null) {
      EntitiesEntity entitiesEntity = new EntitiesEntity();
      setupUrlsEntities(privateMessage, entitiesEntity);
      setupPollEntities(privateMessage, entitiesEntity);
      setupStreamEntities(privateMessage, entitiesEntity);
      privateMessageEntity.setEntities(entitiesEntity);
    }
  }

  private void setupStreamEntities(PrivateMessage privateMessage, EntitiesEntity entitiesEntity) {
    ArrayList<StreamIndexEntity> streamIndexEntities = new ArrayList<>();

    for (StreamIndex streamIndex : privateMessage.getEntities().getStreams()) {
      StreamIndexEntity streamIndexEntity = new StreamIndexEntity();
      streamIndexEntity.setStreamTitle(streamIndex.getStreamTitle());
      streamIndexEntity.setIdStream(streamIndex.getIdStream());
      streamIndexEntity.setIndices(streamIndex.getIndices());
      streamIndexEntities.add(streamIndexEntity);
    }
    entitiesEntity.setStreams(streamIndexEntities);
  }

  private void setupPollEntities(PrivateMessage privateMessage, EntitiesEntity entitiesEntity) {
    ArrayList<BaseMessagePollEntity> baseMessagePollEntities = new ArrayList<>();

    for (Poll poll : privateMessage.getEntities().getPolls()) {
      BaseMessagePollEntity baseMessagePollEntity = new BaseMessagePollEntity();
      baseMessagePollEntity.setPollQuestion(poll.getPollQuestion());
      baseMessagePollEntity.setIdPoll(poll.getIdPoll());
      baseMessagePollEntity.setIndices(poll.getIndices());
      baseMessagePollEntities.add(baseMessagePollEntity);
    }
    entitiesEntity.setPolls(baseMessagePollEntities);
  }

  private void setupUrlsEntities(PrivateMessage privateMessage, EntitiesEntity entitiesEntity) {
    ArrayList<UrlEntity> urlEntities = new ArrayList<>();
    for (Url urlApiEntity : privateMessage.getEntities().getUrls()) {
      UrlEntity urlEntity = new UrlEntity();
      urlEntity.setDisplayUrl(urlApiEntity.getDisplayUrl());
      urlEntity.setUrl(urlApiEntity.getUrl());
      urlEntity.setIndices(urlApiEntity.getIndices());
      urlEntities.add(urlEntity);
    }
    entitiesEntity.setUrls(urlEntities);
  }

  public List<PrivateMessageEntity> transformInEntities(List<PrivateMessage> privateMessages) {
    List<PrivateMessageEntity> privateMessageEntities = new ArrayList<>(privateMessages.size());
    for (PrivateMessage privateMessage : privateMessages) {
      privateMessageEntities.add(transform(privateMessage));
    }
    return privateMessageEntities;
  }
}
