package com.shootr.mobile.data.repository.datasource.searchItem;

import com.shootr.mobile.data.entity.SearchableEntity;
import java.util.List;

public interface ExternalSearchItemDataSource {

  List<SearchableEntity> getSearch(String query, String[] searchTypes);
}
