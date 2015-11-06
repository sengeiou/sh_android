package com.shootr.mobile.util;

public class CrashReportToolFactoryImpl implements CrashReportTool.Factory{

    @Override
    public CrashReportTool create() {
        return new CrashlyticsReportTool();
    }
}
