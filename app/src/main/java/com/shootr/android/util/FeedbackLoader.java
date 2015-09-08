package com.shootr.android.util;

import android.support.annotation.UiThread;
import android.view.View;

public interface FeedbackLoader {

    @UiThread
    void showShortFeedback(View view, String feedback);

    @UiThread
    void showLongFeedback(View view, String feedback);

}
