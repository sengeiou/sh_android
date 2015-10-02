package com.shootr.android.stetho;

import android.content.Context;
import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.shootr.android.data.dagger.ApplicationContext;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShootrDumperPluginsProvider implements DumperPluginsProvider {

    private final Context context;
    private final FakeWatchUpdateRequestPlugin fakeWatchUpdatePlugin;
    private final SyncDispacherPlugin syncDispacherPlugin;

    @Inject
    public ShootrDumperPluginsProvider(@ApplicationContext Context context,
      FakeWatchUpdateRequestPlugin fakeWatchUpdatePlugin, SyncDispacherPlugin syncDispacherPlugin) {
        this.context = context;
        this.fakeWatchUpdatePlugin = fakeWatchUpdatePlugin;
        this.syncDispacherPlugin = syncDispacherPlugin;
    }

    @Override
    public Iterable<DumperPlugin> get() {
        List<DumperPlugin> plugins = new ArrayList<>();
        for (DumperPlugin defaultPlugin : Stetho.defaultDumperPluginsProvider(context).get()) {
            plugins.add(defaultPlugin);
        }
        plugins.add(fakeWatchUpdatePlugin);
        plugins.add(syncDispacherPlugin);
        return plugins;
    }
}
