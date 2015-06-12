package com.shootr.android.data.api.service;

import com.shootr.android.data.api.entity.ShotApiEntity;
import com.shootr.android.data.entity.EventEntity;
import java.io.IOException;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ShotApiService {

    @GET("/shots/boxTimeline?includeLinks=false")
    List<ShotApiEntity> getEventTimeline(@Query("idEvent") String event,
      @Query("count") Integer count,
      @Query("sinceTimestamp") Long sinceTimestamp,
      @Query("maxTimestamp") Long maxTimestamp,
      @Query("includeNice") Boolean includeNice,
      @Query("maxNice") Integer maxNice,
      @Query("me") String me) throws IOException;
}