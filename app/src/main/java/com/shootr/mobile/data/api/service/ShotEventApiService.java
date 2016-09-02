package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.ShotEventEntity;
import java.io.IOException;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ShotEventApiService {

  @POST("/media/upload/event") Response sendShotEvents(@Body List<ShotEventEntity> events)
      throws ApiException, IOException;

}