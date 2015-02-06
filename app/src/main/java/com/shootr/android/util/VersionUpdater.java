package com.shootr.android.util;

import com.shootr.android.data.bus.BusPublisher;
import com.shootr.android.data.bus.UpdateWarning;
import javax.inject.Inject;

public class VersionUpdater {

    private final BusPublisher busPublisher;

    @Inject public VersionUpdater(BusPublisher busPublisher) {
        this.busPublisher = busPublisher;
    }

    public void notifyUpdateRequired() {
        busPublisher.post(new UpdateWarning.Event());
    }
}
