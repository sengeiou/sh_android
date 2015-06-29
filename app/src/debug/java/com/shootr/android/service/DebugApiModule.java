package com.shootr.android.service;

import com.shootr.android.data.ApiEndpoint;
import com.shootr.android.data.DebugMode;
import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.data.prefs.StringPreference;
import com.shootr.android.service.dataservice.ShootrDataService;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.RestAdapter;

@Module(
        complete = false,
        library = true,
        overrides = true
)
public class DebugApiModule {

    @Provides @Singleton Endpoint provideEndpoint(@ApiEndpoint StringPreference apiEndpoint) {
        return Endpoints.newFixedEndpoint(apiEndpoint.get());
    }

    @Provides @Singleton DebugServiceAdapter provideMockServiceAdapter() {
        return new DebugServiceAdapter();
    }

    @Provides @Singleton ShootrService provideShootrService(ShootrDataService shootrDataService, DebugServiceAdapter debugServiceAdapter, @DebugMode
    BooleanPreference debugMode) {
        if (debugMode.get()) {
            return debugServiceAdapter.create(shootrDataService);
        } else {
            return shootrDataService;
        }
    }

    @Provides RestAdapter.LogLevel provideRetrofitLogLevel() {
        return RestAdapter.LogLevel.BASIC;
    }
}
