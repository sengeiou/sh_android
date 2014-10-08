package gm.mobi.android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import gm.mobi.android.BuildConfig;
import timber.log.Timber;

public class VersionUtils {

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

    private static PackageInfo getPackageInfo(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(BuildConfig.PACKAGE_NAME, 0);
    }
}
