package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.DiscoveredEntity;
import java.io.IOException;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Query;

public interface DiscoveredApiService {

  @GET("/discover?includeLinks=false&includeEmbed=true")
  List<DiscoveredEntity> getDiscoveredList(@Query("locale") String locale,
      @Query("streamReadWriteMode") String[] streamTypes,
      @Query("types") String[] discoveredTypes) throws ApiException, IOException;
}
