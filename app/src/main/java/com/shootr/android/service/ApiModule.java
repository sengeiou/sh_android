package com.shootr.android.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shootr.android.BuildConfig;
import com.shootr.android.data.api.service.EventApiService;
import com.shootr.android.domain.repository.PhotoService;
import com.shootr.android.service.dataservice.ShootrPhotoService;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.service.dataservice.ShootrDataService;
import com.shootr.android.service.dataservice.DataServiceModule;
import javax.inject.Singleton;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.JacksonConverter;

@Module(
        injects = {
                ShootrPhotoService.class,
                PhotoService.class,
        },
        includes = DataServiceModule.class,
        complete = false,
  library = true
)
public final class ApiModule {

    public static final String PRODUCTION_ENDPOINT_URL = BuildConfig.DATA_SERVICES_ENDPOINT_BASE;
    public static final String API_PATH_BASE = "/shootr-api/rest";

    @Provides @Singleton ShootrService provideShootrService(ShootrDataService dataService) {
        return dataService;
    }

    @Provides @Singleton PhotoService providePhotoService(ShootrPhotoService photoService) {
        return photoService;
    }

    @Provides RestAdapter provideRestAdapter(Endpoint endpoint, ObjectMapper objectMapper, OkHttpClient okHttpClient) {
        return new RestAdapter.Builder() //
          .setEndpoint(endpoint.getUrl() + API_PATH_BASE) //
          .setConverter(new JacksonConverter(objectMapper)) //
          .setClient(new OkClient(okHttpClient)) //
          .setErrorHandler(new LoggableRetrofitErrorHandler()) //
          .build();
    }

    @Provides
    EventApiService provideEventApiService(RestAdapter restAdapter) {
        return restAdapter.create(EventApiService.class);
    }

    @Provides @Singleton ObjectMapper provideObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Provides @Singleton Endpoint provideEndpoint() {
        return new Endpoint() {
            @Override
            public String getUrl() {
                return PRODUCTION_ENDPOINT_URL;
            }

            @Override
            public String getName() {
                return "Production";
            }
        };
    }
}
