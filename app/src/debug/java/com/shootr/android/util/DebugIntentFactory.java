package com.shootr.android.util;

import android.content.Intent;
import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.ui.ExternalIntentActivity;

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
    public Intent createUrlIntent(String url) {
        Intent baseIntent = realIntentFactory.createUrlIntent(url);
        if (!captureIntents.get()) {
            return baseIntent;
        } else {
            return ExternalIntentActivity.createIntent(baseIntent);
        }
    }
}
