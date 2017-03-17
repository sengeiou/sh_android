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
import com.shootr.mobile.domain.repository.stream.StreamSearchRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class RemoteStreamSearchRepository implements StreamSearchRepository {

  @Deprecated private final DatabaseMemoryStreamSearchDataSource localStreamSearchDataSource;
  private final StreamListDataSource remoteStreamListDataSource;
  private final StreamEntityMapper streamEntityMapper;
  private final StreamDataSource localStreamDataSource;
  private final StreamDataSource remoteStreamDataSource;

  @Inject public RemoteStreamSearchRepository(
      DatabaseMemoryStreamSearchDataSource localStreamSearchDataSource,
      @Remote StreamListDataSource remoteStreamListDataSource,
      StreamEntityMapper streamEntityMapper, @Local StreamDataSource localStreamDataSource,
      @Remote StreamDataSource remoteStreamDataSource) {
    this.localStreamSearchDataSource = localStreamSearchDataSource;
    this.remoteStreamListDataSource = remoteStreamListDataSource;
    this.streamEntityMapper = streamEntityMapper;
    this.localStreamDataSource = localStreamDataSource;
    this.remoteStreamDataSource = remoteStreamDataSource;
  }

  @Override public List<StreamSearchResult> getDefaultStreams(String locale, String[] types) {
    List<StreamEntity> streamEntityList = remoteStreamListDataSource.getStreamList(locale, types);
    localStreamDataSource.putStreams(streamEntityList);
    return transformStreamEntitiesWithWatchers(streamEntityList);
  }

  private List<StreamSearchResult> transformStreamEntitiesWithWatchers(
      List<StreamEntity> streamEntities) {
    List<StreamSearchResult> results = new ArrayList<>(streamEntities.size());
    for (StreamEntity streamEntity : streamEntities) {
      Stream stream = streamEntityMapper.transform(streamEntity);
      StreamSearchResult streamSearchResult = new StreamSearchResult(stream);
      results.add(streamSearchResult);
    }
    return results;
  }

  private List<StreamSearchEntity> transformStreamEntitiesInStreamSearchEntities(
      List<StreamEntity> streamEntities) {
    List<StreamSearchEntity> results = new ArrayList<>(streamEntities.size());
    for (StreamEntity streamEntity : streamEntities) {
      StreamSearchEntity streamSearchEntity =
          transformStreamEntityInStreamSearchEntity(streamEntity);
      results.add(streamSearchEntity);
    }
    return results;
  }

  private StreamSearchEntity transformStreamEntityInStreamSearchEntity(StreamEntity streamEntity) {
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

    return streamSearchEntity;
  }

  @Override
  public List<StreamSearchResult> getStreams(String query, String locale, String[] types) {
    List<StreamEntity> streamEntityList =
        remoteStreamListDataSource.getStreams(query, locale, types);
    localStreamDataSource.putStreams(streamEntityList);

    localStreamSearchDataSource.setLastSearchResults(
        transformStreamEntitiesInStreamSearchEntities(streamEntityList));
    return transformStreamEntitiesWithWatchers(streamEntityList);
  }

  @Override
  public List<StreamSearchResult> getStreamsListing(String listingIdUser, String[] types) {
    List<StreamEntity> streamEntitiesListing =
        remoteStreamDataSource.getStreamsListing(listingIdUser, types);
    localStreamDataSource.putStreams(streamEntitiesListing);
    return transformStreamEntitiesWithWatchers(streamEntitiesListing);
  }
}
