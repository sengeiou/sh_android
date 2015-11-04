package com.shootr.mobile.util;

import android.app.Activity;
import android.content.Intent;
import com.shootr.mobile.data.prefs.BooleanPreference;
import com.shootr.mobile.ui.ExternalIntentActivity;
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

    @Override
    public Intent openUrlIntent(String url) {
        Intent baseIntent = realIntentFactory.openUrlIntent(url);
        return createCaptureIntent(baseIntent);
    }

    @Override
    public Intent openEmbededUrlIntent(Activity launchActivity, String url) {
        Intent baseIntent = realIntentFactory.openEmbededUrlIntent(launchActivity, url);
        return createCaptureIntent(baseIntent);
    }

    @Override
    public Intent shareShotIntent(Activity launchActivity, ShotModel shotModel) {
        Intent baseIntent = realIntentFactory.shareShotIntent(launchActivity, shotModel);
        return createCaptureIntent(baseIntent);
    }

    @Override
    public Intent shareStreamIntent(Activity launchActivity, StreamModel streamModel) {
        Intent baseIntent = realIntentFactory.shareStreamIntent(launchActivity, streamModel);
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
