package com.shootr.android.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shootr.android.data.api.service.ActivityApiService;
import com.shootr.android.data.api.service.AuthApiService;
import com.shootr.android.data.api.service.EventApiService;
import com.shootr.android.data.api.service.FavoriteApiService;
import com.shootr.android.data.api.service.ResetPasswordApiService;
import com.shootr.android.data.api.service.ShotApiService;
import com.shootr.android.data.api.service.VideoApiService;
import com.shootr.android.domain.repository.PhotoService;
import com.shootr.android.service.dataservice.DataServiceModule;
import com.shootr.android.service.dataservice.ShootrDataService;
import com.shootr.android.service.dataservice.ShootrPhotoService;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
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

    public static final String PRODUCTION_ENDPOINT_URL = "http://api.shootr.com/v1";
    public static final String TEST_ENDPOINT_URL = "http://tst.api.shootr.com/v1";
    public static final String TEST_SSL_ENDPOINT_URL = "https://tst.api.shootr.com/v1";

    @Provides @Singleton ShootrService provideShootrService(ShootrDataService dataService) {
        return dataService;
    }

    @Provides @Singleton PhotoService providePhotoService(ShootrPhotoService photoService) {
        return photoService;
    }

    @Provides RestAdapter.LogLevel provideRetrofitLogLevel() {
        return RestAdapter.LogLevel.NONE;
    }

    @Provides RestAdapter provideRestAdapter(Endpoint endpoint, ObjectMapper objectMapper, OkHttpClient okHttpClient,
      RetrofitErrorHandler errorHandler, RestAdapter.LogLevel logLevel) {
        return new RestAdapter.Builder() //
          .setEndpoint(endpoint.getUrl()) //
          .setConverter(new JacksonConverter(objectMapper)) //
          .setClient(new OkClient(okHttpClient)) //
          .setErrorHandler(errorHandler) //
          .setLogLevel(logLevel)
          .build();
    }

    @Provides
    AuthApiService provideAuthApiService(RestAdapter restAdapter) {
        return restAdapter.create(AuthApiService.class);
    }

    @Provides ResetPasswordApiService provideResetPasswordApiService(RestAdapter restAdapter) {
        return restAdapter.create(ResetPasswordApiService.class);
    }

    @Provides
    EventApiService provideEventApiService(RestAdapter restAdapter) {
        return restAdapter.create(EventApiService.class);
    }

    @Provides
    ShotApiService provideShotApiService(RestAdapter restAdapter) {
        return restAdapter.create(ShotApiService.class);
    }

    @Provides VideoApiService provideVideoApiService(RestAdapter restAdapter) {
        return restAdapter.create(VideoApiService.class);
    }

    @Provides
    FavoriteApiService provideFavoriteApiService(RestAdapter restAdapter) {
        return restAdapter.create(FavoriteApiService.class);
    }

    @Provides ActivityApiService provideActivityApiService(RestAdapter restAdapter) {
        return restAdapter.create(ActivityApiService.class);
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
