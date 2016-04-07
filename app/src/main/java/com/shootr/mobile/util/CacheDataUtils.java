package com.shootr.mobile.util;

import android.content.Context;
import java.io.File;
import javax.inject.Inject;

public class CacheDataUtils implements CacheUtils {

    private final CrashReportTool crashReportTool;

    @Inject
    public CacheDataUtils(CrashReportTool crashReportTool) {
        this.crashReportTool = crashReportTool;
    }

    @Override
    public void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            crashReportTool.logException("Error deleting cache when logging out");
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }
}
