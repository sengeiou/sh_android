package com.shootr.android.data.api.service;

import com.shootr.android.data.api.entity.ShotApiEntity;
import com.shootr.android.data.api.exception.ApiException;
import java.io.IOException;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ShotApiService {

    @GET("/shots/streamTimeline?includeLinks=false&includeAll=true")
    List<ShotApiEntity> getStreamTimeline(@Query("idStream") String stream, @Query("count") Integer count,
      @Query("sinceTimestamp") Long sinceTimestamp, @Query("maxTimestamp") Long maxTimestamp) throws ApiException, IOException;

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

    @GET("/shots/user/{idUser}/?includeLinks=false")
    List<ShotApiEntity> getAllShotsFromUser(@Path("idUser") String userId) throws ApiException, IOException;

    @GET("/shots/user/{idUser}/?includeLinks=false")
    List<ShotApiEntity> getAllShotsFromUserWithMaxDate(@Path("idUser") String userId,
      @Query("maxTimestamp") Long maxDate) throws ApiException, IOException;

    @PUT("/shots/{idShot}/nice")
    Response markNice(@Path("idShot") String idShot) throws ApiException, IOException;

    @DELETE("/shots/{idShot}/nice")
    Response unmarkNice(@Path("idShot") String idShot) throws ApiException, IOException;

    @GET("/streams/{idStream}/media?includeLinks=false")
    List<ShotApiEntity> getMediaShots(@Path("idStream") String idStream) throws ApiException, IOException;

    @POST("/shots/{idShot}/share")
    Response shareShot(@Path("idShot") String idShot) throws ApiException, IOException;
}
