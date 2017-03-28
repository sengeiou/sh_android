package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.data.repository.datasource.stream.StreamSearchDataSource;
import com.shootr.mobile.data.repository.remote.cache.StreamCache;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalStreamRepository implements StreamRepository {

    private final StreamDataSource localStreamDataSource;
    private final StreamSearchDataSource localStreamSearchDataSource;
    private final StreamEntityMapper streamEntityMapper;
    private final StreamCache streamCache;

    @Inject public LocalStreamRepository(@Local StreamDataSource localStreamDataSource,
      @Local StreamSearchDataSource localStreamSearchDataSource, StreamEntityMapper streamEntityMapper,
      StreamCache streamCache) {
        this.localStreamDataSource = localStreamDataSource;
        this.localStreamSearchDataSource = localStreamSearchDataSource;
        this.streamEntityMapper = streamEntityMapper;
        this.streamCache = streamCache;
    }

    @Override public Stream getStreamById(String idStream, String[] types) {
        Stream cachedStream = streamCache.getStreamById(idStream);
        if (cachedStream != null) {
            return cachedStream;
        } else {
            StreamEntity streamEntity = localStreamDataSource.getStreamById(idStream, types);
            if (streamEntity == null) {
                streamEntity = fallbackOnSearchResults(idStream);
            }
            return streamEntityMapper.transform(streamEntity);
        }
    }

    private StreamEntity fallbackOnSearchResults(String idEvent) {
        StreamEntity streamEntity = localStreamSearchDataSource.getStreamResult(idEvent);
        if (streamEntity != null) {
            localStreamDataSource.putStream(streamEntity);
        }
        return streamEntity;
    }

    @Override public List<Stream> getStreamsByIds(List<String> streamIds, String[] types) {
        List<StreamEntity> eventEntities = localStreamDataSource.getStreamByIds(streamIds, types);
        return streamEntityMapper.transform(eventEntities);
    }

    @Override public Stream putStream(Stream stream) {
        StreamEntity streamEntity = streamEntityMapper.transform(stream);
        localStreamDataSource.putStream(streamEntity);
        return stream;
    }

    @Override public void removeStream(String idStream) {
        localStreamDataSource.removeStream(idStream);
    }

    @Override public void restoreStream(String idStream) {
        localStreamDataSource.restoreStream(idStream);
    }

    @Override public String getLastTimeFiltered(String idStream) {
        return localStreamDataSource.getLastTimeFilteredStream(idStream);
    }
}
