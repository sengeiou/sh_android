package com.shootr.mobile.util;

import com.crashlytics.android.Crashlytics;
import timber.log.Timber;

public class CrashReportingTree extends Timber.HollowTree {

    @Override public void i(String message, Object... args) {
        Crashlytics.log("I/: "+String.format(message,args));
    }

    @Override public void i(Throwable t, String message, Object... args) {
        Crashlytics.log("I/: "+String.format(message,args));
    }

    @Override public void e(String message, Object... args) {
        Crashlytics.log("E/: "+String.format(message,args));
    }

    @Override public void e(Throwable t, String message, Object... args) {
        Crashlytics.log("E/: "+String.format(message, args));
    }

}

