package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.repository.datasource.log.LogDataSource;
import com.shootr.mobile.data.repository.remote.cache.LogsCache;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.log.LogRepository;
import java.io.IOException;
import javax.inject.Inject;

public class RemoteLogRepository implements LogRepository {

  private final LogDataSource logDataSource;
  private final LogsCache logsCache;

  @Inject public RemoteLogRepository(@Remote LogDataSource logDataSource, LogsCache logsCache) {
    this.logDataSource = logDataSource;
    this.logsCache = logsCache;
  }

  @Override public void sendLogs() {
    try {
      logDataSource.sendLogs();
      logsCache.invalidate();
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }
}
