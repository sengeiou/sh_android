package com.shootr.mobile.data.repository.datasource.log;

import android.content.Context;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.LogsApiService;
import com.shootr.mobile.data.repository.remote.cache.LogsCache;
import com.shootr.mobile.util.LogShootr;
import com.shootr.mobile.util.LogsTool;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class ServiceLogDatasource implements LogDataSource {

  static final String METHOD_NOT_VALID_FOR_SERVICE = "Method not valid for service";
  private final LogsApiService logsApiService;
  private final LogsTool logsTool;
  private final LogsCache logsCache;

  @Inject public ServiceLogDatasource(LogsApiService logsApiService, LogsTool logsTool,
      LogsCache logsCache) {
    this.logsApiService = logsApiService;
    this.logsTool = logsTool;
    this.logsCache = logsCache;
  }

  @Override public void putLog(String message, Context context) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public List<LogShootr> getLogs() {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public void sendLogs() throws IOException, ApiException {
    List<LogShootr> logs = logsCache.getLogs();
    if (logs.size() > 0) {
      Timber.d("Uploading Logins: %s logs", logs.size());
      logsApiService.uploadLogs(logsTool.sendLog(logs));
    }
  }
}