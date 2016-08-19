package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import java.util.List;

public interface RecentStreamRepository {

  void putRecentStream(Stream stream, long currentTime);

  void removeRecentStream(String idStream);

  List<StreamSearchResult> getDefaultStreams();
}
