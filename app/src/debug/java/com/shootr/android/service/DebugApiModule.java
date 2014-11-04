package com.shootr.android.service;

import dagger.Module;
import dagger.Provides;
import com.shootr.android.data.ApiEndpoint;
import com.shootr.android.data.IsMockMode;
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

    @Provides @Singleton ShootrMockService provideMockShootrService() {
        return new ShootrMockService();
    }

    @Provides @Singleton ShootrService provideShootrService(ShootrDataService shootrDataService, @IsMockMode boolean isMockMode) {
        if (isMockMode) {
            return new ShootrMockService();
        } else {
            return shootrDataService;
        }
    }
}
