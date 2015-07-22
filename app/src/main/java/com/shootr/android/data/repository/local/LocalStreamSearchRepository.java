package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.StreamSearchEntity;
import com.shootr.android.data.mapper.StreamEntityMapper;
import com.shootr.android.data.mapper.StreamSearchEntityMapper;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
import com.shootr.android.data.repository.datasource.event.StreamSearchDataSource;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.repository.StreamSearchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.WatchersRepository;
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

    @Override public List<EventSearchResult> getDefaultStreams(String locale) {
        List<StreamSearchEntity> defaultEvents = localStreamSearchDataSource.getDefaultStreams(locale);
        return streamSearchEntityMapper.transformToDomain(defaultEvents);
    }

    @Override public List<EventSearchResult> getStreams(String query, String locale) {
        throw new IllegalArgumentException("method not implemented in local repository");
    }

    @Override public void putDefaultStreams(List<EventSearchResult> eventSearchResults) {
        localStreamSearchDataSource.putDefaultStreams(streamSearchEntityMapper.transform(eventSearchResults));
    }

    @Override public void deleteDefaultStreams() {
        localStreamSearchDataSource.deleteDefaultStreams();
    }

    @Override public List<EventSearchResult> getStreamsListing(String listingIdUser) {
        List<StreamEntity> eventEntitiesListing = localStreamDataSource.getStreamsListing(listingIdUser);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformStreamEntitiesWithWatchers(eventEntitiesListing, watchers);
    }


    private List<EventSearchResult> transformStreamEntitiesWithWatchers(List<StreamEntity> eventEntities,
      Map<String, Integer> watchers) {
        List<EventSearchResult> results = new ArrayList<>(eventEntities.size());
        for (StreamEntity streamEntity : eventEntities) {
            Stream stream = streamEntityMapper.transform(streamEntity);
            Integer eventWatchers = watchers.get(stream.getId());
            EventSearchResult eventSearchResult =
              new EventSearchResult(stream, eventWatchers != null ? eventWatchers : 0);
            results.add(eventSearchResult);
        }
        return results;
    }
}
