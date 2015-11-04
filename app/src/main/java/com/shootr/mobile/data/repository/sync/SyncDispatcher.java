package com.shootr.mobile.data.repository.sync;

public interface SyncDispatcher {

    void notifyNeedsSync(SyncableRepository syncableRepository);

    void triggerSync();

}
