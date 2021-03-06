package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.data.repository.datasource.stream.StreamSearchDataSource;
import com.shootr.mobile.data.repository.remote.cache.LandingStreamsCache;
import com.shootr.mobile.data.repository.remote.cache.LastStreamVisitCache;
import com.shootr.mobile.data.repository.remote.cache.PromotedTiersCache;
import com.shootr.mobile.data.repository.remote.cache.StreamCache;
import com.shootr.mobile.data.repository.remote.cache.TimelineCache;
import com.shootr.mobile.data.repository.remote.cache.TimelineRepositionCache;
import com.shootr.mobile.domain.model.StreamTimeline;
import com.shootr.mobile.domain.model.TimelineReposition;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.PromotedTiers;
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
    private final LastStreamVisitCache lastStreamVisitCache;
    private final TimelineCache timelineCache;
    private final TimelineRepositionCache timelineRepositionCache;
    private final PromotedTiersCache promotedTiersCache;

    @Inject public LocalStreamRepository(@Local StreamDataSource localStreamDataSource,
        @Local StreamSearchDataSource localStreamSearchDataSource, StreamEntityMapper streamEntityMapper,
        StreamCache streamCache, LandingStreamsCache landingStreamsCache, LastStreamVisitCache lastStreamVisitCache,
        TimelineCache timelineCache, TimelineRepositionCache timelineRepositionCache,
        PromotedTiersCache promotedTiersCache) {
        this.localStreamDataSource = localStreamDataSource;
        this.localStreamSearchDataSource = localStreamSearchDataSource;
        this.streamEntityMapper = streamEntityMapper;
        this.streamCache = streamCache;
        this.landingStreamsCache = landingStreamsCache;
        this.lastStreamVisitCache = lastStreamVisitCache;
        this.timelineCache = timelineCache;
        this.timelineRepositionCache = timelineRepositionCache;
        this.promotedTiersCache = promotedTiersCache;
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

    @Override public void hide(String idStream) {
        throw new IllegalArgumentException("this method has no local implementation");
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

    @Override public void putLastStreamVisit(String idStream, long timestamp) {
        lastStreamVisitCache.putLastVisit(idStream, timestamp);
    }

    @Override public Long getLastStreamVisit(String idStream) {
        return lastStreamVisitCache.getLastVisit(idStream);
    }

    @Override public StreamTimeline getCachedTimeline(String idStream, String filter) {
        return timelineCache.getTimeline(idStream, filter);
    }

    @Override
    public StreamTimeline getCachedNicestTimeline(String idStream, String filter, long period) {
        return timelineCache.getNicestTimeline(idStream, filter, period);
    }

    @Override
    public void putTimelineReposition(TimelineReposition timelineReposition, String idStrea,
        String filter) {
        timelineRepositionCache.putTimelineReposition(timelineReposition, idStrea, filter);
    }

    @Override public TimelineReposition getTimelineReposition(String idStream, String filter) {
        return timelineRepositionCache.getTimelineReposition(idStream, filter);
    }

    @Override public PromotedTiers getPromotedTiers() {
        return promotedTiersCache.getPromotedTiers();
    }
}
