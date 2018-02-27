package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.ShotDetailEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotDetail;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class ShotEntityMapper {

  private final MetadataMapper metadataMapper;
  private final EntitiesEntityMapper entitiesEntityMapper;

  @Inject public ShotEntityMapper(MetadataMapper metadataMapper,
      EntitiesEntityMapper entitiesEntityMapper) {
    this.metadataMapper = metadataMapper;
    this.entitiesEntityMapper = entitiesEntityMapper;
  }

  public Shot transform(ShotEntity shotEntity) {
    if (shotEntity == null) {
      return null;
    }
    Shot shot = new Shot();
    shot.setIdShot(shotEntity.getIdShot());
    shot.setComment(shotEntity.getComment());
    shot.setImage(shotEntity.getImage());
    shot.setImageHeight(shotEntity.getImageHeight());
    shot.setImageWidth(shotEntity.getImageWidth());
    shot.setPublishDate(shotEntity.getBirth());
    if (shotEntity.getIdStream() != null) {
      Shot.ShotStreamInfo eventInfo = new Shot.ShotStreamInfo();
      eventInfo.setIdStream(shotEntity.getIdStream());
      eventInfo.setStreamTitle(shotEntity.getStreamTitle());
      shot.setStreamInfo(eventInfo);
    }
    BaseMessage.BaseMessageUserInfo userInfo = new BaseMessage.BaseMessageUserInfo();
    userInfo.setIdUser(shotEntity.getIdUser());
    userInfo.setUsername(shotEntity.getUsername());
    userInfo.setAvatar(shotEntity.getUserPhoto());
    userInfo.setVerifiedUser(shotEntity.getVerifiedUser());
    shot.setUserInfo(userInfo);
    shot.setParentShotId(shotEntity.getIdShotParent());
    shot.setParentShotUserId(shotEntity.getIdUserParent());
    shot.setParentShotUsername(shotEntity.getUserNameParent());
    shot.setVideoUrl(shotEntity.getVideoUrl());
    shot.setVideoTitle(shotEntity.getVideoTitle());
    shot.setVideoDuration(shotEntity.getVideoDuration());
    shot.setType(shotEntity.getType());
    shot.setNiceCount(shotEntity.getNiceCount());
    shot.setNiced(shotEntity.getNiced());
    shot.setNicedTime(
        shotEntity.getNicedTime() != null ? new Date(shotEntity.getNicedTime()) : null);
    shot.setProfileHidden(shotEntity.getProfileHidden());
    shot.setMetadata(metadataMapper.metadataFromEntity(shotEntity));
    shot.setReplyCount(shotEntity.getReplyCount());
    shot.setViews(shotEntity.getViews());
    shot.setLinkClicks(shotEntity.getLinkClicks());
    shot.setReshootCount(shotEntity.getReshootCounter());
    shot.setCtaButtonLink(shotEntity.getCtaButtonLink());
    shot.setCtaButtonText(shotEntity.getCtaButtonText());
    shot.setCtaCaption(shotEntity.getCtaCaption());
    shot.setPromoted(shotEntity.getPromoted());
    shot.setPadding(shotEntity.isPadding() == null || shotEntity.isPadding() == 1);
    shot.setIsHolder(shotEntity.isFromHolder() != null && shotEntity.isFromHolder() == 1);
    shot.setIsContributor(
        shotEntity.isFromContributor() != null && shotEntity.isFromContributor() == 1);
    shot.setNiced(shotEntity.getNiced());
    shot.setNicedTime(
        shotEntity.getNicedTime() != null ? new Date(shotEntity.getNicedTime()) : null);
    shot.setReshooted(shotEntity.getReshooted());
    shot.setReshootedTime(
        shotEntity.getReshootedTime() != null ? new Date(shotEntity.getReshootedTime()) : null);
    shot.setEntities(entitiesEntityMapper.setupEntities(shotEntity.getEntities()));
    shot.setTimelineFlags(shotEntity.getTimelineFlags());
    shot.setDetailFlags(shotEntity.getDetailFlags());
    shot.setImageIdMedia(shotEntity.getImageIdMedia());
    shot.setShareLink(shotEntity.getShareLink());

    return shot;
  }

  public List<Shot> transform(List<ShotEntity> shotEntities) {
    List<Shot> shots = new ArrayList<>(shotEntities.size());
    for (ShotEntity shotEntity : shotEntities) {
      Shot shot = transform(shotEntity);
      if (shot != null) {
        shots.add(shot);
      }
    }
    return shots;
  }

  public ShotEntity transform(Shot shot) {
    if (shot == null) {
      throw new IllegalArgumentException("Shot can't be null");
    }
    ShotEntity shotEntity = new ShotEntity();
    String idShot = shot.getIdShot();
    shotEntity.setIdShot(idShot);
    shotEntity.setComment(shot.getComment());
    shotEntity.setImage(shot.getImage());
    shotEntity.setImageWidth(shot.getImageWidth());
    shotEntity.setImageHeight(shot.getImageHeight());
    shotEntity.setType(shot.getType());
    String idUser = shot.getUserInfo().getIdUser();
    String username = shot.getUserInfo().getUsername();
    String avatar = shot.getUserInfo().getAvatar();
    Long verifiedUser = shot.getUserInfo().getVerifiedUser();
    shotEntity.setIdUser(idUser);
    shotEntity.setUsername(username);
    shotEntity.setUserPhoto(avatar);
    shotEntity.setVerifiedUser(verifiedUser);
    Shot.ShotStreamInfo eventInfo = shot.getStreamInfo();
    if (eventInfo != null) {
      shotEntity.setIdStream(eventInfo.getIdStream());
      shotEntity.setStreamTitle(eventInfo.getStreamTitle());
    }
    shotEntity.setIdShotParent(shot.getParentShotId());
    shotEntity.setIdUserParent(shot.getParentShotUserId());
    shotEntity.setUserNameParent(shot.getParentShotUsername());
    shotEntity.setUsername(shot.getUserInfo().getUsername());
    shotEntity.setBirth(shot.getPublishDate());
    shotEntity.setVideoUrl(shot.getVideoUrl());
    shotEntity.setVideoTitle(shot.getVideoTitle());
    shotEntity.setVideoDuration(shot.getVideoDuration());
    shotEntity.setNiceCount(shot.getNiceCount());
    shotEntity.setProfileHidden(shot.getProfileHidden());
    shotEntity.setReplyCount(shot.getReplyCount());
    shotEntity.setViews(shot.getViews());
    shotEntity.setLinkClicks((shot.getLinkClicks()));
    shotEntity.setReshootCounter(shot.getReshootCount());
    shotEntity.setCtaButtonLink(shot.getCtaButtonLink());
    shotEntity.setCtaButtonText(shot.getCtaButtonText());
    shotEntity.setCtaCaption(shot.getCtaCaption());
    shotEntity.setPromoted(shot.getPromoted());
    if (shot.isFromContributor()) {
      shotEntity.setFromContributor(1);
    } else {
      shotEntity.setFromContributor(0);
    }
    shotEntity.setFromHolder(shot.isFromHolder() ? 1 : 0);
    shotEntity.setNiced(shot.isNiced());
    shotEntity.setReshooted(shot.isReshooted());
    shotEntity.setNicedTime(shot.getNicedTime() != null ? shot.getNicedTime().getTime() : 0L);
    shotEntity.setReshootedTime(
        shot.getReshootedTime() != null ? shot.getReshootedTime().getTime() : 0L);
    shotEntity.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
    metadataMapper.fillEntityWithMetadata(shotEntity, shot.getMetadata());
    shotEntity.setEntities(entitiesEntityMapper.setupEntitiesEntity(shot.getEntities()));
    shotEntity.setTimelineFlags(shot.getTimelineFlags());
    shotEntity.setDetailFlags(shot.getDetailFlags());
    shotEntity.setImageIdMedia(shot.getImageIdMedia());
    shotEntity.setShareLink(shot.getShareLink());
    return shotEntity;
  }

  public ShotDetail transform(ShotDetailEntity shotDetailEntity) {
    if (shotDetailEntity == null) {
      return null;
    }
    ShotDetail shotDetail = new ShotDetail();

    shotDetail.setShot(transform(shotDetailEntity.getShot()));
    shotDetail.setParentShot(transform(shotDetailEntity.getParentShot()));
    shotDetail.setReplies(transform(shotDetailEntity.getReplies()));
    if (shotDetailEntity.getParents() != null) {
      shotDetail.setParents(transform(shotDetailEntity.getParents()));
    } else {
      shotDetail.setParents(Collections.<Shot>emptyList());
    }
    return shotDetail;
  }

  public List<ShotEntity> transformInEntities(List<Shot> shots) {
    List<ShotEntity> shotEntities = new ArrayList<>(shots.size());
    for (Shot shot : shots) {
      shotEntities.add(transform(shot));
    }
    return shotEntities;
  }
}
