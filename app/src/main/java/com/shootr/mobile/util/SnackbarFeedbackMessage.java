package com.shootr.mobile.util;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import javax.inject.Inject;

public class SnackbarFeedbackMessage implements FeedbackMessage {

    @Inject public SnackbarFeedbackMessage() {
    }

    @Override public void show(View view, String text) {
        if (view != null) {
            Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override public void showLong(View view, String feedback) {
        if (view != null) {
            Snackbar.make(view, feedback, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void show(View view, @StringRes int feedback) {
        if (view != null) {
            Snackbar.make(view, feedback, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showLong(View view, @StringRes int feedback) {
        if (view != null) {
            Snackbar.make(view, feedback, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override public void showMultipleActivities(View view, String multipleActivitiesMessage, String action, View.OnClickListener onClickListener) {
        Snackbar.make(view, multipleActivitiesMessage, Snackbar.LENGTH_LONG)
          .setAction(action, onClickListener)
          .show();
    }
}
