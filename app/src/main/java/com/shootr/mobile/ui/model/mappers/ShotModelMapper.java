package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.Url;
import com.shootr.mobile.ui.model.EntitiesModel;
import com.shootr.mobile.ui.model.ShotImageModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.UrlModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShotModelMapper {

  @Inject public ShotModelMapper() {
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
    shotModel.setVideoTitle(shot.getVideoTitle());
    shotModel.setVideoDuration(durationToText(shot.getVideoDuration()));
    shotModel.setNiceCount(shot.getNiceCount());
    shotModel.setIsMarkedAsNice(shot.isMarkedAsNice());
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
    if (userInfo.getVerifiedUser() != null) {
      shotModel.setVerifiedUser(userInfo.getVerifiedUser() == 1);
    }
    setupEntities(shot, shotModel);

    return shotModel;
  }

  private void setupEntities(Shot shot, ShotModel shotModel) {
    if (shot.getEntities() != null) {
      ArrayList<UrlModel> urlModels = new ArrayList<>();
      for (Url url : shot.getEntities().getUrls()) {
        UrlModel urlModel = new UrlModel();
        urlModel.setDisplayUrl(url.getDisplayUrl());
        urlModel.setUrl(url.getUrl());
        urlModel.setIndices(url.getIndices());
        urlModels.add(urlModel);
      }
      EntitiesModel entitiesModel = new EntitiesModel();
      entitiesModel.setUrls(urlModels);
      shotModel.setEntitiesModel(entitiesModel);
    }
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
}
