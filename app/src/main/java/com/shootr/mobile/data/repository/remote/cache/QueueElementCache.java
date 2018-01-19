package com.shootr.mobile.data.repository.remote.cache;

import com.shootr.mobile.data.repository.datasource.CachedDataSource;
import com.shootr.mobile.domain.model.QueueElement;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class QueueElementCache implements CachedDataSource {

  private static final String QUEUE_ELEMENT = "queue_element";
  private final DualCache<List<QueueElement>> shootrQueueLruCache;

  @Inject public QueueElementCache(DualCache<List<QueueElement>> shootrQueueLruCache) {
    this.shootrQueueLruCache = shootrQueueLruCache;
  }

  public List<QueueElement> getQueueElement() {
    return shootrQueueLruCache.get(QUEUE_ELEMENT);
  }

  public void putQueueElement(QueueElement queueElement) {
    List<QueueElement> queueElements = getQueueElement();
    shootrQueueLruCache.invalidate();
    queueElements.add(queueElement);
    shootrQueueLruCache.put(QUEUE_ELEMENT, queueElements);
  }

  @Override public boolean isValid() {
    return false;
  }

  @Override public void invalidate() {
    shootrQueueLruCache.invalidate();
  }
}
