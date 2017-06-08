package com.shootr.mobile.stetho;

import com.facebook.stetho.dumpapp.DumpException;
import com.facebook.stetho.dumpapp.DumperContext;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.shootr.mobile.data.repository.remote.SyncUserRepository;
import com.shootr.mobile.domain.bus.WatchUpdateRequest;
import java.io.PrintStream;
import javax.inject.Inject;

public class FakeWatchUpdateRequestPlugin implements DumperPlugin {

    private final SyncUserRepository syncUserRepository;

    @Inject public FakeWatchUpdateRequestPlugin(SyncUserRepository syncUserRepository) {
        this.syncUserRepository = syncUserRepository;
    }

    @Override public String getName() {
        return "fakeWatchUpdate";
    }

    @Override public void dump(DumperContext dumpContext) throws DumpException {
        PrintStream writer = dumpContext.getStdout();
        writer.println("Forcing watch update on SyncuserRepository...");
        syncUserRepository.onWatchUpdateRequest(new WatchUpdateRequest.Event(false));
        writer.println("Watch update done.");
    }
}
