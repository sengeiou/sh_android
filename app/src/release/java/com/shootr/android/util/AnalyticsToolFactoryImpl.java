package com.shootr.android.util;

public class AnalyticsToolFactoryImpl implements AnalyticsTool.Factory {

    @Override
    public AnalyticsTool create() {
        return new GoogleAnalyticsTool();
    }
}
