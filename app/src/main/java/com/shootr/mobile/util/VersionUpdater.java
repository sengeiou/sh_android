package com.shootr.mobile.util;

import com.shootr.mobile.data.bus.VersionOutdatedError;
import com.shootr.mobile.domain.bus.BusPublisher;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class VersionUpdater {

    private final BusPublisher busPublisher;

    @Inject public VersionUpdater(BusPublisher busPublisher) {
        this.busPublisher = busPublisher;
    }

    public void notifyUpdateRequired() {
        busPublisher.post(new VersionOutdatedError.Event());
    }
}
