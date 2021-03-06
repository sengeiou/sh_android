package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.stream.DatabaseMemoryStreamSearchDataSource;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.data.repository.datasource.stream.StreamListDataSource;
import com.shootr.mobile.domain.model.stream.Stream;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;

public class RemoteStreamSearchRepositoryTest {

  private static final String LOCALE = "locale";
  private static final String[] TYPES = { "PUBLIC", "VIEWONLY" };
  private static final String STREAM_ID = "idStream";
  private static final Integer WATCHERS_COUNT = 1;
  private static final String QUERY = "query";
  private static final String USER_ID = "userId";
  @Mock DatabaseMemoryStreamSearchDataSource localStreamSearchDataSource;
  @Mock StreamListDataSource remoteStreamListDataSource;
  @Mock StreamEntityMapper streamEntityMapper;
  @Mock StreamDataSource localStreamDataSource;
  @Mock StreamDataSource remoteStreamDataSource;

  private RemoteStreamSearchRepository repository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    repository =
        new RemoteStreamSearchRepository(localStreamSearchDataSource, remoteStreamListDataSource,
            streamEntityMapper, localStreamDataSource,
            remoteStreamDataSource);
  }

  @Test public void shouldPutStreamsInLocalWhenGetStreamsListing() throws Exception {
    repository.getStreamsListing(USER_ID, TYPES);

    verify(localStreamDataSource).putStreams(anyList());
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
