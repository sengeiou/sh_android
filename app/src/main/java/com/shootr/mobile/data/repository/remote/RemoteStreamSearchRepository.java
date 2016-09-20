package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.StreamSearchEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.stream.DatabaseMemoryStreamSearchDataSource;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.data.repository.datasource.stream.StreamListDataSource;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.StreamSearchRepository;
import com.shootr.mobile.domain.repository.WatchersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class RemoteStreamSearchRepository implements StreamSearchRepository {

    @Deprecated private final DatabaseMemoryStreamSearchDataSource localStreamSearchDataSource;
    private final StreamListDataSource remoteStreamListDataSource;
    private final WatchersRepository localWatchersRepository;
    private final StreamEntityMapper streamEntityMapper;
    private final StreamDataSource localStreamDataSource;
    private final StreamDataSource remoteStreamDataSource;

    @Inject public RemoteStreamSearchRepository(DatabaseMemoryStreamSearchDataSource localStreamSearchDataSource,
      @Remote StreamListDataSource remoteStreamListDataSource, @Local WatchersRepository localWatchersRepository,
      StreamEntityMapper streamEntityMapper, @Local StreamDataSource localStreamDataSource,
      @Remote StreamDataSource remoteStreamDataSource) {
        this.localStreamSearchDataSource = localStreamSearchDataSource;
        this.remoteStreamListDataSource = remoteStreamListDataSource;
        this.localWatchersRepository = localWatchersRepository;
        this.streamEntityMapper = streamEntityMapper;
        this.localStreamDataSource = localStreamDataSource;
        this.remoteStreamDataSource = remoteStreamDataSource;
    }

    @Override public List<StreamSearchResult> getDefaultStreams(String locale, String[] types) {
        List<StreamEntity> streamEntityList = remoteStreamListDataSource.getStreamList(locale, types);
        localStreamDataSource.putStreams(streamEntityList);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformStreamEntitiesWithWatchers(streamEntityList, watchers);
    }

    private List<StreamSearchResult> transformStreamEntitiesWithWatchers(List<StreamEntity> streamEntities,
      Map<String, Integer> watchers) {
        List<StreamSearchResult> results = new ArrayList<>(streamEntities.size());
        for (StreamEntity streamEntity : streamEntities) {
            Stream stream = streamEntityMapper.transform(streamEntity);
            Integer streamWatchers = watchers.get(stream.getId());
            StreamSearchResult streamSearchResult =
              new StreamSearchResult(stream, streamWatchers != null ? streamWatchers : 0);
            results.add(streamSearchResult);
        }
        return results;
    }

    private List<StreamSearchEntity> transformStreamEntitiesInStreamSearchEntities(List<StreamEntity> streamEntities,
      Map<String, Integer> watchers) {
        List<StreamSearchEntity> results = new ArrayList<>(streamEntities.size());
        for (StreamEntity streamEntity : streamEntities) {
            StreamSearchEntity streamSearchEntity = transformStreamEntityInStreamSearchEntity(watchers, streamEntity);
            results.add(streamSearchEntity);
        }
        return results;
    }

    private StreamSearchEntity transformStreamEntityInStreamSearchEntity(Map<String, Integer> watchers,
      StreamEntity streamEntity) {
        StreamSearchEntity streamSearchEntity = new StreamSearchEntity();
        streamSearchEntity.setIdStream(streamEntity.getIdStream());
        streamSearchEntity.setIdUser(streamEntity.getIdUser());
        streamSearchEntity.setUserName(streamEntity.getUserName());
        streamSearchEntity.setTitle(streamEntity.getTitle());
        streamSearchEntity.setPhoto(streamEntity.getPhoto());
        streamSearchEntity.setNotifyCreation(streamEntity.getNotifyCreation());
        streamSearchEntity.setRemoved(streamEntity.getRemoved());
        streamSearchEntity.setCountry(streamEntity.getCountry());
        streamSearchEntity.setBirth(streamEntity.getBirth());
        streamSearchEntity.setDeleted(streamEntity.getDeleted());
        streamSearchEntity.setModified(streamEntity.getModified());
        streamEntity.setRevision(streamEntity.getRevision());
        if (watchers.get(streamEntity.getIdStream()) != null) {
            streamSearchEntity.setTotalFollowingWatchers(watchers.get(streamEntity.getIdStream()));
        }
        return streamSearchEntity;
    }

    @Override public List<StreamSearchResult> getStreams(String query, String locale, String[] types) {
        List<StreamEntity> streamEntityList = remoteStreamListDataSource.getStreams(query, locale, types);
        localStreamDataSource.putStreams(streamEntityList);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();

        localStreamSearchDataSource.setLastSearchResults(transformStreamEntitiesInStreamSearchEntities(streamEntityList,
          watchers));
        return transformStreamEntitiesWithWatchers(streamEntityList, watchers);
    }

    @Override public void putDefaultStreams(List<StreamSearchResult> streamSearchResults) {
        throw new IllegalStateException("Method not implemented in remote repository");
    }

    @Override public void deleteDefaultStreams() {
        throw new IllegalStateException("Method not implemented in remote repository");
    }

    @Override public List<StreamSearchResult> getStreamsListing(String listingIdUser, String[] types) {
        List<StreamEntity> streamEntitiesListing = remoteStreamDataSource.getStreamsListing(listingIdUser, types);
        localStreamDataSource.putStreams(streamEntitiesListing);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformStreamEntitiesWithWatchers(streamEntitiesListing, watchers);
    }
}
