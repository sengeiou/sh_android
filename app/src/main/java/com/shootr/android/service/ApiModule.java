package com.shootr.android.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.shootr.android.data.api.service.ActivityApiService;
import com.shootr.android.data.api.service.AuthApiService;
import com.shootr.android.data.api.service.ChangePasswordApiService;
import com.shootr.android.data.api.service.FavoriteApiService;
import com.shootr.android.data.api.service.ResetPasswordApiService;
import com.shootr.android.data.api.service.ShotApiService;
import com.shootr.android.data.api.service.StreamApiService;
import com.shootr.android.data.api.service.UserApiService;
import com.shootr.android.data.api.service.VideoApiService;
import com.shootr.android.domain.repository.PhotoService;
import com.shootr.android.service.dataservice.DataServiceModule;
import com.shootr.android.service.dataservice.ShootrDataService;
import com.shootr.android.service.dataservice.ShootrPhotoService;
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

    public static final String PRODUCTION_ENDPOINT_URL = "https://api.shootr.com/v1";

    @Provides @Singleton ShootrService provideShootrService(ShootrDataService dataService) {
        return dataService;
    }

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
          .setConverter(new GsonConverter(gson))
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

    @Provides StreamApiService provideStreamApiService(RestAdapter restAdapter) {
        return restAdapter.create(StreamApiService.class);
    }

    @Provides UserApiService provideUserApiService(RestAdapter restAdapter) {
        return restAdapter.create(UserApiService.class);
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

    @Provides ChangePasswordApiService provideChangePasswordApiService(RestAdapter restAdapter) {
        return restAdapter.create(ChangePasswordApiService.class);
    }

    @Provides @Singleton Gson provideGson() {
        return new GsonBuilder() //
          .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
              @Override
              public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
                  return new Date(json.getAsJsonPrimitive().getAsLong());
              }
          })
          .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
              @Override
              public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                  return new JsonPrimitive(src.getTime());
              }
          })
          .serializeNulls() // TODO remove when dataservice is GONE
          .create();
    }

    @Provides @Singleton JsonAdapter provideJsonAdapter(Gson gson) {
        return new GsonAdapter(gson);
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
