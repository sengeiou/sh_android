package com.shootr.android.util;

import android.app.Activity;
import android.content.Intent;
import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.ui.ExternalIntentActivity;
import com.shootr.android.ui.model.ShotModel;

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

    @Override
    public Intent openUrlIntent(String url) {
        Intent baseIntent = realIntentFactory.openUrlIntent(url);
        return createCaptureIntent(baseIntent);
    }

    @Override
    public Intent shareShotIntent(Activity launchActivity, ShotModel shotModel) {
        Intent baseIntent = realIntentFactory.shareShotIntent(launchActivity, shotModel);
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