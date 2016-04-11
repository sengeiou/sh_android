package com.shootr.mobile.data.repository.sync;

import java.util.Collection;
import javax.inject.Inject;
import timber.log.Timber;

public class SyncDispatcherImpl implements SyncDispatcher {

    private final Collection<SyncableRepository> subscribedSyncableRepositories;
    private boolean needsSync = true; //TODO initial check

    @Inject public SyncDispatcherImpl(SyncableRepositoriesProvider syncableRepositoriesProvider) {
        subscribedSyncableRepositories = syncableRepositoriesProvider.getSyncableRepositories();
    }

    @Override public void notifyNeedsSync(SyncableRepository syncableRepository) {
        Timber.d("Sync scheduled from repository: %s", syncableRepository.toString());
        needsSync = true;
    }

    @Override public void triggerSync() {
        if (needsSync) {
            Timber.d("Sync triggered. Needs sync -> Performing");
            performSync();
        } else {
            Timber.d("Sync triggered. No need to sync -> Ignoring");
        }
    }

    private void performSync() {
        boolean anySyncFailed = false;
        for (SyncableRepository syncableRepository : subscribedSyncableRepositories) {
            try {
                Timber.d("Dispatching sync in %s", syncableRepository.getClass().getSimpleName());
                syncableRepository.dispatchSync();
            } catch (Exception e) {
                Timber.e(e,
                  "Synchronization failed for repository %s. Will retry with in nex trigger.",
                  syncableRepository.toString());
                anySyncFailed = true;
            }
        }
        needsSync = anySyncFailed;
    }
}
