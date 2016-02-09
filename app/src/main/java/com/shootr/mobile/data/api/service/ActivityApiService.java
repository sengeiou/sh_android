package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.entity.ActivityApiEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import java.io.IOException;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ActivityApiService {

    @GET("/activity?includeFollowing=true&includeLinks=false") List<ActivityApiEntity> getActivityTimeline(
      @Query("types") List<String> types,
      @Query("count") Integer count,
      @Query("sinceTimestamp") Long sinceTimestamp,
      @Query("maxTimestamp") Long maxTimestamp,
      @Query("locale") String locale)
    throws ApiException, IOException;

    @GET("/activity/{id}")
    ActivityApiEntity getActivity(@Path("id") String id) throws ApiException, IOException;
}
