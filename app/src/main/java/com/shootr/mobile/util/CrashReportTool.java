package com.shootr.mobile.util;

import android.content.Context;

public interface CrashReportTool {

    void init(Context context);

    void setUserId(String userId);

    void setUserName(String username);

    void setUserEmail(String email);

    void logException(Throwable error);

    void logException(String message);

    interface Factory {

        CrashReportTool create();

    }
}
