package com.shootr.mobile.ui.fragments;

import com.shootr.mobile.ui.model.SearchableModel;
import java.util.List;

public interface SearchFragment {

  void renderSearchItems(List<SearchableModel> searchableModels);
}
