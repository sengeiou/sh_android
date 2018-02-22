package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.mapper.BootstrappingEntityMapper;
import com.shootr.mobile.data.mapper.LandingStreamsEntityMapper;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.data.repository.remote.cache.LandingStreamsCache;
import com.shootr.mobile.data.repository.remote.cache.QueueElementCache;
import com.shootr.mobile.data.repository.remote.cache.StreamCache;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableStreamEntityFactory;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.repository.stream.StreamListSynchronizationRepository;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SyncStreamRepositoryTest {

  public static final String ID_STREAM = "idStream";
  public static final String COUNTRY = "country";
  public static final String LANGUAGE = "language";
  private static final boolean NOTIFY = true;
  private static final boolean NOTIFY_MESSAGE = true;
  String[] TYPES_STREAM = {
      "PUBLIC", "VIEWONLY"
  };
  @Mock StreamEntityMapper streamEntityMapper;
  @Mock BootstrappingEntityMapper socketEntityMapper;
  @Mock StreamDataSource localStreamDataSource;
  @Mock StreamDataSource remoteStreamDataSource;
  @Mock SyncableStreamEntityFactory syncableStreamEntityFactory;
  @Mock StreamCache streamCache;
  @Mock SyncTrigger syncTrigger;
  @Mock LandingStreamsEntityMapper landingStreamsEntityMapper;
  @Mock LandingStreamsCache landingStreamsCache;
  @Mock StreamListSynchronizationRepository streamListSynchronizationRepository;
  @Mock QueueElementCache shootrQueueCache;

  private SyncStreamRepository syncStreamRepository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    syncStreamRepository = new SyncStreamRepository(streamEntityMapper, landingStreamsEntityMapper,
        localStreamDataSource, remoteStreamDataSource, shootrQueueCache,
        streamListSynchronizationRepository, syncableStreamEntityFactory, streamCache,
        landingStreamsCache, syncTrigger, socketEntityMapper);

  }

  @Test public void shouldPutStreamInCacheWhenGetStreamByIdAndRemoteStreamIsNotNull()
      throws Exception {
    when(remoteStreamDataSource.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(streamEntity());

    syncStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM);

    verify(streamCache).putStream(any(Stream.class));
  }

  @Test public void shouldNotPutStreamInCacheWhenGetStreamByIdAndRemoteStreamIsNull()
      throws Exception {
    when(remoteStreamDataSource.getStreamById(ID_STREAM, TYPES_STREAM)).thenReturn(null);

    syncStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM);

    verify(streamCache, never()).putStream(any(Stream.class));
  }

  @Test public void shouldPutStreamInLocalWhenGetBlogStreamAndRemoteBlogStreamIsNotNull()
      throws Exception {
    when(remoteStreamDataSource.getBlogStream(COUNTRY, LANGUAGE)).thenReturn(streamEntity());

    syncStreamRepository.getBlogStream(COUNTRY, LANGUAGE);

    verify(localStreamDataSource).putStream(any(StreamEntity.class));
  }

  @Test public void shouldNotPutStreamInLocalWhenGetBlogStreamAndRemoteBlogStreamIsNull()
      throws Exception {
    when(remoteStreamDataSource.getBlogStream(COUNTRY, LANGUAGE)).thenReturn(null);

    syncStreamRepository.getBlogStream(COUNTRY, LANGUAGE);

    verify(localStreamDataSource, never()).putStream(any(StreamEntity.class));
  }

  @Test public void shouldPutStreamInLocalWhenGetHelpStreamAndRemoteHelpStreamIsNotNull()
      throws Exception {
    when(remoteStreamDataSource.getHelpStream(COUNTRY, LANGUAGE)).thenReturn(streamEntity());

    syncStreamRepository.getHelpStream(COUNTRY, LANGUAGE);

    verify(localStreamDataSource).putStream(any(StreamEntity.class));
  }

  @Test public void shouldNotPutStreamInLocalWhenGetHelpStreamAndRemoteHelpStreamIsNull()
      throws Exception {
    when(remoteStreamDataSource.getHelpStream(COUNTRY, LANGUAGE)).thenReturn(null);

    syncStreamRepository.getHelpStream(COUNTRY, LANGUAGE);

    verify(localStreamDataSource, never()).putStream(any(StreamEntity.class));
  }

  @Test public void shouldPutStreamInCacheWhenGetStreamByIdAndRemoteStreamEntityIsNotNull()
      throws Exception {
    when(remoteStreamDataSource.getStreamById(anyString(), anyArray())).thenReturn(streamEntity());

    syncStreamRepository.getStreamById(ID_STREAM, TYPES_STREAM);

    verify(streamCache).putStream(any(Stream.class));
  }

  @Test public void shouldPutStreamsInLocalWhenGetRemoteStreamsByIds() throws Exception {
    when(remoteStreamDataSource.getStreamByIds(anyList(), anyArray())).thenReturn(
        Collections.singletonList(streamEntity()));

    syncStreamRepository.getStreamsByIds(Collections.singletonList(ID_STREAM), TYPES_STREAM);

    verify(localStreamDataSource).putStreams(anyList());
  }

  @Test public void shouldPutStreamInLocalWhenCallPutStream() throws Exception {
    when(remoteStreamDataSource.createStream(any(StreamEntity.class))).thenReturn(streamEntity());
    when(syncableStreamEntityFactory.updatedOrNewEntity(any(Stream.class))).thenReturn(
        streamEntity());

    syncStreamRepository.putStream(stream(), NOTIFY);

    verify(localStreamDataSource).putStream(any(StreamEntity.class));
  }

  private Stream stream() {
    return new Stream();
  }

  private String[] anyArray() {
    return any(String[].class);
  }

  private StreamEntity streamEntity() {
    StreamEntity streamEntity = new StreamEntity();
    streamEntity.setIdUser(ID_STREAM);
    return streamEntity;
  }
}
