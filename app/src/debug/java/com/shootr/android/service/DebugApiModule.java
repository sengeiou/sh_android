package com.shootr.android.service;

import com.shootr.android.data.DebugMode;
import com.shootr.android.data.prefs.BooleanPreference;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.data.ApiEndpoint;
import com.shootr.android.data.prefs.StringPreference;
import com.shootr.android.service.dataservice.ShootrDataService;

import javax.inject.Singleton;

@Module(
        complete = false,
        library = true,
        overrides = true
)
public class DebugApiModule {

    @Provides @Singleton Endpoint provideEndpoint(@ApiEndpoint StringPreference apiEndpoint) {
        return Endpoints.newFixedEndpoint(apiEndpoint.get());
    }

    @Provides @Singleton MockServiceAdapter provideMockServiceAdapter() {
        return new MockServiceAdapter();
    }

    @Provides @Singleton ShootrService provideShootrService(ShootrDataService shootrDataService, MockServiceAdapter mockServiceAdapter, @DebugMode
    BooleanPreference debugMode) {
        if (debugMode.get()) {
            return mockServiceAdapter.create(shootrDataService);
        } else {
            return shootrDataService;
        }
    }
}
