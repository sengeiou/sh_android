package com.shootr.mobile.data.repository.datasource.log;

import android.content.Context;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.util.LogShootr;
import java.io.IOException;
import java.util.List;

public interface LogDataSource {

  void putLog(String message, Context context);

  List<LogShootr> getLogs();

  void sendLogs() throws IOException, ApiException;
}
