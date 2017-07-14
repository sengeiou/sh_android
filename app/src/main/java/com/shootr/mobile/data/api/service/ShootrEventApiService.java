package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.entity.FollowsEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.ShootrEventEntity;
import java.io.IOException;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface ShootrEventApiService {

  @POST("/media/upload/event") Response sendShootrEvents(@Body List<ShootrEventEntity> events)
      throws ApiException, IOException;


  @GET("/userActivity") FollowsEntity getRecentList()
      throws IOException, ApiException;

}