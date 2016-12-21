package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.PrivateMessageModel;
import com.shootr.mobile.ui.model.ShotImageModel;
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

    return model;
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
