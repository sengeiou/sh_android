package com.shootr.mobile.util;

import android.content.Context;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class CrashlyticsReportTool implements CrashReportTool {

    @Override
    public void init(Context context) {
        Fabric.with(context, new Crashlytics());
    }

    @Override
    public void setUserId(String userId) {
        Crashlytics.setUserIdentifier(userId);
    }

    @Override
    public void setUserName(String username) {
        Crashlytics.setUserName(username);
    }

    @Override
    public void setUserEmail(String email) {
        Crashlytics.setUserEmail(email);
    }

    @Override public void logException(Throwable error) {
        Crashlytics.logException(error);
    }
}
