package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.UserSettingsEntity;
import java.io.IOException;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;

public interface UserSettingsApiService {

  @GET("/pushSetting") List<UserSettingsEntity> getUserSettings() throws IOException, ApiException;

  @PUT("/pushSetting") Response setUserSettings(
      @Body UserSettingsEntity userSettingsEntity) throws IOException, ApiException;

}
