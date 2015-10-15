package com.shootr.android.util;

import android.content.Context;

public class AnalyticsToolFactoryImpl implements AnalyticsTool.Factory {

    @Override
    public AnalyticsTool create() {
        return new StubAnalyticsTool();
    }

    private static class StubAnalyticsTool implements AnalyticsTool {

        @Override
        public void init(Context context) {
            /* no-op */
        }
    }
}
