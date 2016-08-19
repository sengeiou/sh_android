package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.event.DatabaseMemoryStreamSearchDataSource;
import com.shootr.mobile.data.repository.datasource.event.StreamDataSource;
import com.shootr.mobile.data.repository.datasource.event.StreamListDataSource;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.repository.WatchersRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RemoteStreamSearchRepositoryTest {

  private static final String LOCALE = "locale";
  private static final String[] TYPES = { "PUBLIC", "VIEWONLY" };
  private static final String STREAM_ID = "idStream";
  private static final Integer WATCHERS_COUNT = 1;
  private static final String QUERY = "query";
  private static final String USER_ID = "userId";
  @Mock DatabaseMemoryStreamSearchDataSource localStreamSearchDataSource;
  @Mock StreamListDataSource remoteStreamListDataSource;
  @Mock WatchersRepository localWatchersRepository;
  @Mock StreamEntityMapper streamEntityMapper;
  @Mock StreamDataSource localStreamDataSource;
  @Mock StreamDataSource remoteStreamDataSource;

  private RemoteStreamSearchRepository repository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    repository =
        new RemoteStreamSearchRepository(localStreamSearchDataSource, remoteStreamListDataSource,
            localWatchersRepository, streamEntityMapper, localStreamDataSource,
            remoteStreamDataSource);
  }

  @Test public void shouldPutInLocalStreamListWhenAreObtainedFromRemote() throws Exception {
    when(remoteStreamListDataSource.getStreamList(anyString(), any(String[].class))).thenReturn(
        streamEntityList());
    when(localWatchersRepository.getWatchers()).thenReturn(watchers());
    when(streamEntityMapper.transform(any(StreamEntity.class))).thenReturn(stream());

    repository.getDefaultStreams(LOCALE, TYPES);

    verify(localStreamDataSource).putStreams(anyList());
  }

  @Test public void shouldSetLastSearchResultsInLocalWhenGetStreams() throws Exception {
    when(remoteStreamListDataSource.getStreams(anyString(), anyString(),
        any(String[].class))).thenReturn(streamEntityList());
    when(localWatchersRepository.getWatchers()).thenReturn(watchers());
    when(streamEntityMapper.transform(any(StreamEntity.class))).thenReturn(stream());

    repository.getStreams(QUERY, LOCALE, TYPES);

    verify(localStreamSearchDataSource).setLastSearchResults(anyList());
  }

  @Test public void shouldPutStreamsInLocalWhenGetStreamsListing() throws Exception {
    repository.getStreamsListing(USER_ID, TYPES);

    verify(localStreamDataSource).putStreams(anyList());
  }

  private Map<String, Integer> watchers() {
    Map<String, Integer> watcher = new HashMap<>();
    watcher.put(STREAM_ID, WATCHERS_COUNT);
    return watcher;
  }

  private List<StreamEntity> streamEntityList() {
    StreamEntity streamEntity = new StreamEntity();
    streamEntity.setIdStream(STREAM_ID);
    return Collections.singletonList(streamEntity);
  }

  private Stream stream() {
    Stream stream = new Stream();

    stream.setId(STREAM_ID);

    return stream;
  }
}
