package com.shootr.android.data.api.service;

import com.shootr.android.data.api.entity.ShotApiEntity;
import com.shootr.android.data.api.exception.ApiException;
import java.io.IOException;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ShotApiService {

    @GET("/shots/streamTimeline?includeLinks=false")
    List<ShotApiEntity> getStreamTimeline(@Query("idStream") String stream, @Query("count") Integer count,
      @Query("sinceTimestamp") Long sinceTimestamp, @Query("maxTimestamp") Long maxTimestamp,
      @Query("includeNice") Boolean includeNice, @Query("maxNice") Integer maxNice) throws IOException;

    @GET("/shots/{idShot}/?includeLinks=false")
    ShotApiEntity getShot(@Path("idShot") String idShot) throws ApiException, IOException;

    @GET("/shots/{idShot}/?includeLinks=false&includeReplies=true")
    ShotApiEntity getShotWithReplies(@Path("idShot") String idShot) throws ApiException, IOException;

    @GET("/shots/{idShot}/?includeLinks=false&includeReplies=true&includeParent=true")
    ShotApiEntity getShotDetail(@Path("idShot") String idShot) throws ApiException, IOException;

    @GET("/shots/user/{idUser}/?includeLinks=false")
    List<ShotApiEntity> getShotsFromUser(@Path("idUser") String idUser,
      @Query("count") Integer limit,
      @Query("types") String[] types) throws ApiException, IOException;
}
