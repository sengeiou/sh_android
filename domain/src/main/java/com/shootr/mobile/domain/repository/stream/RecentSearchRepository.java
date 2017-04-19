package com.shootr.mobile.domain.repository.stream;

import com.shootr.mobile.domain.model.Searchable;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.User;
import java.util.List;

public interface RecentSearchRepository {

  void putRecentStream(Stream stream, long currentTime);

  void putRecentUser(User user, long currentTime);

  List<Searchable> getDefaultSearch();
}
