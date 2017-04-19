package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.searchItem.GetSearchItemsInteractor;
import com.shootr.mobile.domain.interactor.stream.GetRecentSearchInteractor;
import com.shootr.mobile.domain.model.Searchable;
import com.shootr.mobile.domain.model.SearchableType;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.ui.model.SearchableModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.SearchView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SearchPresenter implements Presenter {

  public static Integer TYPE_ALL = 0;
  public static int TYPE_STREAM = 1;
  public static int TYPE_USER = 2;

  private final GetSearchItemsInteractor getSearchItemsInteractor;
  private final GetRecentSearchInteractor getRecentSearchInteractor;
  private final UserModelMapper userModelMapper;
  private final StreamModelMapper streamModelMapper;

  private SearchView view;
  private String currentQuery = "";
  private int currentType = 0;
  private boolean hasBeenPaused = false;
  private boolean hasBeenSearched;
  private List<SearchableModel> searchableModelList = new ArrayList<>();

  @Inject public SearchPresenter(GetSearchItemsInteractor getSearchItemsInteractor,
      GetRecentSearchInteractor getRecentSearchInteractor, UserModelMapper userModelMapper,
      StreamModelMapper streamModelMapper) {
    this.getSearchItemsInteractor = getSearchItemsInteractor;
    this.getRecentSearchInteractor = getRecentSearchInteractor;
    this.userModelMapper = userModelMapper;
    this.streamModelMapper = streamModelMapper;
  }

  public void initialize(SearchView searchView) {
    this.view = searchView;
  }

  public void initialSearch(final int type) {
    getRecentSearchInteractor.loadSearches(new Interactor.Callback<List<Searchable>>() {
      @Override public void onLoaded(List<Searchable> searchables) {
        mapSearch(searchables);
        filterSearch(type);
      }
    });
  }

  public void search(String query, final int type) {
    if (query != null && !currentQuery.equals(query) && !query.isEmpty()) {
      hasBeenSearched = true;
      currentQuery = query;
      searchItems(query, type);
    } else if (query.isEmpty()) {
      hasBeenSearched = false;
      initialSearch(type);
    } else {
      filterSearch(type);
    }
  }

  private void searchItems(String query, final int type) {
    getSearchItemsInteractor.searchItems(query, new Interactor.Callback<List<Searchable>>() {
      @Override public void onLoaded(List<Searchable> searchables) {
        mapSearch(searchables);
        filterSearch(type);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
      /* no-op */
      }
    });
  }

  public void filterSearch(int type) {
    currentType = type;
    if (type == TYPE_ALL) {
      view.renderSearch(searchableModelList);
    } else if (type == TYPE_USER) {
      renderUsers();
    } else if (type == TYPE_STREAM) {
      renderStreams();
    }
  }

  private void renderStreams() {
    ArrayList<SearchableModel> streams = new ArrayList<>();
    for (SearchableModel searchableModel : searchableModelList) {
      if (searchableModel.getSearchableType().equals(SearchableType.STREAM)) {
        streams.add(searchableModel);
      }
    }
    view.renderStreamsSearch(streams);
  }

  private void renderUsers() {
    ArrayList<SearchableModel> users = new ArrayList<>();
    for (SearchableModel searchableModel : searchableModelList) {
      if (searchableModel.getSearchableType().equals(SearchableType.USER)) {
        users.add(searchableModel);
      }
    }
    view.renderUsersSearch(users);
  }

  private void mapSearch(List<Searchable> searchables) {
    searchableModelList.clear();
    for (Searchable searchable : searchables) {
      switch (searchable.getSearchableType()) {
        case SearchableType.USER:
          searchableModelList.add(userModelMapper.transform((User) searchable));
          break;
        case SearchableType.STREAM:
          searchableModelList.add(streamModelMapper.transform((Stream) searchable));
          break;
        default:
          break;
      }
    }
  }

  @Override public void resume() {
    if (hasBeenPaused) {
      if (hasBeenSearched) {
        searchItems(currentQuery, currentType);
      } else {
        initialSearch(currentType);
      }
    }
  }

  @Override public void pause() {
    hasBeenPaused = true;
  }
}
