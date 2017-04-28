package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.SearchableModel;
import java.util.List;

public interface SearchView {

  void renderSearch(List<SearchableModel> searchableModelList);

  void renderUsersSearch(List<SearchableModel> searchableModelList);

  void renderStreamsSearch(List<SearchableModel> searchableModelList);
}
