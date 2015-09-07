package com.shootr.android.util;

import android.content.Context;
import android.support.annotation.UiThread;

public interface FeedbackLoader {

    @UiThread
    void showShortFeedback(Context context, String feedback);

    @UiThread
    void showLongFeedback(Context context, String feedback);

}
