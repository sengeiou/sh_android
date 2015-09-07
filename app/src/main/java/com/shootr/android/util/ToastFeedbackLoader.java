package com.shootr.android.util;

import android.content.Context;
import android.widget.Toast;
import javax.inject.Inject;

public class ToastFeedbackLoader implements FeedbackLoader {

    @Inject public ToastFeedbackLoader() {
    }

    @Override public void showShortFeedback(Context context, String feedback) {
        Toast.makeText(context, feedback, Toast.LENGTH_SHORT).show();
    }

    @Override public void showLongFeedback(Context context, String feedback) {
        Toast.makeText(context, feedback, Toast.LENGTH_LONG).show();
    }
}
