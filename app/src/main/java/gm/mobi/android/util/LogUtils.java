package gm.mobi.android.util;

import android.util.Log;

import gm.mobi.android.BuildConfig;

public class LogUtils {
    private static final String TAG = "Goles";

    public static void d(String message) {
        if (BuildConfig.DEBUG) Log.d(TAG, message);
    }

    public static void v(String message) {
        Log.v(TAG, message);
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void w(String message) {
        Log.w(TAG, message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }

    public static void e(String message, Throwable tr) {
        Log.e(TAG, message, tr);
    }

    public static void wtf(String message) {
        Log.wtf(TAG, message);
    }

    public static void wtf(String message, Throwable tr) {
        Log.wtf(TAG, message, tr);
    }

}
