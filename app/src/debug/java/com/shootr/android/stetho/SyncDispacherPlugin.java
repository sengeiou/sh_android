package com.shootr.android.stetho;

import com.facebook.stetho.dumpapp.DumpException;
import com.facebook.stetho.dumpapp.DumperContext;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.shootr.android.data.repository.sync.SyncDispatcher;
import javax.inject.Inject;
import timber.log.Timber;

public class SyncDispacherPlugin implements DumperPlugin {

    private final SyncDispatcher syncDispatcher;

    @Inject public SyncDispacherPlugin(SyncDispatcher syncDispatcher) {
        this.syncDispatcher = syncDispatcher;
    }

    @Override
    public String getName() {
        return "dispatchSync";
    }

    @Override
    public void dump(DumperContext dumpContext) throws DumpException {
        StethoTree stethoTree = new StethoTree(dumpContext.getStdout());
        Timber.plant(stethoTree);
        syncDispatcher.triggerSync();
        Timber.uproot(stethoTree);
    }
}
