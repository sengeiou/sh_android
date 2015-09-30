package com.shootr.android.service;

import android.content.SharedPreferences;
import com.shootr.android.data.ApiEndpoint;
import com.shootr.android.data.DebugMode;
import com.shootr.android.data.api.service.ActivityApiService;
import com.shootr.android.data.api.service.AuthApiService;
import com.shootr.android.data.api.service.ChangePasswordApiService;
import com.shootr.android.data.api.service.DeviceApiService;
import com.shootr.android.data.api.service.FavoriteApiService;
import com.shootr.android.data.api.service.ResetPasswordApiService;
import com.shootr.android.data.api.service.ShotApiService;
import com.shootr.android.data.api.service.StreamApiService;
import com.shootr.android.data.api.service.UserApiService;
import com.shootr.android.data.api.service.VideoApiService;
import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.data.prefs.StringPreference;
import com.shootr.android.service.dataservice.ShootrDataService;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.MockRestAdapter;
import retrofit.RestAdapter;
import retrofit.android.AndroidMockValuePersistence;

@Module(
        complete = false,
        library = true,
        overrides = true
)
public class DebugApiModule {

    public static final String TEST_ENDPOINT_URL = "http://tst-api.shootr.com/v1";
    public static final String TEST_SSL_ENDPOINT_URL = "https://tst-api.shootr.com/v1";

    @Provides @Singleton Endpoint provideEndpoint(@ApiEndpoint StringPreference apiEndpoint) {
        return Endpoints.newFixedEndpoint(apiEndpoint.get());
    }

    @Provides @Singleton DebugServiceAdapter provideMockServiceAdapter() {
        return new DebugServiceAdapter();
    }

    @Provides @Singleton MockRestAdapter provideMockRestAdapter(RestAdapter restAdapter, SharedPreferences preferences) {
        MockRestAdapter mockRestAdapter = MockRestAdapter.from(restAdapter);
        AndroidMockValuePersistence.install(mockRestAdapter, preferences);
        return mockRestAdapter;
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
    AuthApiService provideAuthApiService(RestAdapter restAdapter, MockRestAdapter mockRestAdapter, @DebugMode
    BooleanPreference debugMode) {
        return mockOrRealService(AuthApiService.class, restAdapter, mockRestAdapter, debugMode.get());
    }

    @Provides
    ResetPasswordApiService provideResetPasswordApiService(RestAdapter restAdapter, MockRestAdapter mockRestAdapter, @DebugMode
    BooleanPreference debugMode) {
        return mockOrRealService(ResetPasswordApiService.class, restAdapter, mockRestAdapter, debugMode.get());
    }

    @Provides StreamApiService provideStreamApiService(RestAdapter restAdapter, MockRestAdapter mockRestAdapter, @DebugMode
    BooleanPreference debugMode) {
      return mockOrRealService(StreamApiService.class, restAdapter, mockRestAdapter, debugMode.get());
    }

    @Provides UserApiService provideUserApiService(RestAdapter restAdapter,
      MockRestAdapter mockRestAdapter,
      @DebugMode BooleanPreference debugMode) {
        return mockOrRealService(UserApiService.class, restAdapter, mockRestAdapter, debugMode.get());
    }

    @Provides
    ShotApiService provideShotApiService(RestAdapter restAdapter, MockRestAdapter mockRestAdapter, @DebugMode
    BooleanPreference debugMode) {
      return mockOrRealService(ShotApiService.class, restAdapter, mockRestAdapter, debugMode.get());
    }

    @Provides
    VideoApiService provideVideoApiService(RestAdapter restAdapter, MockRestAdapter mockRestAdapter, @DebugMode
    BooleanPreference debugMode) {
      return mockOrRealService(VideoApiService.class, restAdapter, mockRestAdapter, debugMode.get());
    }

    @Provides
    FavoriteApiService provideFavoriteApiService(RestAdapter restAdapter, MockRestAdapter mockRestAdapter, @DebugMode
    BooleanPreference debugMode) {
      return mockOrRealService(FavoriteApiService.class, restAdapter, mockRestAdapter, debugMode.get());
    }

    @Provides
    ActivityApiService provideActivityApiService(RestAdapter restAdapter, MockRestAdapter mockRestAdapter, @DebugMode
    BooleanPreference debugMode) {
      return mockOrRealService(ActivityApiService.class, restAdapter, mockRestAdapter, debugMode.get());
    }

    @Provides ChangePasswordApiService provideChangePasswordApiService(RestAdapter restAdapter, MockRestAdapter mockRestAdapter, @DebugMode
    BooleanPreference debugMode) {
        return mockOrRealService(ChangePasswordApiService.class, restAdapter, mockRestAdapter, debugMode.get());
    }

    @Provides DeviceApiService provideDeviceApiService(RestAdapter restAdapter, MockRestAdapter mockRestAdapter, @DebugMode
    BooleanPreference debugMode) {
        return mockOrRealService(DeviceApiService.class, restAdapter, mockRestAdapter, debugMode.get());
    }

    private <T> T mockOrRealService(Class<T> serviceClass,
      RestAdapter restAdapter,
      MockRestAdapter mockRestAdapter,
      boolean isDebug) {
        T realService = restAdapter.create(serviceClass);
        if (isDebug) {
            return mockRestAdapter.create(serviceClass, realService);
        } else {
            return realService;
        }
    }

    @Provides RestAdapter.LogLevel provideRetrofitLogLevel() {
        return RestAdapter.LogLevel.BASIC;
    }
}
