package com.shootr.android.data.repository.sync;

import com.shootr.android.data.repository.remote.SyncWatchRepository;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton
public class SyncTrigger {

    private final Lazy<SyncDispatcher> lazySyncDispatcher;

    @Inject public SyncTrigger(Lazy<SyncDispatcher> lazySyncDispatcher) {
        this.lazySyncDispatcher = lazySyncDispatcher;
    }

    public void triggerSync() {
        lazySyncDispatcher.get().triggerSync();
    }

    public void notifyNeedsSync(SyncWatchRepository syncRepository) {
        lazySyncDispatcher.get().notifyNeedsSync(syncRepository);
    }

}
