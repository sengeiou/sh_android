package com.shootr.mobile;

import com.facebook.stetho.Stetho;
import com.shootr.mobile.stetho.ShootrDumperPluginsProvider;
import javax.inject.Inject;

public class ShootrDebugApplication extends ShootrApplication {

    @Inject ShootrDumperPluginsProvider dumperPluginsProvider;

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initialize(Stetho.newInitializerBuilder(this)
          .enableDumpapp(dumperPluginsProvider)
          .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
          .build());
    }
}
