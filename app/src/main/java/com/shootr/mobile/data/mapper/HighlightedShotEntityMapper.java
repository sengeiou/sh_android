package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.api.entity.EmbedUserApiEntity;
import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.entity.HighlightedShotApiEntity;
import com.shootr.mobile.data.entity.HighlightedShotEntity;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.HighlightedShot;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.repository.nice.InternalNiceShotRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class HighlightedShotEntityMapper {

  private final InternalNiceShotRepository niceShotRepository;
  private final MetadataMapper metadataMapper;

  @Inject public HighlightedShotEntityMapper(InternalNiceShotRepository niceShotRepository,
      MetadataMapper metadataMapper) {
    this.niceShotRepository = niceShotRepository;
    this.metadataMapper = metadataMapper;
  }

  public HighlightedShotEntity transform(HighlightedShotApiEntity value) {
    HighlightedShotEntity highlightedShot = new HighlightedShotEntity();

    highlightedShot.setActive(value.getActive());
    highlightedShot.setIdHighlightedShot(value.getIdHighlightedShot());

    ShotApiEntity shotApiEntity = value.getShot();

    highlightedShot.setIdShot(shotApiEntity.getIdShot());
    highlightedShot.setComment(shotApiEntity.getComment());
    highlightedShot.setImage(shotApiEntity.getImage());
    highlightedShot.setImageHeight(shotApiEntity.getImageHeight());
    highlightedShot.setImageWidth(shotApiEntity.getImageWidth());
    highlightedShot.setType(shotApiEntity.getType());

    EmbedUserApiEntity userApiEntity = shotApiEntity.getUser();
    highlightedShot.setIdUser(userApiEntity.getIdUser());
    highlightedShot.setUsername(userApiEntity.getUserName());
    highlightedShot.setUserPhoto(userApiEntity.getPhoto());

    highlightedShot.setIdShotParent(shotApiEntity.getIdShotParent());
    highlightedShot.setIdUserParent(shotApiEntity.getIdUserParent());
    highlightedShot.setUserNameParent(shotApiEntity.getUserNameParent());

    highlightedShot.setIdStream(shotApiEntity.getIdStream());
    highlightedShot.setStreamTitle(shotApiEntity.getStreamTitle());

    highlightedShot.setVideoUrl(shotApiEntity.getVideoUrl());
    highlightedShot.setVideoTitle(shotApiEntity.getVideoTitle());
    highlightedShot.setVideoDuration(shotApiEntity.getVideoDuration());

    Integer niceCount = shotApiEntity.getNiceCount();
    highlightedShot.setNiceCount(niceCount != null ? niceCount : 0);

    highlightedShot.setBirth(value.getBirth());
    highlightedShot.setModified(new Date(shotApiEntity.getModified()));
    highlightedShot.setRevision(value.getRevision());

    highlightedShot.setProfileHidden(shotApiEntity.getProfileHidden());
    highlightedShot.setReplyCount(shotApiEntity.getReplyCount());
    highlightedShot.setViews((shotApiEntity.getViews()));
    highlightedShot.setLinkClicks(shotApiEntity.getLinkClicks());
    highlightedShot.setReshootCounter(shotApiEntity.getReshootCount());
    highlightedShot.setPromoted(
        shotApiEntity.getPromoted() != null ? shotApiEntity.getPromoted() : 0L);
    highlightedShot.setCtaButtonLink(shotApiEntity.getCtaButtonLink());
    highlightedShot.setCtaButtonText(shotApiEntity.getCtaButtonText());
    highlightedShot.setCtaCaption(shotApiEntity.getCtaCaption());
    highlightedShot.setVerifiedUser(shotApiEntity.getVerifiedUser());

    return highlightedShot;
  }

  public List<HighlightedShotEntity> transform(List<HighlightedShotApiEntity> value) {
    ArrayList<HighlightedShotEntity> highlightedShotEntities = new ArrayList<>(value.size());
    for (HighlightedShotApiEntity highlightedShotApiEntity : value) {
      highlightedShotEntities.add(transform(highlightedShotApiEntity));
    }
    return highlightedShotEntities;
  }

  public HighlightedShot mapToDomain(HighlightedShotEntity value) {
    if (value == null) {
      return null;
    }

    HighlightedShot highlightedShot = new HighlightedShot();

    highlightedShot.setIdHighlightedShot(value.getIdHighlightedShot());
    highlightedShot.setActive(value.getActive() != 0L);
    highlightedShot.setVisible(value.isVisible() != null ? value.isVisible() : true);

    Shot shot = new Shot();

    shot.setIdShot(value.getIdShot());
    shot.setComment(value.getComment());
    shot.setImage(value.getImage());
    shot.setImageHeight(value.getImageHeight());
    shot.setImageWidth(value.getImageWidth());
    shot.setPublishDate(value.getBirth());
    if (value.getIdStream() != null) {
      Shot.ShotStreamInfo eventInfo = new Shot.ShotStreamInfo();
      eventInfo.setIdStream(value.getIdStream());
      eventInfo.setStreamTitle(value.getStreamTitle());
      shot.setStreamInfo(eventInfo);
    }
    BaseMessage.BaseMessageUserInfo userInfo = new BaseMessage.BaseMessageUserInfo();
    userInfo.setIdUser(value.getIdUser());
    userInfo.setUsername(value.getUsername());
    userInfo.setAvatar(value.getUserPhoto());
    userInfo.setVerifiedUser(value.getVerifiedUser());
    shot.setUserInfo(userInfo);
    shot.setParentShotId(value.getIdShotParent());
    shot.setParentShotUserId(value.getIdUserParent());
    shot.setParentShotUsername(value.getUserNameParent());
    shot.setVideoUrl(value.getVideoUrl());
    shot.setVideoTitle(value.getVideoTitle());
    shot.setVideoDuration(value.getVideoDuration());
    shot.setType(value.getType());
    shot.setNiceCount(value.getNiceCount());
    shot.setIsMarkedAsNice(niceShotRepository.isMarked(shot.getIdShot()));
    shot.setProfileHidden(value.getProfileHidden());
    shot.setMetadata(metadataMapper.metadataFromEntity(value));
    shot.setReplyCount(value.getReplyCount());
    shot.setViews(value.getViews());
    shot.setLinkClicks(value.getLinkClicks());
    shot.setReshootCount(value.getReshootCounter());
    shot.setCtaButtonLink(value.getCtaButtonLink());
    shot.setCtaButtonText(value.getCtaButtonText());
    shot.setCtaCaption(value.getCtaCaption());
    shot.setPromoted(value.getPromoted());

    highlightedShot.setShot(shot);

    return highlightedShot;
  }

  public List<HighlightedShot> transformToDomain(List<HighlightedShotEntity> value) {
    ArrayList<HighlightedShot> highlightedShotEntities = new ArrayList<>(value.size());
    for (HighlightedShotEntity highlightedShotApiEntity : value) {
      highlightedShotEntities.add(mapToDomain(highlightedShotApiEntity));
    }
    return highlightedShotEntities;
  }
}
