package com.shootr.mobile.domain.repository.stream;

import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import java.util.List;

public interface InternalStreamSearchRepository extends StreamSearchRepository {

  void putDefaultStreams(List<StreamSearchResult> streamSearchResults);

  void deleteDefaultStreams();
}
