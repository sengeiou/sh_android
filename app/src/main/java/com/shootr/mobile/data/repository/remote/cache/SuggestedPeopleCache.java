package com.shootr.mobile.data.repository.remote.cache;

import android.support.v4.util.LruCache;
import com.shootr.mobile.data.repository.datasource.CachedDataSource;
import com.shootr.mobile.domain.model.user.SuggestedPeople;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SuggestedPeopleCache implements CachedDataSource {

  private final LruCache<String, SuggestedPeople> suggestedPeopleLruCache;

  @Inject public SuggestedPeopleCache(LruCache<String, SuggestedPeople> suggestedPeopleLruCache) {
    this.suggestedPeopleLruCache = suggestedPeopleLruCache;
  }

  public List<SuggestedPeople> getSuggestedPeople() {
    Collection<SuggestedPeople> suggestedPeopleCollection;
    Map<String, SuggestedPeople> map = suggestedPeopleLruCache.snapshot();
    suggestedPeopleCollection = map.values();
    List<SuggestedPeople> suggestedPeopleList = new ArrayList<>(suggestedPeopleCollection.size());

    Iterator<SuggestedPeople> suggestedPeopleIterator = suggestedPeopleCollection.iterator();
    while (suggestedPeopleIterator.hasNext()) {
      suggestedPeopleList.add(suggestedPeopleIterator.next());
    }

    return suggestedPeopleList;
  }

  public void putSuggestedPeopleList(List<SuggestedPeople> suggestedPeopleList) {
    suggestedPeopleLruCache.evictAll();
    for (SuggestedPeople suggestedPeople : suggestedPeopleList) {
      suggestedPeopleLruCache.put(suggestedPeople.getUser().getIdUser(), suggestedPeople);
    }
  }

  @Override public boolean isValid() {
    return false;
  }

  @Override public void invalidate() {
    suggestedPeopleLruCache.evictAll();
  }
}
