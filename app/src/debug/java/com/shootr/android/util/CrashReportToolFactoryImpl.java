package com.shootr.android.util;

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
    }
}
