package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.StreamSearchEntity;
import com.shootr.android.data.mapper.StreamEntityMapper;
import com.shootr.android.data.repository.datasource.event.DatabaseMemoryStreamSearchDataSource;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
import com.shootr.android.data.repository.datasource.event.StreamListDataSource;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.repository.StreamSearchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class RemoteStreamSearchRepository implements StreamSearchRepository {

    @Deprecated private final DatabaseMemoryStreamSearchDataSource localEventSearchDataSource;
    private final StreamListDataSource remoteStreamListDataSource;
    private final WatchersRepository localWatchersRepository;
    private final StreamEntityMapper streamEntityMapper;
    private final SessionRepository sessionRepository;
    private final StreamDataSource localStreamDataSource;
    private final StreamDataSource remoteStreamDataSource;

    @Inject public RemoteStreamSearchRepository(DatabaseMemoryStreamSearchDataSource localEventSearchDataSource,
      @Remote StreamListDataSource remoteStreamListDataSource, @Local WatchersRepository localWatchersRepository,
      StreamEntityMapper streamEntityMapper, SessionRepository sessionRepository,
      @Local StreamDataSource localStreamDataSource, @Remote StreamDataSource remoteStreamDataSource) {
        this.localEventSearchDataSource = localEventSearchDataSource;
        this.remoteStreamListDataSource = remoteStreamListDataSource;
        this.localWatchersRepository = localWatchersRepository;
        this.streamEntityMapper = streamEntityMapper;
        this.sessionRepository = sessionRepository;
        this.localStreamDataSource = localStreamDataSource;
        this.remoteStreamDataSource = remoteStreamDataSource;
    }

    @Override public List<StreamSearchResult> getDefaultStreams(String locale) {
        List<StreamEntity> streamEntityList = remoteStreamListDataSource.getStreamList(locale);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformStreamEntitiesWithWatchers(streamEntityList, watchers);
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

    private List<StreamSearchEntity> transformStreamEntitiesInStreamSearchEntities(List<StreamEntity> eventEntities,
      Map<String, Integer> watchers) {
        List<StreamSearchEntity> results = new ArrayList<>(eventEntities.size());
        for (StreamEntity streamEntity : eventEntities) {
            StreamSearchEntity streamSearchEntity = transformStreamEntityInStreamSearchEntity(watchers, streamEntity);
            results.add(streamSearchEntity);
        }
        return results;
    }

    private StreamSearchEntity transformStreamEntityInStreamSearchEntity(Map<String, Integer> watchers,
      StreamEntity streamEntity) {
        StreamSearchEntity streamSearchEntity = new StreamSearchEntity();
        streamSearchEntity.setIdEvent(streamEntity.getIdEvent());
        streamSearchEntity.setIdUser(streamEntity.getIdUser());
        streamSearchEntity.setUserName(streamEntity.getUserName());
        streamSearchEntity.setTag(streamEntity.getTag());
        streamSearchEntity.setTitle(streamEntity.getTitle());
        streamSearchEntity.setPhoto(streamEntity.getPhoto());
        streamSearchEntity.setNotifyCreation(streamEntity.getNotifyCreation());
        streamSearchEntity.setLocale(streamEntity.getLocale());
        streamSearchEntity.setBirth(streamEntity.getBirth());
        streamSearchEntity.setDeleted(streamEntity.getDeleted());
        streamSearchEntity.setModified(streamEntity.getModified());
        streamEntity.setRevision(streamEntity.getRevision());
        if(watchers.get(streamEntity.getIdEvent()) != null) {
            streamSearchEntity.setWatchers(watchers.get(streamEntity.getIdEvent()));
        }
        return streamSearchEntity;
    }

    @Override public List<StreamSearchResult> getStreams(String query, String locale) {
        List<StreamEntity> streamEntityList = remoteStreamListDataSource.getStreams(query, locale);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();

        localEventSearchDataSource.setLastSearchResults(transformStreamEntitiesInStreamSearchEntities(streamEntityList,
          watchers));
        return transformStreamEntitiesWithWatchers(streamEntityList, watchers);
    }

    @Override public void putDefaultStreams(List<StreamSearchResult> streamSearchResults) {
        throw new IllegalStateException("Method not implemented in remote repository");
    }

    @Override public void deleteDefaultStreams() {
        throw new IllegalStateException("Method not implemented in remote repository");
    }

    @Override public List<StreamSearchResult> getStreamsListing(String listingIdUser) {
        List<StreamEntity> eventEntitiesListing = remoteStreamDataSource.getStreamsListing(listingIdUser);
        localStreamDataSource.putStreams(eventEntitiesListing);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformStreamEntitiesWithWatchers(eventEntitiesListing, watchers);
    }
}
