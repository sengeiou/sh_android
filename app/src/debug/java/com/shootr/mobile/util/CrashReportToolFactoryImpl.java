package com.shootr.mobile.util;

import android.content.Context;

public class CrashReportToolFactoryImpl implements CrashReportTool.Factory {

    @Override
    public CrashReportTool create() {
        return new StubCrashReportTool();
    }

    private static class StubCrashReportTool implements CrashReportTool {

        @Override
        public void init(Context context) {
            /* no-op */
        }

        @Override
        public void setUserId(String userId) {
            /* no-op */
        }

        @Override
        public void setUserName(String username) {
            /* no-op */
        }

        @Override
        public void setUserEmail(String email) {
            /* no-op */
        }

        @Override public void logException(Throwable error) {
            /* no-op */
        }

        @Override public void logException(String message) {
            /* no-op */
        }
    }
}
