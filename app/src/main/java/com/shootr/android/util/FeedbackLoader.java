package com.shootr.android.util;

import android.support.annotation.UiThread;
import android.view.View;

public interface FeedbackLoader {

    @UiThread
    void show(View view, String feedback);

    @UiThread
    void showLong(View view, String feedback);

}
