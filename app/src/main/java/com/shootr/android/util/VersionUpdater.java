package com.shootr.android.util;

import android.app.Application;
import com.shootr.android.data.bus.BusPublisher;
import com.shootr.android.data.bus.UpdateWarning;
import com.shootr.android.data.prefs.LastVersionNotCompatible;
import com.shootr.android.data.prefs.LongPreference;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class VersionUpdater {

    private final BusPublisher busPublisher;
    private final LongPreference notCompatibleVersion;
    private final long currentVersion;

    @Inject public VersionUpdater(Application application, BusPublisher busPublisher, @LastVersionNotCompatible
    LongPreference notCompatibleVersion) {
        this.busPublisher = busPublisher;
        this.notCompatibleVersion = notCompatibleVersion;
        currentVersion = VersionUtils.getVersionCode(application);
    }

    public void checkVersionCompatible() {
        if (currentVersion <= notCompatibleVersion.get()) {
            notifyUpdateRequired();
        }
    }

    public void notifyUpdateRequired() {
        notCompatibleVersion.set(currentVersion);
        busPublisher.post(new UpdateWarning.Event());
    }
}
