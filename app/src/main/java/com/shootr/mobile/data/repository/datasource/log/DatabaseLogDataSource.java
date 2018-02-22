package com.shootr.mobile.data.repository.datasource.log;

import android.content.Context;
import com.shootr.mobile.data.repository.remote.cache.LogsCache;
import com.shootr.mobile.util.LogShootr;
import java.util.List;
import javax.inject.Inject;

public class DatabaseLogDataSource implements LogDataSource {

  public static final String METHOD_NOT_VALID_FOR_SERVICE = "Method not valid for service";
  private final LogsCache logsCache;

  @Inject public DatabaseLogDataSource(LogsCache logsCache) {
    this.logsCache = logsCache;
  }

  @Override public void putLog(String message, Context context) {
    logsCache.putNewLog(message, context);
  }

  @Override public List<LogShootr> getLogs() {
    List<LogShootr> logShootr = logsCache.getLogs();
    return logShootr;
  }

  @Override public void sendLogs() {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }
}
