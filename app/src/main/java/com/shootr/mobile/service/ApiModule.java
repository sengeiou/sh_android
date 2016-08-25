package com.shootr.mobile.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.shootr.mobile.BuildConfig;
import com.shootr.mobile.data.api.service.ActivityApiService;
import com.shootr.mobile.data.api.service.AuthApiService;
import com.shootr.mobile.data.api.service.ChangePasswordApiService;
import com.shootr.mobile.data.api.service.ContributorApiService;
import com.shootr.mobile.data.api.service.DeviceApiService;
import com.shootr.mobile.data.api.service.DiscoveredApiService;
import com.shootr.mobile.data.api.service.FavoriteApiService;
import com.shootr.mobile.data.api.service.NicerApiService;
import com.shootr.mobile.data.api.service.PollApiService;
import com.shootr.mobile.data.api.service.ResetPasswordApiService;
import com.shootr.mobile.data.api.service.ShotApiService;
import com.shootr.mobile.data.api.service.ShotEventApiService;
import com.shootr.mobile.data.api.service.StreamApiService;
import com.shootr.mobile.data.api.service.UserApiService;
import com.shootr.mobile.data.api.service.UserSettingsApiService;
import com.shootr.mobile.data.api.service.VideoApiService;
import com.shootr.mobile.domain.repository.PhotoService;
import com.sloydev.jsonadapters.JsonAdapter;
import com.sloydev.jsonadapters.gson.GsonAdapter;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import java.lang.reflect.Type;
import java.util.Date;
import javax.inject.Singleton;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import timber.log.Timber;

@Module(
    injects = {
        ShootrPhotoService.class, PhotoService.class,
    },
    complete = false,
    library = true) public final class ApiModule {

  public static final String PRODUCTION_ENDPOINT_URL = "https://api.shootr.com/v1";
  public static final String PRE_PRODUCTION_ENDPOINT_URL = "https://pre-api.shootr.com/v1";

  @Provides @Singleton PhotoService providePhotoService(ShootrPhotoService photoService) {
    return photoService;
  }

  @Provides RestAdapter.LogLevel provideRetrofitLogLevel() {
    return RestAdapter.LogLevel.NONE;
  }

  @Provides RestAdapter provideRestAdapter(Endpoint endpoint, OkHttpClient okHttpClient, Gson gson,
      RetrofitErrorHandler errorHandler, RestAdapter.LogLevel logLevel) {
    return new RestAdapter.Builder() //
        .setEndpoint(endpoint.getUrl()) //
        .setClient(new OkClient(okHttpClient)) //
        .setConverter(new GsonConverter(gson)).setErrorHandler(errorHandler) //
        .setLogLevel(logLevel).setLog(new RestAdapter.Log() {
          @Override public void log(String message) {
            Timber.tag("Retrofit");
            Timber.d(message);
          }
        }).build();
  }

  @Provides AuthApiService provideAuthApiService(RestAdapter restAdapter) {
    return restAdapter.create(AuthApiService.class);
  }

  @Provides ResetPasswordApiService provideResetPasswordApiService(RestAdapter restAdapter) {
    return restAdapter.create(ResetPasswordApiService.class);
  }

  @Provides StreamApiService provideStreamApiService(RestAdapter restAdapter) {
    return restAdapter.create(StreamApiService.class);
  }

  @Provides UserApiService provideUserApiService(RestAdapter restAdapter) {
    return restAdapter.create(UserApiService.class);
  }

  @Provides UserSettingsApiService provideUserSettingsApiService(RestAdapter restAdapter) {
    return restAdapter.create(UserSettingsApiService.class);
  }

  @Provides ShotApiService provideShotApiService(RestAdapter restAdapter) {
    return restAdapter.create(ShotApiService.class);
  }

  @Provides VideoApiService provideVideoApiService(RestAdapter restAdapter) {
    return restAdapter.create(VideoApiService.class);
  }

  @Provides FavoriteApiService provideFavoriteApiService(RestAdapter restAdapter) {
    return restAdapter.create(FavoriteApiService.class);
  }

  @Provides ActivityApiService provideActivityApiService(RestAdapter restAdapter) {
    return restAdapter.create(ActivityApiService.class);
  }

  @Provides ChangePasswordApiService provideChangePasswordApiService(RestAdapter restAdapter) {
    return restAdapter.create(ChangePasswordApiService.class);
  }

  @Provides DeviceApiService provideDeviceApiService(RestAdapter restAdapter) {
    return restAdapter.create(DeviceApiService.class);
  }

  @Provides @Singleton Gson provideGson() {
    return new GsonBuilder() //
        .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
          @Override public Date deserialize(JsonElement json, Type typeOfT,
              JsonDeserializationContext context) throws JsonParseException {
            return new Date(json.getAsJsonPrimitive().getAsLong());
          }
        }).registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
          @Override
          public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTime());
          }
        }).create();
  }

  @Provides @Singleton JsonAdapter provideJsonAdapter(Gson gson) {
    return new GsonAdapter(gson);
  }

  @Provides @Singleton Endpoint provideEndpoint() {
    return new Endpoint() {
      @Override public String getUrl() {
        return BuildConfig.USE_PRE_PRODUCTION ? PRE_PRODUCTION_ENDPOINT_URL
            : PRODUCTION_ENDPOINT_URL;
      }

      @Override public String getName() {
        return "Production";
      }
    };
  }

  @Provides NicerApiService provideNicerApiService(RestAdapter restAdapter) {
    return restAdapter.create(NicerApiService.class);
  }

  @Provides ContributorApiService provideContributorApiService(RestAdapter restAdapter) {
    return restAdapter.create(ContributorApiService.class);
  }

  @Provides PollApiService providePollApiService(RestAdapter restAdapter) {
    return restAdapter.create(PollApiService.class);
  }

  @Provides DiscoveredApiService provideDiscoveredApiService(RestAdapter restAdapter) {
    return restAdapter.create(DiscoveredApiService.class);
  }

  @Provides ShotEventApiService provideShotEventApiService(RestAdapter restAdapter) {
    return restAdapter.create(ShotEventApiService.class);
  }
}
