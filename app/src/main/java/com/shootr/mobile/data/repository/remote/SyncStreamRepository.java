package com.shootr.mobile.data.repository.remote;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.mapper.BootstrappingEntityMapper;
import com.shootr.mobile.data.mapper.LandingStreamsEntityMapper;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.MemoryStreamListSynchronizationRepository;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.data.repository.remote.cache.LandingStreamsCache;
import com.shootr.mobile.data.repository.remote.cache.QueueElementCache;
import com.shootr.mobile.data.repository.remote.cache.StreamCache;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.data.repository.sync.SyncableStreamEntityFactory;
import com.shootr.mobile.domain.exception.InvalidYoutubeVideoUrlException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.model.Bootstrapping;
import com.shootr.mobile.domain.model.QueueElement;
import com.shootr.mobile.domain.model.QueueElementType;
import com.shootr.mobile.domain.model.StreamTimeline;
import com.shootr.mobile.domain.model.TimelineReposition;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamUpdateParameters;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamListSynchronizationRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;

public class SyncStreamRepository
    implements StreamRepository, SyncableRepository, ExternalStreamRepository {

  private final StreamEntityMapper streamEntityMapper;
  private final LandingStreamsEntityMapper landingStreamsEntityMapper;
  private final StreamDataSource localStreamDataSource;
  private final StreamDataSource remoteStreamDataSource;
  private final QueueElementCache queueElementCache;
  private final StreamListSynchronizationRepository streamListSynchronizationRepository;
  private final SyncableStreamEntityFactory syncableStreamEntityFactory;
  private final StreamCache streamCache;
  private final LandingStreamsCache landingStreamsCache;
  private final SyncTrigger syncTrigger;
  private final BootstrappingEntityMapper socketEntityMapper;

  @Inject public SyncStreamRepository(StreamEntityMapper streamEntityMapper,
      LandingStreamsEntityMapper landingStreamsEntityMapper,
      @Local StreamDataSource localStreamDataSource,
      @Remote StreamDataSource remoteStreamDataSource, QueueElementCache queueElementCache,
      StreamListSynchronizationRepository streamListSynchronizationRepository,
      SyncableStreamEntityFactory syncableStreamEntityFactory, StreamCache streamCache,
      LandingStreamsCache landingStreamsCache, SyncTrigger syncTrigger,
      BootstrappingEntityMapper socketEntityMapper) {
    this.landingStreamsEntityMapper = landingStreamsEntityMapper;
    this.localStreamDataSource = localStreamDataSource;
    this.remoteStreamDataSource = remoteStreamDataSource;
    this.streamEntityMapper = streamEntityMapper;
    this.queueElementCache = queueElementCache;
    this.streamListSynchronizationRepository = streamListSynchronizationRepository;
    this.syncableStreamEntityFactory = syncableStreamEntityFactory;
    this.streamCache = streamCache;
    this.landingStreamsCache = landingStreamsCache;
    this.syncTrigger = syncTrigger;
    this.socketEntityMapper = socketEntityMapper;
  }

  @Override public Stream getStreamById(String idStream, String[] types) {
    try {
      StreamEntity streamEntity = remoteStreamDataSource.getStreamById(idStream, types);
      if (streamEntity != null) {
        markEntityAsSynchronized(streamEntity);
        localStreamDataSource.putStream(streamEntity);
        Stream stream = streamEntityMapper.transform(streamEntity);
        streamCache.putStream(stream);
        return stream;
      } else {
        return null;
      }
    } catch (ServerCommunicationException e) {
      /* no-op */
      return null;
    }
  }

  @Override public List<Stream> getStreamsByIds(List<String> streamIds, String[] types) {
    List<StreamEntity> remoteEvents = remoteStreamDataSource.getStreamByIds(streamIds, types);
    markEntitiesAsSynchronized(remoteEvents);
    localStreamDataSource.putStreams(remoteEvents);
    return streamEntityMapper.transform(remoteEvents);
  }

  @Override public Stream putStream(Stream stream) throws InvalidYoutubeVideoUrlException {
    return putStream(stream, false);
  }

  @Override public Stream putStream(Stream stream, boolean notify)
      throws InvalidYoutubeVideoUrlException {
    StreamEntity currentOrNewEntity = syncableStreamEntityFactory.updatedOrNewEntity(stream);
    currentOrNewEntity.setNotifyCreation(notify ? 1 : 0);

    StreamEntity remoteStreamEntity = remoteStreamDataSource.createStream(currentOrNewEntity);
    markEntityAsSynchronized(remoteStreamEntity);
    localStreamDataSource.putStream(remoteStreamEntity);
    return streamEntityMapper.transform(remoteStreamEntity);
  }

  @Override public Stream updateStream(StreamUpdateParameters streamUpdateParameters) {
    StreamEntity streamEntity = remoteStreamDataSource.updateStream(streamUpdateParameters);
    localStreamDataSource.putStream(streamEntity);
    streamListSynchronizationRepository.setStreamsRefreshDate(
        MemoryStreamListSynchronizationRepository.DEFAULT_REFRESH_DATE);
    return streamEntityMapper.transform(streamEntity);
  }

  @Override public Bootstrapping getSocket() {
    return socketEntityMapper.transform(remoteStreamDataSource.getSocket());
  }

  @Override public void shareStream(String idStream) {
    remoteStreamDataSource.shareStream(idStream);
  }

  @Override public void removeStream(String idStream) {
    remoteStreamDataSource.removeStream(idStream);
    streamListSynchronizationRepository.setStreamsRefreshDate(
        MemoryStreamListSynchronizationRepository.DEFAULT_REFRESH_DATE);
  }

  @Override public void restoreStream(String idStream) {
    remoteStreamDataSource.restoreStream(idStream);
    streamListSynchronizationRepository.setStreamsRefreshDate(
        MemoryStreamListSynchronizationRepository.DEFAULT_REFRESH_DATE);
  }

  @Override public String getLastTimeFiltered(String idStream) {
    return "0";
  }

  @Override public void putLastTimeFiltered(String idStream, String lastTimeFiltered) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public void mute(String idStream) {
    landingStreamsCache.invalidate();
    remoteStreamDataSource.mute(idStream);
  }

  @Override public void unmute(String idStream) {
    landingStreamsCache.invalidate();
    remoteStreamDataSource.unmute(idStream);
  }

  @Override public void follow(String idStream) {
    try {
      remoteStreamDataSource.follow(idStream);
      streamListSynchronizationRepository.setStreamsRefreshDate(
          MemoryStreamListSynchronizationRepository.DEFAULT_REFRESH_DATE);
      syncTrigger.triggerSync();
    } catch (ServerCommunicationException e) {
      localStreamDataSource.putFailedFollow(createFailedFollow(idStream, true));
      syncTrigger.notifyNeedsSync(this);
    }
  }

  @Override public void unfollow(String idStream) {
    try {
      remoteStreamDataSource.unfollow(idStream);
      streamListSynchronizationRepository.setStreamsRefreshDate(
          MemoryStreamListSynchronizationRepository.DEFAULT_REFRESH_DATE);
      syncTrigger.triggerSync();
    } catch (ServerCommunicationException e) {
      localStreamDataSource.putFailedFollow(createFailedFollow(idStream, false));
      syncTrigger.notifyNeedsSync(this);
    }
  }

  @Override public void hide(String idStream) {
    try {
      remoteStreamDataSource.hide(idStream);
      landingStreamsCache.invalidate();
    } catch (ServerCommunicationException e) {
      removeHidingStreamFromCache(idStream);
      queueElementCache.putQueueElement(createFailedHide(idStream));
    }
  }

  private void removeHidingStreamFromCache(String idStream) {
    LandingStreams landingStreams = landingStreamsCache.getLandingStreams();
    Iterator<Stream> iterator = landingStreams.getUserStreams().getStreams().iterator();
    while (iterator.hasNext()) {
      Stream stream = iterator.next();
      if (stream.getId().equals(idStream)) {
        iterator.remove();
        break;
      }
    }
    landingStreamsCache.putLandingStreams(landingStreams);
  }

  private QueueElement createFailedHide(String idStream) {
    QueueElement queueElement = new QueueElement();
    queueElement.setId(idStream);
    queueElement.setType(QueueElementType.HIDE);
    return queueElement;
  }

  @Override public long getConnectionTimes(String idStream) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public void storeConnection(String idStream, long connections) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public LandingStreams getLandingStreams() {
    return landingStreamsEntityMapper.transform(remoteStreamDataSource.getLandingStreams());
  }

  @Override public void putLandingStreams(LandingStreams landingStreams) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public void putLastStreamVisit(String idStream, long timestamp) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public Long getLastStreamVisit(String idStream) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public StreamTimeline getCachedTimeline(String idStream, String filter) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public void putTimelineReposition(TimelineReposition timelineReposition, String idStrea,
      String filter) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public TimelineReposition getTimelineReposition(String idStrea, String filter) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public Stream getBlogStream(String country, String language) {
    StreamEntity blogStream = remoteStreamDataSource.getBlogStream(country, language);
    if (blogStream != null) {
      markEntityAsSynchronized(blogStream);
      localStreamDataSource.putStream(blogStream);
      return streamEntityMapper.transform(blogStream);
    } else {
      return null;
    }
  }

  @Override public Stream getHelpStream(String country, String language) {
    StreamEntity helpStream = remoteStreamDataSource.getHelpStream(country, language);
    if (helpStream != null) {
      markEntityAsSynchronized(helpStream);
      localStreamDataSource.putStream(helpStream);
      return streamEntityMapper.transform(helpStream);
    } else {
      return null;
    }
  }

  private void markEntitiesAsSynchronized(List<StreamEntity> remoteEvents) {
    for (StreamEntity event : remoteEvents) {
      markEntityAsSynchronized(event);
    }
  }

  private void markEntityAsSynchronized(StreamEntity event) {
    event.setSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
  }

  @Override public void dispatchSync() {
    List<FollowEntity> pendingEntities = localStreamDataSource.getEntitiesNotSynchronized();
    localStreamDataSource.deleteFailedFollows();
    for (FollowEntity entity : pendingEntities) {
      if (entity.isFollowing()) {
        followFailed(entity);
      } else {
        unfollow(entity.getIdFollowedUser());
      }
    }
  }

  private void followFailed(FollowEntity entity) {
    follow(entity.getIdFollowedUser());
  }

  @NonNull private FollowEntity createFailedFollow(String idStream, boolean isFollowing) {
    FollowEntity followEntity = new FollowEntity();
    followEntity.setFollowing(isFollowing);
    followEntity.setIdFollowed(idStream);
    followEntity.setType("STREAM");
    return followEntity;
  }
}
