package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.mapper.StreamEntityMapper;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
import com.shootr.android.data.repository.datasource.event.StreamSearchDataSource;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.StreamRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalStreamRepository implements StreamRepository {

    private final StreamDataSource localStreamDataSource;
    private final StreamSearchDataSource localStreamSearchDataSource;
    private final StreamEntityMapper streamEntityMapper;

    @Inject public LocalStreamRepository(@Local StreamDataSource localStreamDataSource,
      @Local StreamSearchDataSource localStreamSearchDataSource, StreamEntityMapper streamEntityMapper) {
        this.localStreamDataSource = localStreamDataSource;
        this.localStreamSearchDataSource = localStreamSearchDataSource;
        this.streamEntityMapper = streamEntityMapper;
    }

    @Override public Stream getStreamById(String idStream) {
        StreamEntity streamEntity = localStreamDataSource.getStreamById(idStream);
        if (streamEntity == null) {
            streamEntity = fallbackOnSearchResults(idStream);
        }
        return streamEntityMapper.transform(streamEntity);
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

    @Override public Stream getBlogStream(String country) {
        throw new IllegalStateException("Not allowed in local repository.");
    }

    @Override public Stream getHelpStream(String country) {
        throw new IllegalStateException("Not allowed in local repository.");
    }
}
