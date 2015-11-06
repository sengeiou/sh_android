package com.shootr.mobile.data.repository.sync;

import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SyncTrigger {

    private final Lazy<SyncDispatcher> lazySyncDispatcher;

    @Inject public SyncTrigger(Lazy<SyncDispatcher> lazySyncDispatcher) {
        this.lazySyncDispatcher = lazySyncDispatcher;
    }

    public void triggerSync() {
        lazySyncDispatcher.get().triggerSync();
    }

    public void notifyNeedsSync(SyncableRepository syncRepository) {
        lazySyncDispatcher.get().notifyNeedsSync(syncRepository);
    }

}
