package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.mapper.StreamSearchEntityMapper;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.data.repository.datasource.stream.StreamSearchDataSource;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.InternalStreamSearchRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class LocalStreamSearchRepository implements InternalStreamSearchRepository {

  private final StreamSearchDataSource localStreamSearchDataSource;
  private final StreamSearchEntityMapper streamSearchEntityMapper;
  private final StreamDataSource localStreamDataSource;
  private final StreamEntityMapper streamEntityMapper;

  @Inject
  public LocalStreamSearchRepository(@Local StreamSearchDataSource localStreamSearchDataSource,
      StreamSearchEntityMapper streamSearchEntityMapper,
      @Local StreamDataSource localStreamDataSource, StreamEntityMapper streamEntityMapper) {
    this.localStreamSearchDataSource = localStreamSearchDataSource;
    this.streamSearchEntityMapper = streamSearchEntityMapper;
    this.localStreamDataSource = localStreamDataSource;
    this.streamEntityMapper = streamEntityMapper;
  }

  @Override
  public List<StreamSearchResult> getStreamsListing(String listingIdUser, String[] types) {
    List<StreamEntity> eventEntitiesListing =
        localStreamDataSource.getStreamsListing(listingIdUser, types);
    return transformStreamEntitiesWithWatchers(eventEntitiesListing);
  }

  private List<StreamSearchResult> transformStreamEntitiesWithWatchers(
      List<StreamEntity> eventEntities) {
    List<StreamSearchResult> results = new ArrayList<>(eventEntities.size());
    for (StreamEntity streamEntity : eventEntities) {
      Stream stream = streamEntityMapper.transform(streamEntity);

      StreamSearchResult streamSearchResult = new StreamSearchResult(stream);
      results.add(streamSearchResult);
    }
    return results;
  }
}
