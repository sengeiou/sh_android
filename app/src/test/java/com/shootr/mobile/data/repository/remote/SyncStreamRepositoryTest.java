package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.event.StreamDataSource;
import com.shootr.mobile.data.repository.remote.cache.StreamCache;
import com.shootr.mobile.data.repository.sync.SyncableStreamEntityFactory;
import com.shootr.mobile.domain.Stream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SyncStreamRepositoryTest {

    public static final String ID_STREAM = "idStream";
    public static final String COUNTRY = "country";
    public static final String LANGUAGE = "language";
    @Mock StreamEntityMapper streamEntityMapper;
    @Mock StreamDataSource localStreamDataSource;
    @Mock StreamDataSource remoteStreamDataSource;
    @Mock SyncableStreamEntityFactory syncableStreamEntityFactory;
    @Mock StreamCache streamCache;

    private SyncStreamRepository syncStreamRepository;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        syncStreamRepository = new SyncStreamRepository(streamEntityMapper,
          localStreamDataSource,
          remoteStreamDataSource,
          syncableStreamEntityFactory,
          streamCache);
    }

    @Test public void shouldPutStreamInCacheWhenGetStreamByIdAndRemoteStreamIsNotNull() throws Exception {
        when(remoteStreamDataSource.getStreamById(ID_STREAM)).thenReturn(streamEntity());

        syncStreamRepository.getStreamById(ID_STREAM);

        verify(streamCache).putStream(any(Stream.class));
    }

    @Test public void shouldNotPutStreamInCacheWhenGetStreamByIdAndRemoteStreamIsNull() throws Exception {
        when(remoteStreamDataSource.getStreamById(ID_STREAM)).thenReturn(null);

        syncStreamRepository.getStreamById(ID_STREAM);

        verify(streamCache, never()).putStream(any(Stream.class));
    }

    @Test public void shouldPutStreamInLocalWhenGetBlogStreamAndRemoteBlogStreamIsNotNull() throws Exception {
        when(remoteStreamDataSource.getBlogStream(COUNTRY, LANGUAGE)).thenReturn(streamEntity());

        syncStreamRepository.getBlogStream(COUNTRY, LANGUAGE);

        verify(localStreamDataSource).putStream(any(StreamEntity.class));
    }

    @Test public void shouldNotPutStreamInLocalWhenGetBlogStreamAndRemoteBlogStreamIsNull() throws Exception {
        when(remoteStreamDataSource.getBlogStream(COUNTRY, LANGUAGE)).thenReturn(null);

        syncStreamRepository.getBlogStream(COUNTRY, LANGUAGE);

        verify(localStreamDataSource, never()).putStream(any(StreamEntity.class));
    }

    @Test public void shouldPutStreamInLocalWhenGetHelpStreamAndRemoteHelpStreamIsNotNull() throws Exception {
        when(remoteStreamDataSource.getHelpStream(COUNTRY, LANGUAGE)).thenReturn(streamEntity());

        syncStreamRepository.getHelpStream(COUNTRY, LANGUAGE);

        verify(localStreamDataSource).putStream(any(StreamEntity.class));
    }

    @Test public void shouldNotPutStreamInLocalWhenGetHelpStreamAndRemoteHelpStreamIsNull() throws Exception {
        when(remoteStreamDataSource.getHelpStream(COUNTRY, LANGUAGE)).thenReturn(null);

        syncStreamRepository.getHelpStream(COUNTRY, LANGUAGE);

        verify(localStreamDataSource, never()).putStream(any(StreamEntity.class));
    }

    private StreamEntity streamEntity() {
        StreamEntity streamEntity = new StreamEntity();
        streamEntity.setIdUser(ID_STREAM);
        return streamEntity;
    }
}
