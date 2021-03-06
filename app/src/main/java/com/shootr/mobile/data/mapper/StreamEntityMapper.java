package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class StreamEntityMapper {

  private static final int CAN_WRITE = 1;
  private static final int CAN_REPLY = 2;
  private static final int CAN_H1_ITEM = 4;
  private static final int CAN_H2_ITEM = 8;
  private static final int CAN_SEND_PROMOTED = 16;
  private static final int CAN_TOGGLE_PROMOTED = 32;
  private static final int CAN_SHOW_PROMOTED_INFO = 64;

  private final UserEntityMapper userEntityMapper;
  private final SessionRepository sessionRepository;

  @Inject public StreamEntityMapper(UserEntityMapper userEntityMapper,
      SessionRepository sessionRepository) {
    this.userEntityMapper = userEntityMapper;
    this.sessionRepository = sessionRepository;
  }

  public Stream transform(StreamEntity streamEntity) {
    if (streamEntity == null) {
      return null;
    }
    Stream stream = new Stream();
    stream.setId(streamEntity.getIdStream());
    stream.setAuthorId(streamEntity.getIdUser());
    stream.setTitle(streamEntity.getTitle());
    stream.setPicture(streamEntity.getPhoto());
    stream.setLandscapePicture(streamEntity.getLandscapePhoto());
    stream.setAuthorUsername(streamEntity.getUserName());
    stream.setCountry(streamEntity.getCountry());
    stream.setDescription(streamEntity.getDescription());
    stream.setTopic(streamEntity.getTopic());
    stream.setMediaCount(streamEntity.getMediaCountByRelatedUsers());
    stream.setRemoved(streamEntity.getRemoved() == 1);
    if (streamEntity.getWatchers() != null) {
      stream.setWatchers(userEntityMapper.transformEntities(streamEntity.getWatchers()));
    }
    stream.setTotalFollowers(
        streamEntity.getTotalFavorites() != null ? streamEntity.getTotalFavorites().intValue() : 0);
    stream.setTotalWatchers(
        streamEntity.getTotalWatchers() != null ? streamEntity.getTotalWatchers().intValue() : 0);
    stream.setReadWriteMode(streamEntity.getReadWriteMode());
    stream.setVerifiedUser(
        (streamEntity.getVerifiedUser() != null) && (streamEntity.getVerifiedUser() == 1));
    stream.setStrategic(streamEntity.isStrategic());
    stream.setContributorCount(streamEntity.getContributorCount());
    if (streamEntity.getIdUserContributors() != null) {
      stream.setCurrentUserContributor(
          streamEntity.getIdUserContributors().contains(sessionRepository.getCurrentUserId()));
    } else {
      stream.setCurrentUserContributor(false);
    }

    stream.setTotalFollowingWatchers(streamEntity.getTotalFollowingWatchers());
    if (streamEntity.isFollowing() != null) {
      stream.setFollowing(streamEntity.isFollowing());
    }
    if (streamEntity.isMuted() != null) {
      stream.setMuted(streamEntity.isMuted());
    }
    stream.setPhotoIdMedia(streamEntity.getPhotoIdMedia());
    stream.setViews(streamEntity.getViews());
    setPermissionsInBoolean(streamEntity.getPermissions(), stream);
    stream.setLastTimeShooted(streamEntity.getLastTimeShooted());
    stream.setShareLink(streamEntity.getShareLink());
    stream.setVideoUrl(streamEntity.getVideoUrl());
    stream.setPromotedShotsEnabled(streamEntity.getPromotedShotsEnabled());
    return stream;
  }

  public Stream transform(StreamEntity streamEntity, boolean isFavorite) {
    Stream stream = transform(streamEntity);
    stream.setFollowing(isFavorite);
    return stream;
  }

  public List<Stream> transform(List<StreamEntity> eventEntities) {
    List<Stream> streams = new ArrayList<>(eventEntities.size());
    for (StreamEntity streamEntity : eventEntities) {
      streams.add(transform(streamEntity));
    }
    return streams;
  }

  public StreamEntity transform(Stream stream) {
    StreamEntity streamEntity = new StreamEntity();
    transformToTemplate(stream, streamEntity);
    return streamEntity;
  }

  protected void transformToTemplate(Stream stream, StreamEntity entityTemplate) {
    entityTemplate.setIdStream(stream.getId());
    entityTemplate.setIdUser(stream.getAuthorId());
    entityTemplate.setTitle(stream.getTitle());
    entityTemplate.setPhoto(stream.getPicture());
    entityTemplate.setLandscapePhoto(stream.getLandscapePicture());
    entityTemplate.setUserName(stream.getAuthorUsername());
    entityTemplate.setCountry(stream.getCountry());
    entityTemplate.setDescription(stream.getDescription());
    entityTemplate.setTopic(stream.getTopic());
    entityTemplate.setMediaCountByRelatedUsers(stream.getMediaCount());
    entityTemplate.setRemoved(stream.isRemoved() ? 1 : 0);
    entityTemplate.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
    entityTemplate.setTotalFavorites(Long.valueOf(stream.getTotalFollowers()));
    entityTemplate.setTotalWatchers(Long.valueOf(stream.getTotalWatchers()));
    entityTemplate.setReadWriteMode(stream.getReadWriteMode());
    entityTemplate.setVerifiedUser((stream.isVerifiedUser()) ? 1L : 0L);
    entityTemplate.setiAmContributor(stream.isCurrentUserContributor() ? 1 : 0);
    entityTemplate.setContributorCount(stream.getContributorCount());
    entityTemplate.setTotalFollowingWatchers(stream.getTotalFollowingWatchers());
    entityTemplate.setStrategic(stream.isStrategic());
    entityTemplate.setMuted(stream.isMuted());
    entityTemplate.setPhotoIdMedia(stream.getPhotoIdMedia());
    entityTemplate.setViews(stream.getViews());
    entityTemplate.setLastTimeShooted(stream.getLastTimeShooted());
    entityTemplate.setShareLink(stream.getShareLink());
    entityTemplate.setVideoUrl(stream.getVideoUrl());
    entityTemplate.setPromotedShotsEnabled(stream.isPromotedShotsEnabled());
    setPermissionsToBinary(stream, entityTemplate);
  }

  private void setPermissionsInBoolean(int permissionsInDecimal, Stream stream) {
    stream.setCanWrite((permissionsInDecimal & CAN_WRITE) == CAN_WRITE);
    stream.setCanReply((permissionsInDecimal & CAN_REPLY) == CAN_REPLY);
    stream.setCanPinItem((permissionsInDecimal & CAN_H1_ITEM) == CAN_H1_ITEM);
    stream.setCanFixItem((permissionsInDecimal & CAN_H2_ITEM) == CAN_H2_ITEM);
    stream.setCanPostPromoted((permissionsInDecimal & CAN_SEND_PROMOTED) == CAN_SEND_PROMOTED);
    stream.setCanTogglePromoted((permissionsInDecimal & CAN_TOGGLE_PROMOTED) == CAN_TOGGLE_PROMOTED);
    stream.setCanShowPromotedInfo((permissionsInDecimal & CAN_SHOW_PROMOTED_INFO) == CAN_SHOW_PROMOTED_INFO);
  }

  private void setPermissionsToBinary(Stream stream, StreamEntity streamEntity) {

    String permissions = "";

    permissions = permissions + (stream.canShowPromotedInfo() ? "1" : "0");
    permissions = permissions + (stream.canTogglePromoted() ? "1" : "0");
    permissions = permissions + (stream.canPostPromoted() ? "1" : "0");
    permissions = permissions + (stream.canFixItem() ? "1" : "0");
    permissions = permissions + (stream.canPinItem() ? "1" : "0");
    permissions = permissions + (stream.canReply() ? "1" : "0");
    permissions = permissions + (stream.canWrite() ? "1" : "0");

    streamEntity.setPermissions(Integer.parseInt(permissions, 2));
  }
}
