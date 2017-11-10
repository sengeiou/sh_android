package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class StreamEntityMapper {

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
    stream.setHistoricWatchers(
        streamEntity.getHistoricWatchers() != null ? streamEntity.getHistoricWatchers() : 0L);
    stream.setTotalShots(streamEntity.getTotalShots() != null ? streamEntity.getTotalShots() : 0L);
    stream.setUniqueShots(
        streamEntity.getUniqueShots() != null ? streamEntity.getUniqueShots() : 0L);
    stream.setReadWriteMode(streamEntity.getReadWriteMode());
    stream.setVerifiedUser(
        (streamEntity.getVerifiedUser() != null) ? (streamEntity.getVerifiedUser() == 1) : false);
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
    entityTemplate.setHistoricWatchers(stream.getHistoricWatchers());
    entityTemplate.setTotalShots(stream.getTotalShots());
    entityTemplate.setUniqueShots(stream.getUniqueShots());
    entityTemplate.setReadWriteMode(stream.getReadWriteMode());
    entityTemplate.setVerifiedUser((stream.isVerifiedUser()) ? 1L : 0L);
    entityTemplate.setiAmContributor(stream.isCurrentUserContributor() ? 1 : 0);
    entityTemplate.setContributorCount(stream.getContributorCount());
    entityTemplate.setTotalFollowingWatchers(stream.getTotalFollowingWatchers());
    entityTemplate.setStrategic(stream.isStrategic());
    entityTemplate.setMuted(stream.isMuted());
    entityTemplate.setPhotoIdMedia(stream.getPhotoIdMedia());
  }
}
