package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.data.repository.datasource.stream.StreamSearchDataSource;
import com.shootr.mobile.data.repository.remote.cache.LandingStreamsCache;
import com.shootr.mobile.data.repository.remote.cache.StreamCache;
import com.shootr.mobile.domain.model.stream.LandingStreams;
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
    private final LandingStreamsCache landingStreamsCache;

    @Inject public LocalStreamRepository(@Local StreamDataSource localStreamDataSource,
        @Local StreamSearchDataSource localStreamSearchDataSource, StreamEntityMapper streamEntityMapper,
        StreamCache streamCache, LandingStreamsCache landingStreamsCache) {
        this.localStreamDataSource = localStreamDataSource;
        this.localStreamSearchDataSource = localStreamSearchDataSource;
        this.streamEntityMapper = streamEntityMapper;
        this.streamCache = streamCache;
        this.landingStreamsCache = landingStreamsCache;
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

    @Override public void putLastTimeFiltered(String idStream, String lastTimeFiltered) {
        localStreamDataSource.putLastTimeFiltered(idStream, lastTimeFiltered);
    }

    @Override public void mute(String idStream) {
        localStreamDataSource.mute(idStream);
        localStreamSearchDataSource.mute(idStream);
    }

    @Override public void unmute(String idStream) {
        localStreamDataSource.unmute(idStream);
        localStreamSearchDataSource.unmute(idStream);
    }

    @Override public void follow(String idStream) {
        localStreamDataSource.follow(idStream);
        localStreamSearchDataSource.follow(idStream);
    }

    @Override public void unfollow(String idStream) {
        localStreamDataSource.unfollow(idStream);
        localStreamSearchDataSource.unfollow(idStream);
    }

    @Override public long getConnectionTimes(String idStream) {
        return localStreamDataSource.getConnectionTimes(idStream);
    }

    @Override public void storeConnection(String idStream, long connections) {
        localStreamDataSource.storeConnection(idStream, connections);
    }

    @Override public LandingStreams getLandingStreams() {
        return landingStreamsCache.getLandingStreams();
    }

    @Override public void putLandingStreams(LandingStreams landingStreams) {
        landingStreamsCache.putLandingStreams(landingStreams);
    }
}
