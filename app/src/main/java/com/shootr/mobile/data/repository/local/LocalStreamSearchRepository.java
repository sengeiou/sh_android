package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.StreamSearchEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.mapper.StreamSearchEntityMapper;
import com.shootr.mobile.data.repository.datasource.event.StreamDataSource;
import com.shootr.mobile.data.repository.datasource.event.StreamSearchDataSource;
import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.StreamSearchRepository;
import com.shootr.mobile.domain.repository.WatchersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class LocalStreamSearchRepository implements StreamSearchRepository {

    private final StreamSearchDataSource localStreamSearchDataSource;
    private final StreamSearchEntityMapper streamSearchEntityMapper;
    private final StreamDataSource localStreamDataSource;
    private final WatchersRepository localWatchersRepository;
    private final StreamEntityMapper streamEntityMapper;

    @Inject public LocalStreamSearchRepository(@Local StreamSearchDataSource localStreamSearchDataSource,
      StreamSearchEntityMapper streamSearchEntityMapper, @Local StreamDataSource localStreamDataSource,
      @Local WatchersRepository localWatchersRepository, StreamEntityMapper streamEntityMapper) {
        this.localStreamSearchDataSource = localStreamSearchDataSource;
        this.streamSearchEntityMapper = streamSearchEntityMapper;
        this.localStreamDataSource = localStreamDataSource;
        this.localWatchersRepository = localWatchersRepository;
        this.streamEntityMapper = streamEntityMapper;
    }

    @Override public List<StreamSearchResult> getDefaultStreams(String locale, String[] types) {
        List<StreamSearchEntity> defaultEvents = localStreamSearchDataSource.getDefaultStreams(locale);
        return streamSearchEntityMapper.transformToDomain(defaultEvents);
    }

    @Override public List<StreamSearchResult> getStreams(String query, String locale, String[] types) {
        throw new IllegalArgumentException("method not implemented in local repository");
    }

    @Override public void putDefaultStreams(List<StreamSearchResult> streamSearchResults) {
        localStreamSearchDataSource.putDefaultStreams(streamSearchEntityMapper.transform(streamSearchResults));
    }

    @Override public void deleteDefaultStreams() {
        localStreamSearchDataSource.deleteDefaultStreams();
    }

    @Override public List<StreamSearchResult> getStreamsListing(String listingIdUser, String[] types) {
        List<StreamEntity> eventEntitiesListing = localStreamDataSource.getStreamsListing(listingIdUser,
            types);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformStreamEntitiesWithWatchers(eventEntitiesListing, watchers);
    }

    private List<StreamSearchResult> transformStreamEntitiesWithWatchers(List<StreamEntity> eventEntities,
      Map<String, Integer> watchers) {
        List<StreamSearchResult> results = new ArrayList<>(eventEntities.size());
        for (StreamEntity streamEntity : eventEntities) {
            Stream stream = streamEntityMapper.transform(streamEntity);
            Integer eventWatchers = watchers.get(stream.getId());
            StreamSearchResult streamSearchResult =
              new StreamSearchResult(stream, eventWatchers != null ? eventWatchers : 0);
            results.add(streamSearchResult);
        }
        return results;
    }
}
