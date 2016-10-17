package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.StreamResultModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

@Singleton public class StreamResultModelMapper {

  private final StreamModelMapper streamModelMapper;

  @Inject public StreamResultModelMapper(StreamModelMapper streamModelMapper) {
    this.streamModelMapper = streamModelMapper;
  }

  public StreamResultModel transform(StreamSearchResult streamSearchResult) {
    if (streamSearchResult == null || streamSearchResult.getStream() == null) {
      return null;
    }
    checkNotNull(streamSearchResult.getStream());
    StreamModel streamModel = streamModelMapper.transform(streamSearchResult.getStream());

    StreamResultModel resultModel = new StreamResultModel();
    resultModel.setStreamModel(streamModel);
    resultModel.setWatchers(streamSearchResult.getFollowingWatchersNumber());
    resultModel.setIsWatching(streamSearchResult.isWatching());
    return resultModel;
  }

  public List<StreamResultModel> transform(List<StreamSearchResult> streamSearchResults) {
    List<StreamResultModel> models = new ArrayList<>(streamSearchResults.size());
    for (StreamSearchResult streamSearchResult : streamSearchResults) {
      if (streamSearchResult.getStream() != null
          && streamSearchResult.getStream().getId() != null) {
        models.add(transform(streamSearchResult));
      }
    }
    return models;
  }

  public List<StreamResultModel> transformWithFavorites(List<StreamSearchResult> streamSearchResults,
      List<String> favorites) {
    List<StreamResultModel> models = new ArrayList<>(streamSearchResults.size());
    for (StreamSearchResult streamSearchResult : streamSearchResults) {
      if (streamSearchResult.getStream() != null
          && streamSearchResult.getStream().getId() != null) {
        StreamResultModel streamResultModel = transform(streamSearchResult);
        if (favorites != null && favorites.contains(streamSearchResult.getStream().getId())) {
          streamResultModel.setFavorited(true);
        } else {
          streamResultModel.setFavorited(false);
        }
        models.add(streamResultModel);
      }
    }
    return models;
  }
}
