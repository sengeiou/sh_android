package com.shootr.android.util;

import com.shootr.android.data.bus.VersionOutdatedError;
import com.shootr.android.domain.bus.BusPublisher;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class VersionUpdater {

    private final BusPublisher busPublisher;

    @Inject public VersionUpdater(BusPublisher busPublisher) {
        this.busPublisher = busPublisher;
    }

    public void notifyUpdateRequired() {
        busPublisher.post(new VersionOutdatedError.Event());
    }
}
