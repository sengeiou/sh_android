package com.shootr.android.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shootr.android.data.DebugMode;
import com.shootr.android.data.prefs.BooleanPreference;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.data.ApiEndpoint;
import com.shootr.android.data.prefs.StringPreference;
import com.shootr.android.service.dataservice.ShootrDataService;

import javax.inject.Singleton;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.JacksonConverter;

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

    @Provides
    RestAdapter provideRestAdapter(Endpoint endpoint, ObjectMapper objectMapper, OkHttpClient okHttpClient) {
        return new RestAdapter.Builder() //
          .setEndpoint(endpoint.getUrl() + ApiModule.API_PATH_BASE) //
          .setConverter(new JacksonConverter(objectMapper)) //
          .setClient(new OkClient(okHttpClient)) //
          .setErrorHandler(new RetrofitErrorHandler()) //
          .setLogLevel(RestAdapter.LogLevel.FULL) //
          .build();
    }
}
