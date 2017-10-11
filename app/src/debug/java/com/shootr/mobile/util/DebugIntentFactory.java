package com.shootr.mobile.util;

import android.app.Activity;
import android.content.Intent;
import com.shootr.mobile.data.prefs.BooleanPreference;
import com.shootr.mobile.ui.ExternalIntentActivity;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;

/**
 * An {@link IntentFactory} implementation that wraps all {@code Intent}s with a debug action, which
 * launches an activity that allows you to inspect the content.
 */
public final class DebugIntentFactory implements IntentFactory {

    private final IntentFactory realIntentFactory;
    private final BooleanPreference captureIntents;

    public DebugIntentFactory(IntentFactory realIntentFactory, BooleanPreference captureIntents) {
        this.realIntentFactory = realIntentFactory;
        this.captureIntents = captureIntents;
    }

    @Override public Intent openUrlIntent(String url) {
        Intent baseIntent = realIntentFactory.openUrlIntent(url);
        return createCaptureIntent(baseIntent);
    }

    @Override public Intent openEmbededUrlIntent(Activity launchActivity, String url) {
        Intent baseIntent = realIntentFactory.openEmbededUrlIntent(launchActivity, url);
        return createCaptureIntent(baseIntent);
    }

    @Override public Intent shareShotIntent(Activity activity, ShotModel shotModel, String locale) {
        Intent baseIntent = realIntentFactory.shareShotIntent(activity, shotModel, locale);
        return createCaptureIntent(baseIntent);
    }

    @Override public Intent shareStreamIntent(Activity launchActivity, StreamModel streamModel,
        String locale) {
        Intent baseIntent = realIntentFactory.shareStreamIntent(launchActivity, streamModel, locale);
        return createCaptureIntent(baseIntent);
    }

    @Override public Intent reportEmailIntent(Activity launchActivity, String currentUserId, String reportedUserId) {
        Intent baseIntent = realIntentFactory.reportEmailIntent(launchActivity, currentUserId, reportedUserId);
        return createCaptureIntent(baseIntent);
    }

    @Override public Intent sharePollIntent(Activity activity, PollModel pollModel, String locale) {
        Intent baseIntent = realIntentFactory.sharePollIntent(activity, pollModel, locale);
        return createCaptureIntent(baseIntent);
    }

  @Override public Intent sharePollVotedIntent(Activity activity, PollModel pollModel,
      PollOptionModel pollOptionModel, String locale) {
    Intent baseIntent = realIntentFactory.sharePollVotedIntent(activity, pollModel, pollOptionModel, locale);
    return createCaptureIntent(baseIntent);
  }

  private Intent createCaptureIntent(Intent baseIntent) {
        if (!captureIntents.get()) {
            return baseIntent;
        } else {
            return ExternalIntentActivity.createIntent(baseIntent);
        }
    }
}
