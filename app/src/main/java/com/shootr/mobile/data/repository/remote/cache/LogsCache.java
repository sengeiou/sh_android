package com.shootr.mobile.data.repository.remote.cache;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import com.shootr.mobile.data.repository.datasource.CachedDataSource;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.util.LogShootr;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LogsCache implements CachedDataSource {

  private static final String LOGS = "logs";
  private static final String ANDROID = "android";
  private final DualCache<List<LogShootr>> logShootrDualCache;
  private final SessionRepository sessionRepository;

  @Inject public LogsCache(DualCache<List<LogShootr>> logShootrDualCache,
      SessionRepository sessionRepository) {
    this.logShootrDualCache = logShootrDualCache;
    this.sessionRepository = sessionRepository;
  }

  public List<LogShootr> getLogs() {
    try {
      return logShootrDualCache.get(LOGS);
    } catch (IllegalStateException error) {
      return null;
    }
  }

  public void putNewLog(String message, Context context) {
    LogShootr logShootr = buildNewLog(message, context);

    List<LogShootr> logShootrs = getLogs();
    logShootrDualCache.invalidate();
    if (logShootrs == null) {
      logShootrs = new ArrayList<>();
    }
    logShootrs.add(logShootr);
    logShootrDualCache.put(LOGS, logShootrs);
  }

  private LogShootr buildNewLog(String message, Context context) {
    LogShootr logShootr = new LogShootr();
    logShootr.setTimestamp(new Date().getTime());
    logShootr.setPlatform(ANDROID);
    try {
      logShootr.setVersion(
          context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    logShootr.setApp(context.getPackageName());
    logShootr.setMessage(message);
    logShootr.setUserName(sessionRepository.getCurrentUser().getUsername());
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    logShootr.setNetworkType(cm.getActiveNetworkInfo().getTypeName());
    logShootr.setNetworkStatus(cm.getActiveNetworkInfo().getState().name());
    logShootr.setModel(Build.MANUFACTURER + " " + Build.MODEL);
    return logShootr;
  }

  @Override public boolean isValid() {
    return false;
  }

  @Override public void invalidate() {
    logShootrDualCache.invalidate();
  }
}
