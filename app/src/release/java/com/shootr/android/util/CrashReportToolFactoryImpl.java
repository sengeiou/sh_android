package com.shootr.android.util;

public class CrashReportToolFactoryImpl implements CrashReportTool.Factory{

    @Override
    public CrashReportTool create() {
        return new CrashlyticsReportTool();
    }
}
