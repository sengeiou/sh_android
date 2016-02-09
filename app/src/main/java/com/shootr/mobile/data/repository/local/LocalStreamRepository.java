package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.event.StreamDataSource;
import com.shootr.mobile.data.repository.datasource.event.StreamSearchDataSource;
import com.shootr.mobile.data.repository.remote.cache.StreamCache;
import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.StreamRepository;
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

    @Override public Stream getStreamById(String idStream) {
        Stream cachedStream = streamCache.getStreamById(idStream);
        if (cachedStream != null) {
            return cachedStream;
        } else {
            StreamEntity streamEntity = localStreamDataSource.getStreamById(idStream);
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

    @Override public List<Stream> getStreamsByIds(List<String> streamIds) {
        List<StreamEntity> eventEntities = localStreamDataSource.getStreamByIds(streamIds);
        return streamEntityMapper.transform(eventEntities);
    }

    @Override public Stream putStream(Stream stream) {
        StreamEntity streamEntity = streamEntityMapper.transform(stream);
        localStreamDataSource.putStream(streamEntity);
        return stream;
    }

    @Override public Stream putStream(Stream stream, boolean notify) {
        throw new IllegalStateException("Notify not allowed in local repository.");
    }

    @Override public void shareStream(String idStream) {
        throw new IllegalStateException("Not allowed in local repository.");
    }

    @Override public void removeStream(String idStream) {
        localStreamDataSource.removeStream(idStream);
    }

    @Override public void restoreStream(String idStream) {
        localStreamDataSource.restoreStream(idStream);
    }

    @Override public Stream getBlogStream(String country, String language) {
        throw new IllegalStateException("Not allowed in local repository.");
    }

    @Override public Stream getHelpStream(String country, String language) {
        throw new IllegalStateException("Not allowed in local repository.");
    }
}
