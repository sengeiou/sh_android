package com.shootr.mobile.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.shootr.mobile.BuildConfig;
import timber.log.Timber;

public class VersionUtils {

  private VersionUtils() {
  }

  public static int getVersionCode(Context context) {
    try {
      return getPackageInfo(context).versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      Timber.e("Error obteniendo version code, devolviendo -1", e);
      return -1;
    }
  }

  public static String getVersionName(Context context) {
    try {
      return getPackageInfo(context).versionName;
    } catch (PackageManager.NameNotFoundException e) {
      Timber.e("Error obteniendo version name, devolviendo 0.0", e);
      return "0.0";
    }
  }

  public static String getDBVersion(Context context) {
    try {
      return String.valueOf(BuildConfig.DATABASE_VERSION);
    } catch (Exception e) {
      Timber.e("Error obteniendo version name, devolviendo 0", e);
      return "0";
    }
  }

  private static PackageInfo getPackageInfo(Context context)
      throws PackageManager.NameNotFoundException {
    return context.getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, 0);
  }
}
