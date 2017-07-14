package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.Poll;
import com.shootr.mobile.domain.model.shot.StreamIndex;
import com.shootr.mobile.domain.model.shot.Url;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.BaseMessagePollModel;
import com.shootr.mobile.ui.model.EntitiesModel;
import com.shootr.mobile.ui.model.PrivateMessageModel;
import com.shootr.mobile.ui.model.ShotImageModel;
import com.shootr.mobile.ui.model.StreamIndexModel;
import com.shootr.mobile.ui.model.UrlModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PrivateMessageModelMapper {

  private final SessionRepository sessionRepository;

  @Inject public PrivateMessageModelMapper(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  public PrivateMessageModel transform(PrivateMessage privateMessage, String avatar) {

    PrivateMessageModel model = new PrivateMessageModel();

    model.setIdPrivateMessageChannel(privateMessage.getIdPrivateMessageChannel());
    model.setIdPrivateMessage(privateMessage.getIdPrivateMessage());
    model.setIdTargetUser(privateMessage.getIdTargetUser());
    model.setPublishDate(privateMessage.getPublishDate());

    model.setComment(privateMessage.getComment());
    if (privateMessage.getUserInfo().getIdUser().equals(sessionRepository.getCurrentUserId())) {
      model.setAvatar(sessionRepository.getCurrentUser().getPhoto());
    } else {
      model.setAvatar(avatar);
    }
    if (privateMessage.getUserInfo() != null) {
      model.setVerifiedUser(!((privateMessage.getUserInfo().getVerifiedUser() == null)
          || privateMessage.getUserInfo().getVerifiedUser() == 0)
          && privateMessage.getUserInfo().getVerifiedUser() == 1);
      model.setIdUser(privateMessage.getUserInfo().getIdUser());
      model.setUsername(privateMessage.getUserInfo().getUsername());
    }

    ShotImageModel shotImageModel = new ShotImageModel();
    shotImageModel.setImageUrl(privateMessage.getImage());
    shotImageModel.setImageHeight(privateMessage.getImageHeight());
    shotImageModel.setImageWidth(privateMessage.getImageWidth());
    model.setImage(shotImageModel);

    model.setVideoUrl(privateMessage.getVideoUrl());
    model.setVideoTitle(privateMessage.getVideoTitle());
    if (privateMessage.getVideoDuration() != null) {
      model.setVideoDuration(durationToText(privateMessage.getVideoDuration()));
    }

    model.setBirth(privateMessage.getPublishDate());

    setupEntities(privateMessage, model);

    return model;
  }

  private void setupEntities(PrivateMessage privateMessage, PrivateMessageModel model) {
    if (privateMessage.getEntities() != null) {
      EntitiesModel entitiesModel = new EntitiesModel();
      setupUrls(privateMessage, entitiesModel);
      setupPolls(privateMessage, entitiesModel);
      setupStreams(privateMessage, entitiesModel);
      model.setEntitiesModel(entitiesModel);
    }
  }

  private void setupStreams(PrivateMessage privateMessage, EntitiesModel entitiesModel) {
    ArrayList<StreamIndexModel> streamIndexModels = new ArrayList<>();
    for (StreamIndex streamIndex : privateMessage.getEntities().getStreams()) {
      StreamIndexModel streamIndexModel = new StreamIndexModel();
      streamIndexModel.setIndices(streamIndex.getIndices());
      streamIndexModel.setIdStream(streamIndex.getIdStream());
      streamIndexModel.setStreamTitle(streamIndex.getStreamTitle());
      streamIndexModels.add(streamIndexModel);
    }

    entitiesModel.setStreams(streamIndexModels);
  }

  private void setupPolls(PrivateMessage privateMessage, EntitiesModel entitiesModel) {
    ArrayList<BaseMessagePollModel> baseMessagePollModels = new ArrayList<>();
    for (Poll poll : privateMessage.getEntities().getPolls()) {
      BaseMessagePollModel baseMessagePollModel = new BaseMessagePollModel();
      baseMessagePollModel.setIndices(poll.getIndices());
      baseMessagePollModel.setPollQuestion(poll.getPollQuestion());
      baseMessagePollModel.setIdPoll(poll.getIdPoll());
      baseMessagePollModels.add(baseMessagePollModel);
    }

    entitiesModel.setPolls(baseMessagePollModels);
  }

  private void setupUrls(PrivateMessage privateMessage, EntitiesModel entitiesModel) {
    ArrayList<UrlModel> urlModels = new ArrayList<>();
    for (Url url : privateMessage.getEntities().getUrls()) {
      UrlModel urlModel = new UrlModel();
      urlModel.setDisplayUrl(url.getDisplayUrl());
      urlModel.setUrl(url.getUrl());
      urlModel.setIndices(url.getIndices());
      urlModels.add(urlModel);
    }

    entitiesModel.setUrls(urlModels);
  }

  private String durationToText(Long durationInSeconds) {
    if (durationInSeconds == null) {
      return null;
    }
    int minutes = (int) (durationInSeconds / 60);
    int seconds = (int) (durationInSeconds % 60);
    String secondsTwoDigits = seconds > 10 ? String.valueOf(seconds) : "0" + String.valueOf(seconds);
    return String.format("%d:%s", minutes, secondsTwoDigits);
  }

  public List<PrivateMessageModel> transform(List<PrivateMessage> privateMessages, String avatar) {
    List<PrivateMessageModel> models = new ArrayList<>(privateMessages.size());
    for (PrivateMessage privateMessage : privateMessages) {
      models.add(transform(privateMessage, avatar));
    }
    return models;
  }
}
