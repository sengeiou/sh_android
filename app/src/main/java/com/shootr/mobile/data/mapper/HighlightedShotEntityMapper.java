package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.api.entity.BaseMessagePollApiEntity;
import com.shootr.mobile.data.api.entity.EmbedUserApiEntity;
import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.api.entity.StreamIndexApiEntity;
import com.shootr.mobile.data.api.entity.UrlApiEntity;
import com.shootr.mobile.data.entity.BaseMessagePollEntity;
import com.shootr.mobile.data.entity.EntitiesEntity;
import com.shootr.mobile.data.entity.HighlightedShotApiEntity;
import com.shootr.mobile.data.entity.HighlightedShotEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.StreamIndexEntity;
import com.shootr.mobile.data.entity.UrlEntity;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Entities;
import com.shootr.mobile.domain.model.shot.HighlightedShot;
import com.shootr.mobile.domain.model.shot.Poll;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.StreamIndex;
import com.shootr.mobile.domain.model.shot.Url;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class HighlightedShotEntityMapper {

  private final MetadataMapper metadataMapper;

  @Inject public HighlightedShotEntityMapper(MetadataMapper metadataMapper) {
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
    highlightedShot.setNiced(shotApiEntity.getNiced());
    highlightedShot.setNicedTime(shotApiEntity.getNicedTime());

    highlightedShot.setBirth(value.getBirth());
    highlightedShot.setModified(shotApiEntity.getModified());
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
    highlightedShot.setShareLink(shotApiEntity.getShareLink());

    highlightedShot.setNiced(shotApiEntity.getNiced());
    highlightedShot.setNicedTime(shotApiEntity.getNicedTime());
    highlightedShot.setReshooted(shotApiEntity.getReshooted());
    highlightedShot.setReshootedTime(shotApiEntity.getReshootedTime());
    setupEntities(shotApiEntity, highlightedShot);

    return highlightedShot;
  }

  private void setupEntities(ShotApiEntity shotApiEntity, HighlightedShotEntity shotEntity) {
    if (shotApiEntity.getEntities() != null) {
      EntitiesEntity entitiesEntity = new EntitiesEntity();
      setupUrls(shotApiEntity, entitiesEntity);
      setupStreams(shotApiEntity, entitiesEntity);
      setupPollsEntities(shotApiEntity, entitiesEntity);
      shotEntity.setEntities(entitiesEntity);
    }
  }

  private void setupStreams(ShotApiEntity shotApiEntity, EntitiesEntity entitiesEntity) {
    ArrayList<StreamIndexEntity> streamsEntities = new ArrayList<>();
    if (shotApiEntity.getEntities() != null) {
      for (StreamIndexApiEntity streamIndexApiEntity : shotApiEntity.getEntities().getStreams()) {
        StreamIndexEntity streamIndexEntity = new StreamIndexEntity();
        streamIndexEntity.setStreamTitle(streamIndexApiEntity.getStreamTitle());
        streamIndexEntity.setIdStream(streamIndexApiEntity.getIdStream());
        streamIndexEntity.setIndices(streamIndexApiEntity.getIndices());
        streamsEntities.add(streamIndexEntity);
      }
    }
    entitiesEntity.setStreams(streamsEntities);
  }

  private void setupPollsEntities(ShotApiEntity shotApiEntity, EntitiesEntity entitiesEntity) {
    ArrayList<BaseMessagePollEntity> pollEntities = new ArrayList<>();

    for (BaseMessagePollApiEntity baseMessagePollApiEntity : shotApiEntity.getEntities()
        .getPolls()) {
      BaseMessagePollEntity baseMessagePollEntity = new BaseMessagePollEntity();
      baseMessagePollEntity.setIdPoll(baseMessagePollApiEntity.getIdPoll());
      baseMessagePollEntity.setIndices(baseMessagePollApiEntity.getIndices());
      baseMessagePollEntity.setPollQuestion(baseMessagePollApiEntity.getPollQuestion());
      pollEntities.add(baseMessagePollEntity);
    }
    entitiesEntity.setPolls(pollEntities);
  }

  private void setupUrls(ShotApiEntity shotApiEntity, EntitiesEntity entitiesEntity) {
    ArrayList<UrlEntity> urlEntities = new ArrayList<>();
    if (shotApiEntity.getEntities() != null) {
      for (UrlApiEntity urlApiEntity : shotApiEntity.getEntities().getUrls()) {
        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setDisplayUrl(urlApiEntity.getDisplayUrl());
        urlEntity.setUrl(urlApiEntity.getUrl());
        urlEntity.setIndices(urlApiEntity.getIndices());
        urlEntities.add(urlEntity);
      }
    }
    entitiesEntity.setUrls(urlEntities);
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
    shot.setNiced(value.getNiced());
    shot.setNicedTime(value.getNicedTime() != null ? new Date(value.getNicedTime()) : null);
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
    shot.setReshooted(value.getReshooted());
    shot.setReshootedTime(
        value.getReshootedTime() != null ? new Date(value.getReshootedTime()) : new Date());
    shot.setNicedTime(value.getNicedTime() != null ? new Date(value.getNicedTime()) : new Date());
    shot.setNiced(value.getNiced());
    shot.setShareLink(value.getShareLink());
    setupEntities(shot, value);

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

  private void setupEntities(Shot shot, ShotEntity shotEntity) {
    if (shotEntity.getEntities() != null) {
      Entities entities = new Entities();
      setupUrls(shotEntity, entities);
      setupPolls(shotEntity, entities);
      setupStreams(shotEntity, entities);
      shot.setEntities(entities);
    }
  }

  private void setupStreams(ShotEntity shotEntity, Entities entities) {
    ArrayList<StreamIndex> streams = new ArrayList<>();
    for (StreamIndexEntity streamApiEntity : shotEntity.getEntities().getStreams()) {
      StreamIndex streamIndex = new StreamIndex();
      streamIndex.setIdStream(streamApiEntity.getIdStream());
      streamIndex.setStreamTitle(streamApiEntity.getStreamTitle());
      streamIndex.setIndices(streamApiEntity.getIndices());
      streams.add(streamIndex);
    }

    entities.setStreams(streams);
  }

  private void setupPolls(ShotEntity shotEntity, Entities entities) {
    ArrayList<Poll> polls = new ArrayList<>();

    for (BaseMessagePollEntity baseMessagePollEntity : shotEntity.getEntities().getPolls()) {
      Poll poll = new Poll();
      poll.setIdPoll(baseMessagePollEntity.getIdPoll());
      poll.setIndices(baseMessagePollEntity.getIndices());
      poll.setPollQuestion(baseMessagePollEntity.getPollQuestion());
      polls.add(poll);
    }
    entities.setPolls(polls);
  }

  private void setupUrls(ShotEntity shotEntity, Entities entities) {
    ArrayList<Url> urls = new ArrayList<>();
    for (UrlEntity urlApiEntity : shotEntity.getEntities().getUrls()) {
      Url url = new Url();
      url.setDisplayUrl(urlApiEntity.getDisplayUrl());
      url.setUrl(urlApiEntity.getUrl());
      url.setIndices(urlApiEntity.getIndices());
      urls.add(url);
    }

    entities.setUrls(urls);
  }
}
