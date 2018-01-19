package com.shootr.mobile.data.repository.datasource;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.StreamApiService;
import com.shootr.mobile.data.repository.remote.cache.QueueElementCache;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.model.QueueElement;
import com.shootr.mobile.domain.model.QueueElementType;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceQueueElementDatasource implements QueueElementDataSource {

  private final StreamApiService streamApiService;
  private final QueueElementCache queueElementCache;

  @Inject public ServiceQueueElementDatasource(StreamApiService streamApiService,
      QueueElementCache queueElementCache) {
    this.streamApiService = streamApiService;
    this.queueElementCache = queueElementCache;
  }

  @Override public void sendQueue() {
    List<QueueElement> queueElementList = queueElementCache.getQueueElement();
    queueElementCache.invalidate();
    for (QueueElement queueElement : queueElementList) {
      switch (queueElement.getType()) {
        case QueueElementType.HIDE:
          sendHide(queueElement.getId());
          break;
      }
    }
  }

  @Override public void sendBackToQueue(String idStream, String queueElementType) {
    QueueElement queueElement = new QueueElement();
    queueElement.setId(idStream);
    queueElement.setType(queueElementType);
    queueElementCache.putQueueElement(queueElement);
  }

  private void sendHide(String idStream) {
    try {
      streamApiService.hide(idStream);
    } catch (ApiException | IOException cause) {
      sendBackToQueue(idStream, QueueElementType.HIDE);
      throw new ServerCommunicationException(cause);
    } catch (Exception e) {
      sendBackToQueue(idStream, QueueElementType.HIDE);
      throw new ServerCommunicationException(e);
    }
  }
}
