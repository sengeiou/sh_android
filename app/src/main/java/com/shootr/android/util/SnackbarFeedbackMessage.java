package com.shootr.android.util;

import android.support.design.widget.Snackbar;
import android.view.View;
import javax.inject.Inject;

public class SnackbarFeedbackMessage implements FeedbackMessage {

    @Inject public SnackbarFeedbackMessage() {
    }

    @Override public void show(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }

    @Override public void showLong(View view, String feedback) {
        Snackbar.make(view, feedback, Snackbar.LENGTH_LONG).show();
    }
}
