package com.shootr.mobile.domain.repository.searchItem;

import com.shootr.mobile.domain.model.Searchable;
import java.util.List;

public interface ExternalSearchItemRepository {

  List<Searchable> getSearch(String query, String[] types);
}
