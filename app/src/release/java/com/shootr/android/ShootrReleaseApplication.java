package com.shootr.android;

import com.shootr.android.util.CrashReportTool;
import javax.inject.Inject;

public class ShootrReleaseApplication extends ShootrApplication {

    @Inject CrashReportTool crashReportTool;

    @Override
    public void onCreate() {
        super.onCreate();
        crashReportTool.init(this);
    }
}
