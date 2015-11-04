package com.shootr.mobile.util;

import com.shootr.mobile.util.CrashReportTool;

public class CrashReportToolFactoryImpl implements CrashReportTool.Factory{

    @Override
    public CrashReportTool create() {
        return new CrashlyticsReportTool();
    }
}
