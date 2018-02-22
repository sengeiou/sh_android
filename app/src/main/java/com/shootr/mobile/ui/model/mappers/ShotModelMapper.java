package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.ShotImageModel;
import com.shootr.mobile.ui.model.ShotModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShotModelMapper {

  private final SessionRepository sessionRepository;
  private final EntitiesModelMapper entitiesModelMapper;

  @Inject public ShotModelMapper(SessionRepository sessionRepository,
      EntitiesModelMapper entitiesModelMapper) {
    this.sessionRepository = sessionRepository;
    this.entitiesModelMapper = entitiesModelMapper;
  }

  public ShotModel transform(Shot shot) {
    if (shot == null) {
      return null;
    }
    ShotModel shotModel = new ShotModel();
    shotModel.setIdShot(shot.getIdShot());
    shotModel.setComment(shot.getComment());
    ShotImageModel shotImageModel = new ShotImageModel();
    if (shot.getImage() != null) {
      shotImageModel.setImageUrl(shot.getImage());
      shotImageModel.setImageHeight(shot.getImageHeight());
      shotImageModel.setImageWidth(shot.getImageWidth());
    }
    shotModel.setImage(shotImageModel);
    shotModel.setBirth(shot.getPublishDate());
    BaseMessage.BaseMessageUserInfo userInfo = shot.getUserInfo();
    shotModel.setUsername(userInfo.getUsername());
    shotModel.setIdUser(userInfo.getIdUser());
    shotModel.setAvatar(userInfo.getAvatar());
    Shot.ShotStreamInfo streamInfo = shot.getStreamInfo();
    if (streamInfo != null) {
      shotModel.setStreamId(streamInfo.getIdStream());
      shotModel.setStreamTitle(streamInfo.getStreamTitle());
    }
    shotModel.setReplyUsername(shot.getParentShotUsername());
    shotModel.setParentShotId(shot.getParentShotId());
    shotModel.setVideoUrl(shot.getVideoUrl());
    shotModel.setHasMedia(shotImageModel.getImageUrl() != null || shot.getVideoUrl() != null);
    shotModel.setVideoTitle(shot.getVideoTitle());
    shotModel.setVideoDuration(durationToText(shot.getVideoDuration()));
    shotModel.setNiceCount(shot.getNiceCount());
    shotModel.setHide(shot.getProfileHidden());
    shotModel.setReplyCount(shot.getReplyCount());
    shotModel.setLinkClickCount(shot.getLinkClicks() == null ? 0 : shot.getLinkClicks());
    shotModel.setViews(shot.getViews() == null ? 0 : shot.getViews());
    shotModel.setReshootCount(shot.getReshootCount() == null ? 0 : shot.getReshootCount());
    shotModel.setCtaButtonLink(shot.getCtaButtonLink());
    shotModel.setCtaButtonText(shot.getCtaButtonText());
    shotModel.setCtaCaption(shot.getCtaCaption());
    shotModel.setPromoted(shot.getPromoted());
    shotModel.setType(shot.getType());
    shotModel.setHolderOrContributor(shot.isFromContributor() || shot.isFromHolder());
    shotModel.setNiced(shot.isNiced());
    shotModel.setReshooted(shot.isReshooted());
    if (userInfo.getIdUser() != null) {
      shotModel.setMyshot(
          shot.getUserInfo().getIdUser().equals(sessionRepository.getCurrentUserId()));
    }
    if (userInfo.getVerifiedUser() != null) {
      shotModel.setVerifiedUser(userInfo.getVerifiedUser() == 1);
    }
    shotModel.setEntitiesModel(entitiesModelMapper.setupEntities(shot.getEntities()));
    shotModel.setTimelineFlags(shot.getTimelineFlags());
    shotModel.setDetailFlags(shot.getDetailFlags());
    if (shot.getMetadata() != null) {
      shotModel.setDeleted(shot.getMetadata().getDeleted() != null);
    }
    entitiesModelMapper.setupEntities(shot.getEntities());

    return shotModel;
  }

  private String durationToText(Long durationInSeconds) {
    if (durationInSeconds == null) {
      return null;
    }
    int minutes = (int) (durationInSeconds / 60);
    int seconds = (int) (durationInSeconds % 60);
    String secondsTwoDigits =
        seconds > 10 ? String.valueOf(seconds) : "0" + String.valueOf(seconds);
    return String.format("%d:%s", minutes, secondsTwoDigits);
  }

  public List<ShotModel> transform(List<Shot> shots) {
    List<ShotModel> shotModels = new ArrayList<>();
    for (Shot shot : shots) {
      shotModels.add(transform(shot));
    }
    return shotModels;
  }

  public List<ShotModel> transformPrintableItem(List<PrintableItem> shots) {
    List<ShotModel> shotModels = new ArrayList<>();
    for (PrintableItem shot : shots) {
      shotModels.add(transform((Shot) shot));
    }
    return shotModels;
  }
}
