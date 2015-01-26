package com.shootr.android.data.repository.sync;

public interface SyncDispatcher {

    void notifyNeedsSync(SyncableRepository syncableRepository);

    void triggerSync();

}
