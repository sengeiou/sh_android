package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.SearchItemEntity;
import java.io.IOException;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SearchApiService {

  @GET("/search/") List<SearchItemEntity> searchItems(@Query("query") String query,
      @Query("types") String[] types) throws ApiException, IOException;
}
