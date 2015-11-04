package com.shootr.mobile.util;

import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.view.View;

public interface FeedbackMessage {

    @UiThread
    void show(View view, String feedback);

    @UiThread
    void showLong(View view, String feedback);

    @UiThread
    void show(View view, @StringRes int feedback);

    @UiThread
    void showLong(View view, @StringRes int feedback);

}
